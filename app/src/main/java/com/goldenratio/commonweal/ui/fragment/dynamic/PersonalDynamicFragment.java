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
import com.goldenratio.commonweal.adapter.MyDynamicAdapter;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 龙啸天 on 2016/6/21 0021.
 * update by 冰封承諾Andy
 * 个人动态页面
 */
public class PersonalDynamicFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    //    private PullToRefreshListView mListView;
    private View mView;
    private List<Dynamic> mDynamicList;
    private ListView mListView;
    private BGARefreshLayout mBGARefreshLayout;
    //最大加载数
    private int mMAXItem = 8;
    //记录当前加载到第几页
    private int count;
    //数据是否加载完毕
    private boolean dataDone = true;

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
        mBGARefreshLayout.beginRefreshing();
        BmobQuery<Dynamic> data = new BmobQuery<>();
        data.order("-createdAt");
        //限制返回的数据量
        data.setLimit(mMAXItem);
        data.include("Dynamics_user");
        data.findObjects(new FindListener<Dynamic>() {
            @Override
            public void done(List<Dynamic> list, BmobException e) {
                if (e == null) {
                    //如果数据仅有一页，关闭上拉加载更多
                    //第一次加载或者刷新数据初始化加载更多逻辑
                    dataDone = list.size() >= mMAXItem;
                    count = 1;

                    mDynamicList = list;
                    mListView.setAdapter(new MyDynamicAdapter(getActivity(), list));
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
//                    Toast.makeText(getContext(), "未知错误" + e, Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mListView = (ListView) mView.findViewById(R.id.lv_dynamic_all);

        mBGARefreshLayout = (BGARefreshLayout) mView.findViewById(R.id.rl_BGA_refresh);
        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        if (myApplication.isDynamicRefresh()) {
            initData();
            myApplication.setDynamicRefresh(false);
        }
    }

    /**
     * 下拉刷新逻辑
     *
     * @param refreshLayout 刷新布局控件
     */
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //重新装填数据
        if (mDynamicList != null) {
            mDynamicList.clear();
            initData();
        }else {
            initData();
        }
    }

    /**
     * 加载更多逻辑--上拉刷新
     *
     * @param refreshLayout 刷新布局控件
     * @return true:显示正在刷新  false:不显示刷新
     *
     * 原来的方法(看历史推送)：一次从数据库加载所有数据，分段向listview添加数据 (适合少量数据)
     * 现在方法(改进)：一次从服务器查少量数据，数据加载完毕再doInBackground里继续查剩余的 （适合大数据）
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {

        if (!dataDone){
            Toast.makeText(getContext(), "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
            return false;
        }
        //查询后面的数据
        BmobQuery<Dynamic> data = new BmobQuery<>();
        data.order("-createdAt");
        //限制返回的数据量
        data.setLimit(mMAXItem);
        data.setSkip(mMAXItem * count);
        data.include("Dynamics_user");
        data.findObjects(new FindListener<Dynamic>() {
            @Override
            public void done(List<Dynamic> list, BmobException e) {
                if (e == null) {
                    //如果数据已经不足，设置上拉加载标志位
                    if (list.size() < mMAXItem){
                        Toast.makeText(getContext(), "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
                        dataDone = false;
                    }
                    //追加数据
                    for (int i = 0; i < list.size(); i++) {
                        mDynamicList.add(list.get(i));
                    }
                    count++;
                    // 加载完毕后在UI线程结束加载更多
                    mBGARefreshLayout.endLoadingMore();
                } else {
//                    Toast.makeText(getContext(), "刷新失败！" + e, Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getContext(), e.getErrorCode() + "");
                }
            }
        });

        return true;
    }
}
