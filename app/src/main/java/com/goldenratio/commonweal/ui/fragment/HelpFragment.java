package com.goldenratio.commonweal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpListViewAdapter;
import com.goldenratio.commonweal.adapter.HelpViewPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

public class HelpFragment extends Fragment {


    private ViewPager mViewPager;

    private ListView mListView;



    private CirclePageIndicator indicator;


    // 初始化fragment的布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, null);
        mListView = (ListView) view.findViewById(R.id.lv_help);

        View mHeaderView =inflater.inflate(R.layout.view_help_hander,null);
        indicator = (CirclePageIndicator) mHeaderView.findViewById(R.id.indicator);

        //头文件
        mViewPager= (ViewPager) mHeaderView.findViewById(R.id.vp_news_title);
        mViewPager.setAdapter(new HelpViewPagerAdapter(getContext()));

       indicator.setViewPager(mViewPager);
       indicator.setSnap(true);

        mListView.addHeaderView(mHeaderView);
        mListView.setAdapter(new HelpListViewAdapter(getContext()));


        return view;
    }




    }

