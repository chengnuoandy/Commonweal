package com.goldenratio.commonweal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.goldenratio.commonweal.ui.activity.MainActivity;
import com.goldenratio.commonweal.ui.fragment.DynamicFragment;
import com.goldenratio.commonweal.ui.fragment.GoodFragment;
import com.goldenratio.commonweal.ui.fragment.HelpFragment;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import java.util.List;


/**
 * 作者：Created by 龙啸天 on 2016/6/20 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 *
 * 承担viewPager与fragment的适配（界面的滑动切换）
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i("dynamic", "destroyItem: " + position);
        super.destroyItem(container, position, object);
    }

}
