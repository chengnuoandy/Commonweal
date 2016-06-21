package com.goldenratio.commonweal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.goldenratio.commonweal.ui.activity.MainActivity;
import com.goldenratio.commonweal.ui.fragment.DynamicFragment;
import com.goldenratio.commonweal.ui.fragment.GoodFragment;
import com.goldenratio.commonweal.ui.fragment.HelpFragment;
import com.goldenratio.commonweal.ui.fragment.MyFragment;


/**
 * Created by 龙啸天 on 2016/6/20 0020.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private HelpFragment mHelpFragment = null;
    private GoodFragment mGoodFragment = null;
    private DynamicFragment mDynamicFragment = null;
    private MyFragment mMyFragment = null;


    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mHelpFragment = new HelpFragment();
        mGoodFragment = new GoodFragment();
        mDynamicFragment = new DynamicFragment();
        mMyFragment = new MyFragment();
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = mHelpFragment;
                break;
            case MainActivity.PAGE_TWO:
                fragment = mGoodFragment;
                break;
            case MainActivity.PAGE_THREE:
                fragment = mDynamicFragment;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = mMyFragment;
                break;
 /*         case 4:
                fragment = mGoodFragment;
                break;
            case 5:
                fragment = mHelpFragment;
                break;*/
        }
        return fragment;
    }

}
