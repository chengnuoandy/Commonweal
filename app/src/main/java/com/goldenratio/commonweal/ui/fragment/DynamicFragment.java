package com.goldenratio.commonweal.ui.fragment;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyFragmentPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DynamicFragment extends Fragment {

    private PagerTabStrip mPtsTitle;
    private ViewPager mVpDynamic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, null);

       /* mPtsTitle = (PagerTabStrip) view.findViewById(R.id.pts_title);
        mVpDynamic = (ViewPager) view.findViewById(R.id.vp_dynamic);
        mVpDynamic.setAdapter(new MyFragmentPagerAdapter(getFragmentManager()));*/
       // mVpDynamic.addOnPageChangeListener(this);
        return view;
    }


}
