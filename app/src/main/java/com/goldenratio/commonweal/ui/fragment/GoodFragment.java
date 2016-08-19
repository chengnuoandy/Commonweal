package com.goldenratio.commonweal.ui.fragment;

import android.app.ProgressDialog;
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
import com.goldenratio.commonweal.ui.activity.GoodDetailActivity;
import com.goldenratio.commonweal.ui.view.PullToRefreshListView;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;


public class GoodFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "lxc";
    private View view;
    private MyGoodListViewAdapter myGoodListViewAdapter;
    private List<Good> mGoodList;
    private Long endTime;
    private PullToRefreshListView mListView;
    private LinearLayout mLlNoNet;
    private TextView mTvNowPrice;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_good, null);

        initView();
        findDataFromBmob();
        ifTime();
        return view;
    }


    /**
     * 初始化数据
     */
    private void findDataFromBmob() {
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.order("-createdAt");
        goodBmobQuery.include("Good_User");
        goodBmobQuery.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> list, BmobException e) {
                if (e == null) {
                    myGoodListViewAdapter = new MyGoodListViewAdapter(getContext(), list);
                    mListView.setAdapter(myGoodListViewAdapter);
                    mListView.onRefreshComplete();
                    mGoodList = list;
                    hideLinearLayout();
                } else {
                    mLlNoNet.setVisibility(View.VISIBLE);

                }
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
                findDataFromBmob();
            }
        });
        mListView = (PullToRefreshListView) view.findViewById(R.id.lv_good_all);
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //初始化数据
                mGoodList.clear();
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
        Bmob.getServerTime(new QueryListener<Long>() {

            @Override
            public void done(Long aLong, BmobException e) {
                if (e == null) {
                    Long t = System.currentTimeMillis();
                    Long w = aLong * 1000L;
                    if (t > (w + 180000) || t < (w - 180000))
                        Toast.makeText(getContext(), "检测到您的时钟与网络时间不符，可能会影响您的购买！", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("lxc", "获取服务器时间失败:" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 跳转activity逻辑代码
     * 获取现在时间与截止时间的差值 传给activity
     * 由于bmob获取时间方法限制，故提取方法作
     */
    private void StartAct(final Bundle bundle) {
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if (e == null) {
                    Long TimeLeft = endTime - (aLong * 1000L);
                    Intent intent = new Intent(getContext(), GoodDetailActivity.class);
                    intent.putExtra("EndTime", TimeLeft);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    progressDialog.dismiss();
                } else {
                    Log.d("lxc", "获取服务器时间失败:" + e.getMessage());
                    Toast.makeText(getContext(), "获取服务器时间失败！" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * listview 列表点击事件--跳转activity
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        progressDialog = ProgressDialog.show(getContext(), null, "正在安全获取", true, false);

        //获取当前条目的截止时间
        endTime = mGoodList.get(position - 1).getGood_UpDateM();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Bmob_Good", mGoodList.get(position - 1));
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