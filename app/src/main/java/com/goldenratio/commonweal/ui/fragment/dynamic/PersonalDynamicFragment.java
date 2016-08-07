package com.goldenratio.commonweal.ui.fragment.dynamic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyDynamicAdapter;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.ui.view.PullToRefreshListView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 龙啸天 on 2016/6/21 0021.
 * update by 冰封承諾Andy
 * 个人动态页面
 */
public class PersonalDynamicFragment extends Fragment {

    private PullToRefreshListView mListView;
    private View mView;
    private List<Dynamic> mDynamicList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_personal, null);
        mView = view;

        initView();
        initData();
        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        BmobQuery<Dynamic> data = new BmobQuery<>();
        data.order("createdAt");
        data.findObjects(getContext(), new FindListener<Dynamic>() {
            @Override
            public void onSuccess(List<Dynamic> list) {
                mDynamicList = list;
                mListView.setAdapter(new MyDynamicAdapter(getActivity(),list));
                mListView.onRefreshComplete();
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
        mListView = (PullToRefreshListView) mView.findViewById(R.id.lv_dynamic_all);
        //下拉刷新
        mListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新装填数据
                mDynamicList.clear();
                initData();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }
}
