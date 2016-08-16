package com.goldenratio.commonweal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyFragmentPagerAdapter;
import com.goldenratio.commonweal.receiver.NetworkReceiver;
import com.goldenratio.commonweal.ui.fragment.DynamicFragment;
import com.goldenratio.commonweal.ui.fragment.GoodFragment;
import com.goldenratio.commonweal.ui.fragment.HelpFragment;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.b.BP;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Kiuber on 2016/6/6.
 */
public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, NetworkReceiver.NetEventHandle, ViewPager.OnPageChangeListener {

    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.rb_help)
    RadioButton mRbDefault;
    @BindView(R.id.rb_good)
    RadioButton mRbGood;
    @BindView(R.id.rb_dynamic)
    RadioButton mRbDynamic;
    @BindView(R.id.rb_my)
    RadioButton mRbMy;
    @BindView(R.id.rg_tabs)
    RadioGroup mRgTabs;
    @BindView(R.id.fl_post)
    ImageView mSend;

    private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
    private long exitTime = 0;

    private List<Fragment> mFragmentList;

    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private final int PAGE_THREE = 2;
    private final int PAGE_FOUR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        NetworkReceiver.ehList.add(this);
        //检测网络状态
        new NetworkReceiver().onReceive(getApplicationContext(), null);

        String libName = "bmob"; // 库名, 注意没有前缀lib和后缀.so
        System.loadLibrary(libName);

        //初始化Bmob
        Bmob.initialize(MainActivity.this, "727a409235aab18ae7b1e1f3933c9a64");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);
        BP.init(MainActivity.this,"727a409235aab18ae7b1e1f3933c9a64");

        mFragmentList = new ArrayList<Fragment>();

        mFragmentList.add(new HelpFragment());
        mFragmentList.add(new GoodFragment());
        mFragmentList.add(new DynamicFragment());
        mFragmentList.add(new MyFragment());

        mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mRgTabs.setOnCheckedChangeListener(this);
        mRbDefault.setChecked(true);

        mVpContent.setAdapter(mMyFragmentPagerAdapter);
        mVpContent.setCurrentItem(0);
        mVpContent.addOnPageChangeListener(this);
        mVpContent.setOffscreenPageLimit(4);  //设置是适配器缓存fragment数
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_help:// 项目
                mVpContent.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_good:// 物品
                mVpContent.setCurrentItem(PAGE_TWO);
                break;
            case R.id.rb_dynamic:// 动态
                mVpContent.setCurrentItem(PAGE_THREE);
                break;
            case R.id.rb_my:// 我
                mVpContent.setCurrentItem(PAGE_FOUR);
                break;
            default:
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            //滑动已完成
            switch (mVpContent.getCurrentItem()) {
                case PAGE_ONE:
                    mRbDefault.setChecked(true);
                    break;
                case PAGE_TWO:
                    mRbGood.setChecked(true);
                    break;
                case PAGE_THREE:
                    mRbDynamic.setChecked(true);
                    break;
                case PAGE_FOUR:
                    mRbMy.setChecked(true);
                    break;
            }
        }
    }

    // 监听返回键，然后退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //      finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkReceiver.ehList.remove(this);
    }


    boolean x = false;  //网络是否连接标识符

    //网络改变时检测网络状态，并提示用户
    @Override
    public void netState(NetworkReceiver.NetState netCode) {
        switch (netCode) {
            case NET_NO:
                Toast.makeText(this, "亲，没有网络哟", Toast.LENGTH_SHORT).show();
                x = true;
                break;
            case NET_2G:
            case NET_3G:
            case NET_4G:
                Toast.makeText(getApplicationContext(), "当前处于2G/3G/4G网络，请注意您的网络流量", Toast.LENGTH_SHORT).show();
                break;
            case NET_WIFI:
                if (x) {
                    Toast.makeText(MainActivity.this, "网络连接已恢复", Toast.LENGTH_SHORT).show();
                    x = false;
                }
                break;
            case NET_UNKNOWN:
                Toast.makeText(this, "未知网络", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "不知道什么情况~>_<~", Toast.LENGTH_SHORT).show();
                break;
        }
        Log.d("net00", netCode + "");
    }


    @OnClick(R.id.fl_post)
    public void onClick() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }
}

