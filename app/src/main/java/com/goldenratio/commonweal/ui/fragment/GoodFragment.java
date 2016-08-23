package com.goldenratio.commonweal.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodListViewAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.ui.activity.GoodDetailActivity;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;


public class GoodFragment extends Fragment implements AdapterView.OnItemClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final String TAG = "lxc";
    private View view;
    private MyGoodListViewAdapter myGoodListViewAdapter;
    private List<Good> mGoodList;
    private Long endTime;
    private ListView mListView;
    private LinearLayout mLlNoNet;
    private ProgressDialog progressDialog;
    private BGARefreshLayout mBGARefreshLayout;
    //最大加载数
    private int mMAXItem = 8;
    //记录当前加载到第几页
    private int count;
    //数据是否加载完毕
    private boolean dataDone = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_good, null);
        initView();
        findDataFromBmob();
        ifTime();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * 初始化数据
     */
    private void findDataFromBmob() {
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.order("-createdAt");
        goodBmobQuery.setLimit(mMAXItem);
        goodBmobQuery.include("Good_User");
        goodBmobQuery.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> list, BmobException e) {
                if (e == null) {
                    dataDone = list.size() >= mMAXItem;
                    count = 1;

                    mGoodList = list;
                    myGoodListViewAdapter = new MyGoodListViewAdapter(getContext(), mGoodList);
                    mListView.setAdapter(myGoodListViewAdapter);
                    hideLinearLayout();
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                    mLlNoNet.setVisibility(View.VISIBLE);

                }
            }

        });
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mLlNoNet = (LinearLayout) view.findViewById(R.id.ll_no_net);
        mLlNoNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDataFromBmob();
            }
        });
        mListView = (ListView) view.findViewById(R.id.lv_good_all);
        mListView.setOnItemClickListener(this);

        mBGARefreshLayout = (BGARefreshLayout) view.findViewById(R.id.rl_BGA_refresh);
        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
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
        endTime = mGoodList.get(position).getGood_UpDateM();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Bmob_Good", mGoodList.get(position));
        StartAct(bundle);
//        Object itemAtPosition = parent.getItemAtPosition(position);
//        Log.d(TAG, "onItemClick: " + itemAtPosition);
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

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (mGoodList != null) {
            mGoodList.clear();
            findDataFromBmob();
        }else {
            findDataFromBmob();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (!dataDone){
            Toast.makeText(getContext(), "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
            return false;
        }
        //查询后面的数据
        BmobQuery<Good> data = new BmobQuery<>();
        data.order("-createdAt");
        //限制返回的数据量
        data.setLimit(mMAXItem);
        data.setSkip(mMAXItem * count);
        data.include("Good_User");
        data.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> list, BmobException e) {
                if (e == null) {
                    //如果数据已经不足，设置上拉加载标志位
                    if (list.size() < mMAXItem){
                        Toast.makeText(getContext(), "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
                        dataDone = false;
                    }
                    //追加数据
                    for (int i = 0; i < list.size(); i++) {
                        mGoodList.add(list.get(i));
                    }
                    count++;
                    // 加载完毕后在UI线程结束加载更多
                    mBGARefreshLayout.endLoadingMore();
                } else {
                    // 加载完毕后在UI线程结束加载更多
                    mBGARefreshLayout.endLoadingMore();
                    Toast.makeText(getContext(), "刷新失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }
}