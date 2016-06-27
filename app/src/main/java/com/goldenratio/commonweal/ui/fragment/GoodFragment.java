package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodListViewAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.ui.activity.GoodActivity;
import com.goldenratio.commonweal.ui.activity.GoodDetail;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;


public class GoodFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "lxc";
    private View view;
    private ListView mLvGood;
    private MyGoodListViewAdapter myGoodListViewAdapter;
    private List<Good> mGoodList;
    private Long endTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_good, null);

        initView();
        initData();
        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.order("-createdAt");
        goodBmobQuery.findObjects(getContext(), new FindListener<Good>() {
            @Override
            public void onSuccess(List<Good> list) {
                myGoodListViewAdapter = new MyGoodListViewAdapter(getContext(), list);
                mLvGood.setAdapter(myGoodListViewAdapter);
                mGoodList = list;
            }

            @Override
            public void onError(int i, String s) {
            }
        });

    }

    /**
     * 初始化布局
     */
    private void initView() {
        mLvGood = (ListView) view.findViewById(R.id.lv_good_all);

        view.findViewById(R.id.iv_add_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GoodActivity.class));
            }
        });
        mLvGood.setOnItemClickListener(this);
    }


    /**
     * 跳转activity逻辑代码
     * 获取现在时间与截止时间的差值 传给activity
     * 由于bmob获取时间方法限制，故提取方法作
     */
    private void StartAct() {
        Bmob.getServerTime(getContext(), new GetServerTimeListener() {
            @Override
            public void onSuccess(long time) {
                Long TimeLeft = endTime - (time * 1000L);
                Log.d(TAG, "onSuccess: endtime-->" + endTime + "time---> " + time + "剩余时间---->" + TimeLeft);
                Intent intent = new Intent(getContext(), GoodDetail.class);
                intent.putExtra("EndTime", TimeLeft);
                startActivity(intent);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("lxc", "获取服务器时间失败:" + msg);
            }
        });
    }

    /**
     * listview 列表点击事件--跳转activity
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取当前条目的截止时间
        endTime = mGoodList.get(position).getGoods_UpDateM();
        StartAct();
    }

    //    以下为生命周期部分
    @Override
    public void onResume() {
        super.onResume();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.startRefreshTime();
        }
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

}
