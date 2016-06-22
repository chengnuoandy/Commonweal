package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpListViewAdapter;
import com.goldenratio.commonweal.adapter.HelpViewPagerAdapter;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.ui.activity.HelpContentActivity;
import com.goldenratio.commonweal.ui.view.PullToRefreshListView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class HelpFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ViewPager mViewPager;
    private PullToRefreshListView mListView;
    private CirclePageIndicator indicator;
    private Handler mHandler;

    private TextView mTV_Item;

    private int participant;  //参与人数
    private int Day;           //剩余日期
    private int sum;           //项目所需总数
    private int AtPresent;    //项目现在进程


    private View mHeaderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView();

       mListView.setOnItemClickListener(this);

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
    List<Help> mList;

    public void initData() {
        BmobQuery<Help> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(getContext(), new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                mList = list;
                mListView.setAdapter(new HelpListViewAdapter(getContext(), list));
                mListView.onRefreshComplete();
            }


            @Override
            public void onError(int i, String s) {

                Log.i("bmob", "下载失败：" + s);
                mListView.onRefreshComplete();
            }
        });
        if (mHandler == null) {
            mHandler = new android.os.Handler() {
                public void handleMessage(android.os.Message msg) {
                    int currentItem = mViewPager.getCurrentItem();
                    currentItem++;

                    if (currentItem > 2) {
                        currentItem = 0;// 如果已经到了最后一个页面,跳到第一页
                    }

                    mViewPager.setCurrentItem(currentItem);

                    mHandler.sendEmptyMessageDelayed(0, 3000);// 继续发送延时3秒的消息,形成内循环
                }

            };

            // 保证启动自动轮播逻辑只执行一次
            mHandler.sendEmptyMessageDelayed(0, 3000);// 发送延时3秒的消息
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(),HelpContentActivity.class);
        intent.putExtra("title",mList.get(position-2).getHelp_Title());
        Log.d("CN", "onItemClick: "+mList.get(position).getHelp_Title());
        startActivity(intent);
    }
}