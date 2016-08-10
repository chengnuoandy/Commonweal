package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodListViewAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.ui.activity.GoodActivity;
import com.goldenratio.commonweal.ui.activity.GoodDetailActivity;
import com.goldenratio.commonweal.ui.view.PullToRefreshListView;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;


public class GoodFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "lxc";
    private View view;
    private MyGoodListViewAdapter myGoodListViewAdapter;
    private List<Good> mGoodList;
    private Long endTime;
    private PullToRefreshListView mListView;
    private LinearLayout mLlNoNet;
    private TextView mTvNowPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_good, null);

        initView();
        initData();
        ifTime();
        return view;
    }


    /**
     * 初始化数据
     */
    private void initData() {
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.order("-createdAt");
        goodBmobQuery.include("Good_User");
        goodBmobQuery.findObjects(getContext(), new FindListener<Good>() {
            @Override
            public void onSuccess(List<Good> list) {
                myGoodListViewAdapter = new MyGoodListViewAdapter(getContext(), list);
                mListView.setAdapter(myGoodListViewAdapter);
                mListView.onRefreshComplete();
                mGoodList = list;
                hideLinearLayout();
            }

            @Override
            public void onError(int i, String s) {
                mLlNoNet.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * 初始化布局
     */
    private void initView() {
        mTvNowPrice = (TextView) view.findViewById(R.id.tv_now_price);
        mLlNoNet = (LinearLayout) view.findViewById(R.id.ll_no_net);
        mLlNoNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        view.findViewById(R.id.iv_add_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GoodActivity.class));
            }
        });
        mListView = (PullToRefreshListView) view.findViewById(R.id.lv_good_all);
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //初始化数据
                mGoodList.clear();
                initData();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    /**
     * 判断用户本地时间是否准确
     */
    private void ifTime() {
        Bmob.getServerTime(getContext(), new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                Long t = System.currentTimeMillis();
                Long w = time * 1000L;
                if (t > (w + 180000) || t < (w - 180000))
                    Toast.makeText(getContext(), "检测到您的时钟与网络时间不符，可能会影响您的购买！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i("lxc", "获取服务器时间失败:" + msg);
            }
        });
    }

    /**
     * 跳转activity逻辑代码
     * 获取现在时间与截止时间的差值 传给activity
     * 由于bmob获取时间方法限制，故提取方法作
     */
    private void StartAct(final Bundle bundle) {
        Bmob.getServerTime(getContext(), new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                Long TimeLeft = endTime - (time * 1000L);
                Intent intent = new Intent(getContext(), GoodDetailActivity.class);
                intent.putExtra("EndTime", TimeLeft);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("lxc", "获取服务器时间失败:" + msg);
                Toast.makeText(getContext(), "获取服务器时间失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * listview 列表点击事件--跳转activity
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取当前条目的截止时间
        endTime = mGoodList.get(position - 1).getGood_UpDateM();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Good", mGoodList.get(position - 1));
        StartAct(bundle);

        Object itemAtPosition = parent.getItemAtPosition(position);
        Log.d(TAG, "onItemClick: " + itemAtPosition);
    }


    //    以下为生命周期部分
    @Override
    public void onResume() {
        super.onResume();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.startRefreshTime();
        }
        ifTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.cancelRefreshTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.cancelRefreshTime();
        }
    }

    private void hideLinearLayout() {
        if (mLlNoNet.getVisibility() == View.VISIBLE) {
            mLlNoNet.setVisibility(View.GONE);
        }
    }
}