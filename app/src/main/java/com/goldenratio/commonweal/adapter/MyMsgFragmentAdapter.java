package com.goldenratio.commonweal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.goldenratio.commonweal.ui.fragment.MessageDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.PrivateLetterFragment;

import java.util.List;

/**
 * 作者：Created by 龙啸天 on 2016/6/25 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 * <p/>
 * 承担消息界面两个fragment的适配（私信与动态的滑动切换）
 */
public class MyMsgFragmentAdapter extends FragmentPagerAdapter {

    private MessageDynamicFragment mMessageDynamicFragment;
    private PrivateLetterFragment mPrivateLetterFragment;

    public MyMsgFragmentAdapter(FragmentManager fm, List fragmentList ) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mMessageDynamicFragment;
                break;
            case 1:
                fragment = mPrivateLetterFragment;
                break;
        }
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
