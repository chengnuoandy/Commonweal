package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyDynamicAdapter;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DynamicActivity extends Activity implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private List<Dynamic> mDynamicList;
    private ListView mListView;
    private BGARefreshLayout mBGARefreshLayout;
    //最大加载数
    private int mMAXItem = 8;
    //记录当前加载到第几页
    private int count;
    //数据是否加载完毕
    private boolean dataDone = true;
    private String mObj = "";
    private TextView mNoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);

        initView();
        mObj = ((MyApplication)getApplication()).getObjectID();
        if (mObj != null){
            initData();
        }else {
//            mNoTextView.setVisibility(View.VISIBLE);
            Toast.makeText(DynamicActivity.this, "请先登陆！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        User_Profile user = new User_Profile();
        user.setObjectId(mObj);
        BmobQuery<Dynamic> data = new BmobQuery<>();
        data.order("-createdAt");
        data.addWhereEqualTo("Dynamics_user",user);
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
                    mListView.setAdapter(new MyDynamicAdapter(DynamicActivity.this, list));
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
//                    Toast.makeText(DynamicActivity.this, "未知错误" + e, Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_dynamic_all);
        mNoTextView = (TextView) findViewById(R.id.tv_no_data);

        mListView.setEmptyView(mNoTextView);
        mBGARefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_BGA_refresh);
        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
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
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {

        if (!dataDone){
            Toast.makeText(DynamicActivity.this, "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DynamicActivity.this, "数据已全部加载完毕！", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(DynamicActivity.this, "刷新失败！" + e, Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });

        return true;
    }
}
