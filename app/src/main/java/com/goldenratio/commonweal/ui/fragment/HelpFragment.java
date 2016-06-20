package com.goldenratio.commonweal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpListViewAdapter;
import com.goldenratio.commonweal.adapter.HelpViewPagerAdapter;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Top;
import com.goldenratio.commonweal.ui.view.PullToRefreshListView;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class HelpFragment extends Fragment {


    private ViewPager mViewPager;

    private PullToRefreshListView mListView;



    private CirclePageIndicator indicator;

    // private HelpListViewAdapter adapter;
    private View mHeaderView;

    // 初始化fragment的布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView();

        return view;
    }



    private View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_help, null);
        mListView = (PullToRefreshListView) view.findViewById(R.id.lv_help);

        mHeaderView = View.inflate(getContext(), R.layout.view_help_hander, null);
        indicator = (CirclePageIndicator) mHeaderView.findViewById(R.id.indicator);

        //头文件
        mViewPager = (ViewPager) mHeaderView.findViewById(R.id.vp_news_title);
        mViewPager.setAdapter(new HelpViewPagerAdapter(getContext()));

//       adapter= new HelpListViewAdapter(getContext(),help_url);
//      mListView.setAdapter(adapter);

        indicator.setViewPager(mViewPager);
        indicator.setSnap(true);
        mListView.addHeaderView(mHeaderView);
        mListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onLoadMore() {

            }
        });


        initData();

        return view;
    }




    public void initData() {
        BmobQuery<Help> bmobQuery =new BmobQuery<>();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(getContext(), new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {

                mListView.setAdapter(new HelpListViewAdapter(getContext(),list));
                mListView.onRefreshComplete();


            }

            @Override
            public void onError(int i, String s) {

                Log.i("bmob","下载失败："+s);
                mListView.onRefreshComplete();
            }
        });



    }
}


