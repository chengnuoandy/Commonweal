package com.goldenratio.commonweal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 作者：Created by 龙啸天 on 2016/6/25 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 *
 * 承担消息界面两个fragment的适配（私信与动态的滑动切换）
 */
public class MyMsgFragmentAdapter extends FragmentPagerAdapter {

    public MyMsgFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
