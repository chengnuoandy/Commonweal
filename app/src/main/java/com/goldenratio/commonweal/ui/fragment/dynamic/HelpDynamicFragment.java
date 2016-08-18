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
import com.goldenratio.commonweal.adapter.DynamicHelpAdapter;
import com.goldenratio.commonweal.bean.Dynamic_Help;

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
 * update: 冰封承諾Andy
 */
public class HelpDynamicFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    //最大加载数
    private int mMAXItem = 8;
    //记录当前加载到第几页
    private int count;
    //数据是否加载完毕
    private boolean dataDone = true;

    @BindView(R.id.lv_dynamic_help)
    ListView mLvDynamicHelp;
    @BindView(R.id.bga_help_refresh)
    BGARefreshLayout mBgaHelpRefresh;

    private List<Dynamic_Help> mDycHelpList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_help, null);

        ButterKnife.bind(this, view);

        configurationRefresh(view);
        getDataFromBmob();
        return view;
    }

    private void configurationRefresh(View v) {
        // 为BGARefreshLayout设置代理
        mBgaHelpRefresh.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新和上拉加载更多的风格
        mBgaHelpRefresh.setRefreshViewHolder(refreshViewHolder);
        //下拉刷新
        mBgaHelpRefresh.beginRefreshing();

    }

    private void getDataFromBmob() {
        BmobQuery<Dynamic_Help> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(mMAXItem);
        query.findObjects(new FindListener<Dynamic_Help>() {
            @Override
            public void done(List<Dynamic_Help> list, BmobException e) {
                if (e == null) {
                    dataDone = list.size() >= mMAXItem;
                    count = 1;
                    mDycHelpList = list;
                    mLvDynamicHelp.setAdapter(new DynamicHelpAdapter(list, getActivity()));
                    mBgaHelpRefresh.endRefreshing();
                } else {
                    mBgaHelpRefresh.endRefreshing();
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
        if (mDycHelpList != null) {
            mDycHelpList.clear();
            getDataFromBmob();
        }else {
            getDataFromBmob();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (!dataDone){
            Toast.makeText(getContext(), "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
            return false;
        }
        //查询后面的数据
        BmobQuery<Dynamic_Help> query = new BmobQuery<>();
        query.order("-createdAt");
        //限制返回的数据量
        query.setLimit(mMAXItem);
        query.setSkip(mMAXItem * count);
        query.findObjects(new FindListener<Dynamic_Help>() {
            @Override
            public void done(List<Dynamic_Help> list, BmobException e) {
                if (e == null) {
                    //如果数据已经不足，设置上拉加载标志位
                    if (list.size() < mMAXItem){
                        Toast.makeText(getContext(), "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
                        dataDone = false;
                    }
                    //追加数据
                    for (int i = 0; i < list.size(); i++) {
                        mDycHelpList.add(list.get(i));
                    }
                    count++;
                    // 加载完毕后在UI线程结束加载更多
                    mBgaHelpRefresh.endLoadingMore();
                } else {
                    Toast.makeText(getContext(), "刷新失败！" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }
}
