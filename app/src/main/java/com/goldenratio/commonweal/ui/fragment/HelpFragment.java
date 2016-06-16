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

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class HelpFragment extends Fragment {


    private ViewPager mViewPager;

    private ListView mListView;

   // private Handler mHandler;

    private CirclePageIndicator indicator;

    private HelpListViewAdapter adapter;
    // 初始化fragment的布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= initView();
        return view;
    }


    private  View initView(){

        View view = View.inflate(getContext(),R.layout.fragment_help, null);
        mListView = (ListView) view.findViewById(R.id.lv_help);

        View mHeaderView =View.inflate(getContext(),R.layout.view_help_hander,null);
        indicator = (CirclePageIndicator) mHeaderView.findViewById(R.id.indicator);

        //头文件
        mViewPager= (ViewPager) mHeaderView.findViewById(R.id.vp_news_title);
        mViewPager.setAdapter(new HelpViewPagerAdapter(getContext()));

        indicator.setViewPager(mViewPager);
        indicator.setSnap(true);

        mListView.addHeaderView(mHeaderView);
        adapter= new HelpListViewAdapter(getContext());
        mListView.setAdapter(adapter);


        return view;
    }




    }

