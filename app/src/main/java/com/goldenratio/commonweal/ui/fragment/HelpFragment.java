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
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpListViewAdapter;
import com.goldenratio.commonweal.adapter.HelpViewPagerAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Top;
import com.goldenratio.commonweal.ui.activity.HelpContentActivity;
import com.goldenratio.commonweal.ui.view.PullToRefreshListView;
import com.viewpagerindicator.CirclePageIndicator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static java.security.AccessController.getContext;

public class HelpFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ViewPager mViewPager;
    private PullToRefreshListView mListView;
    private CirclePageIndicator indicator;
    private Handler mHandler;
    private List<Help_Top> mList;


    private int participant;  //参与人数
    private int Day;           //剩余日期
    private int sum;           //项目所需总数
    private int AtPresent;    //项目现在进程


    private View mHeaderView;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView();
        initData();
        //topSliding();

        mListView.setOnItemClickListener(this);
//        Help help = new Help();
//        help.setHelp_Type("教育");
//        help.setHelp_Organization("商务");
//        help.setHelp_Open_Data("2016-06-26");
//        help.setHelp_End_Data("2016-06-27");
//
//      help.save(getContext(), new SaveListener() {
//          @Override
//          public void onSuccess() {
//
//          }
//
//          @Override
//          public void onFailure(int i, String s) {
//              Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
//          }
//      });
        return view;
    }

    private View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_help, null);
        mListView = (PullToRefreshListView) view.findViewById(R.id.lv_help);

        mHeaderView = View.inflate(getContext(), R.layout.view_help_hander, null);
        indicator = (CirclePageIndicator) mHeaderView.findViewById(R.id.indicator);
        Log.d("CN", "initView: ++++++++++++++++++++++++++++++++++++++");
        //头文件


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
        return view;
    }

    private List<Help> mHelpLlist;

    public void initData() {
        BmobQuery<Help> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(getContext(), new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                mHelpLlist = list;
//
//                mViewPager = (ViewPager) mHeaderView.findViewById(R.id.vp_news_title);
//                mViewPager.setAdapter(new HelpViewPagerAdapter(getContext(),list));
//                indicator.setViewPager(mViewPager);
//                indicator.setSnap(true);

                mListView.setAdapter(new HelpListViewAdapter(getContext(), list));
                mListView.onRefreshComplete();
            }

            @Override
            public void onError(int i, String s) {

                Log.i("bmob", "下载失败：" + s);
                mListView.onRefreshComplete();
            }
        });
        BmobQuery<Help_Top> bmobQueryTop = new BmobQuery<>();
        bmobQueryTop.order("-createdAt");
        bmobQueryTop.findObjects(getContext(), new FindListener<Help_Top>() {
            @Override
            public void onSuccess(List<Help_Top> list) {

                mList = list;
                mViewPager = (ViewPager) mHeaderView.findViewById(R.id.vp_news_title);
                mViewPager.setAdapter(new HelpViewPagerAdapter(getContext(), list));
                indicator.setViewPager(mViewPager);
                indicator.setSnap(true);


                topSliding();


            }

            @Override
            public void onError(int i, String s) {

                Log.i("bmob", "下载失败：" + s);
                mListView.onRefreshComplete();
            }
        });

    }
    // 获取头部数据


    public void topSliding() {
          /*
           实现图片轮播
         */

        if (mHandler == null) {
            mHandler = new android.os.Handler() {
                public void handleMessage(android.os.Message msg) {
                    int currentItem = mViewPager.getCurrentItem();
                    currentItem++;

                    if (currentItem > mList.size() - 1) {
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


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), HelpContentActivity.class);
        intent.putExtra("title", mHelpLlist.get(position - 2).getHelp_Title());//标题
        intent.putExtra("pic", mHelpLlist.get(position - 2).getHelp_Top_pic());//图片
        intent.putExtra("sponsor", mHelpLlist.get(position - 2).getHelp_Sponsor());//赞助方
        intent.putExtra("initiator", mHelpLlist.get(position - 2).getHelp_Initiator());//发起方，执行方
        intent.putExtra("content", mHelpLlist.get(position - 2).getHelp_Content_content());//项目内容简介
//        Log.d("CN", "onItemClick: "+mList.get(position-2).getHelp_Top_Pic());
        startActivity(intent);
    }
}