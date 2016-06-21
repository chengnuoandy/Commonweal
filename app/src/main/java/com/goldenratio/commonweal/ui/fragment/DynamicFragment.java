package com.goldenratio.commonweal.ui.fragment;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyDynFragmentPagerAdapter;
import com.goldenratio.commonweal.adapter.MyFragmentPagerAdapter;
import com.goldenratio.commonweal.ui.fragment.Dynamic.HelpDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.Dynamic.OfficialDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.Dynamic.PersonalDynamicFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DynamicFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private PagerTabStrip mPtsTitle;
    private ViewPager mVpDynamic;
    private List<Fragment> mFragmentList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, null);

        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new OfficialDynamicFragment());
        mFragmentList.add(new HelpDynamicFragment());
        mFragmentList.add(new PersonalDynamicFragment());

    /*    mPtsTitle = (PagerTabStrip) view.findViewById(R.id.pts_title);
        mPtsTitle.setDrawFullUnderline(false);
        mPtsTitle.setTabIndicatorColor(Color.RED);*/

        mVpDynamic = (ViewPager) view.findViewById(R.id.vp_dynamic);
        mVpDynamic.setAdapter(new MyDynFragmentPagerAdapter(getFragmentManager(), mFragmentList));
        mVpDynamic.addOnPageChangeListener(this);
        return view;
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
        }
    }
}
