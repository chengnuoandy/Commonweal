package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.fragment.DynamicFragment;
import com.goldenratio.commonweal.ui.fragment.GoodsFragment;
import com.goldenratio.commonweal.ui.fragment.HelpFragment;
import com.goldenratio.commonweal.ui.fragment.LoginFragment;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2016/6/6.
 */
public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {

    private FragmentManager mFmMain;// 管理fragment的类
    private RadioGroup mRgTabs;
    private RadioButton mRbDefalut;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            case R.id.rb_goods:// 物品
                changeFragment(new GoodsFragment(), true);
                break;
            case R.id.rb_dynamic:// 动态
                changeFragment(new DynamicFragment(), true);
                break;
            case R.id.rb_my:// 我
                //changeFragment(new MyFragment(), true);
                changeFragment(new LoginFragment(), true);
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
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
