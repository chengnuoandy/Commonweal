package com.goldenratio.commonweal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.dao.entity.User;
import com.goldenratio.commonweal.receiver.NetworkReceiver;
import com.goldenratio.commonweal.ui.fragment.DynamicFragment;
import com.goldenratio.commonweal.ui.fragment.GoodsFragment;
import com.goldenratio.commonweal.ui.fragment.HelpFragment;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import cn.bmob.v3.Bmob;

/**
 * Created by Kiuber on 2016/6/6.
 */
public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, NetworkReceiver.NetEventHandle {

    private FragmentManager mFmMain;// 管理fragment的类
    private RadioGroup mRgTabs;
    private RadioButton mRbDefalut;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkReceiver.ehList.add(this);

        //检测网络状态
        new NetworkReceiver().onReceive(getApplicationContext(), null);

        String libName = "bmob"; // 库名, 注意没有前缀lib和后缀.so
        System.loadLibrary(libName);
        //初始化Bmob
        Bmob.initialize(this, "727a409235aab18ae7b1e1f3933c9a64");
        // 初始化fragmentManager
        mFmMain = getSupportFragmentManager();
        // 设置进入app时默认选中
        mRgTabs = (RadioGroup) findViewById(R.id.rg_tabs);
        mRgTabs.setOnCheckedChangeListener(this);
        mRbDefalut = (RadioButton) findViewById(R.id.rb_help);
        mRbDefalut.setChecked(true);
        // 切换不同的fragment
        changeFragment(new HelpFragment(), false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_help:// 项目
                changeFragment(new HelpFragment(), true);
                break;
            case R.id.rb_good:// 物品
                changeFragment(new GoodsFragment(), true);
                break;
            case R.id.rb_dynamic:// 动态
                changeFragment(new DynamicFragment(), true);
                break;
            case R.id.rb_my:// 我
                UserDao userDao = new UserDao(MainActivity.this);
                changeFragment(new MyFragment(), true);
                break;
            default:
                break;
        }
    }

    // 封装切换不同的fragment
    public void changeFragment(Fragment fragment, boolean isInit) {
        // 开启事务
        FragmentTransaction transaction = mFmMain.beginTransaction();
        transaction.replace(R.id.fl_content, fragment);
        if (!isInit) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
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
    protected void onPause() {
        super.onPause();
        NetworkReceiver.ehList.remove(this);
    }

    //网络改变时检测网络状态，并提示用户
    @Override
    public void netState(NetworkReceiver.NetState netCode) {
        switch (netCode) {
            case NET_NO:
                Toast.makeText(this, "亲，没有网络哟", Toast.LENGTH_SHORT).show();
                break;
            case NET_2G:
            case NET_3G:
            case NET_4G:
                Toast.makeText(getApplicationContext(), "当前处于2G/3G/4G网络，请注意您的网络流量", Toast.LENGTH_SHORT).show();
                break;
            case NET_WIFI:
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


}

