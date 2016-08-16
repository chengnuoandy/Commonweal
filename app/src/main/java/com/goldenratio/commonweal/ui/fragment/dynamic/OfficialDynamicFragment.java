package com.goldenratio.commonweal.ui.fragment.dynamic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.DynamicOfficialAdapter;
import com.goldenratio.commonweal.bean.Dynamic_Official;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 龙啸天 - 龙啸天 on 2016/6/21 0021.
 * Email:jxfengmtx@163.com
 */
public class OfficialDynamicFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {


    @BindView(R.id.lv_dynamic_official)
    ListView mLvDynamicOfficial;
    @BindView(R.id.rl_official_refresh)
    BGARefreshLayout mBGARefreshLayout;

    private List<Dynamic_Official> mDycOfficialList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_official, null);

        ButterKnife.bind(this, view);

        configurationRefresh(view);
        getDataFromBmob();
        return view;
    }

    private void configurationRefresh(View v) {
        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), false);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
        //下拉刷新
        mBGARefreshLayout.beginRefreshing();
    }

    private void getDataFromBmob() {
        BmobQuery<Dynamic_Official> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<Dynamic_Official>() {
            @Override
            public void done(List<Dynamic_Official> list, BmobException e) {
                if (e == null) {
                    mDycOfficialList = list;
                    mLvDynamicOfficial.setAdapter(new DynamicOfficialAdapter(list, getActivity()));
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    mBGARefreshLayout.endRefreshing();
                    Toast.makeText(getActivity(), "获取数据失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        if (myApplication.isDynamicRefresh()) {
            getDataFromBmob();
            myApplication.setDynamicRefresh(false);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //重新装填数据
        if (mDycOfficialList != null) {
            mDycOfficialList.clear();
            getDataFromBmob();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
