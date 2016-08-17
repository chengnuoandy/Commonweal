package com.goldenratio.commonweal.ui.fragment.dynamic;

import android.os.AsyncTask;
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

import java.util.ArrayList;
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
    private int mMAXItem = 3;
    private List<Dynamic> mListPiece = new ArrayList<>();

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
        data.include("Dynamics_user");
        data.findObjects(new FindListener<Dynamic>() {
            @Override
            public void done(List<Dynamic> list, BmobException e) {
                if (e == null) {
                    mDynamicList = list;
                    for (int i = 0; i < mMAXItem; i++) {
                        mListPiece.add(list.get(i));
                    }
                    mListView.setAdapter(new MyDynamicAdapter(getActivity(), mListPiece));
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                    Toast.makeText(getContext(), "未知错误" + e, Toast.LENGTH_SHORT).show();
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
        }
    }

    /**
     * 加载更多逻辑--上拉刷新
     *
     * @param refreshLayout 刷新布局控件
     * @return true:显示正在刷新  false:不显示刷新
     *
     * 现在逻辑：一次从数据库加载所有数据，分段向listview添加数据 (适合少量数据)
     * 待改进方法：一次从服务器查少量数据，数据加载完毕再doInBackground里继续查剩余的 （适合大数据）
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {

        final int sum = mListView.getAdapter().getCount();
        //判断适配器中的数据是否已是全部数据
        if (sum >= mDynamicList.size()) {
            Toast.makeText(getContext(), "数据已经加载完毕！", Toast.LENGTH_SHORT).show();
            return false;
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // 加载完毕后在UI线程结束加载更多
                mBGARefreshLayout.endLoadingMore();
                //添加数据
                if ((sum + mMAXItem) < mDynamicList.size()) {
                    for (int i = 0; i < mMAXItem; i++) {
                        mListPiece.add(mDynamicList.get(i + sum));
                    }
                } else {
                    //数据已经不足
                    for (int i = sum; i < mDynamicList.size(); i++) {
                        mListPiece.add(mDynamicList.get(i));
                    }
                }
            }
        }.execute();

        return true;
    }
}
