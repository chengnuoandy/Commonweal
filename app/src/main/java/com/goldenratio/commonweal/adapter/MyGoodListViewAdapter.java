package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.U_NormalP;
import com.goldenratio.commonweal.util.GlideCircleTransform;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Kiuber on 2016/6/26.
 * 物品列表适配器相关
 */

public class MyGoodListViewAdapter extends BaseAdapter {
    private static final String TAG = "lxc";
    private Context mContext;
    private List<Good> mGoodList;
    private LayoutInflater mInflater;
    private final SparseArray<ViewHolder> mCountdownVHList;
    private Handler mHandler = new Handler();
    private Timer mTimer;
    private boolean isCancel = true;

    public MyGoodListViewAdapter(Context mContext, List<Good> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.mInflater = LayoutInflater.from(mContext);
        mCountdownVHList = new SparseArray<>();
        startRefreshTime();
    }

    /**
     * 初始化倒计时相关逻辑
     */
    public void startRefreshTime() {
        if (!isCancel) return; //判断第一次new才执行此方法

        if (null != mTimer) {
            mTimer.cancel();
        }

        isCancel = false;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(mRefreshTimeRunnable);
            }
        }, 0, 10);
    }

    /**
     * 停止倒计时同时清除倒计时所占相关资源
     */
    public void cancelRefreshTime() {
        isCancel = true;
        if (null != mTimer) {
            mTimer.cancel();
        }
        mHandler.removeCallbacks(mRefreshTimeRunnable);
    }

    @Override
    public int getCount() {
        return mGoodList.size();
    }

    @Override
    public Good getItem(int position) {
        return mGoodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_good_item, null);
            viewHolder = new ViewHolder();
            //初始化布局
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initData(position);
        Good mGood = mGoodList.get(position);
        //初始化倒计时相关数据
        viewHolder.bindData(mGood);

        // 处理倒计时
        if (mGood.getGood_UpDateM() > 0) {
            synchronized (mCountdownVHList) {
                mCountdownVHList.put(position, viewHolder);
            }
        }

        return convertView;
    }

    /**
     * 多线程更新倒计时
     */
    private Runnable mRefreshTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCountdownVHList.size() == 0) return;

            synchronized (mCountdownVHList) {
                long currentTime = System.currentTimeMillis(); //先用本地时间
                int key;
                for (int i = 0; i < mCountdownVHList.size(); i++) {
                    key = mCountdownVHList.keyAt(i);
                    ViewHolder curMyViewHolder = mCountdownVHList.get(key);
                    if (currentTime >= curMyViewHolder.getBean().getGood_UpDateM()) {
                        // 倒计时结束
                        curMyViewHolder.getBean().setGood_UpDateM((long) 0);
                        mCountdownVHList.remove(key);
                        //更新显示
                        notifyDataSetChanged();
                        Log.d(TAG, "run: 已经结束！");
                    } else {
                        curMyViewHolder.refreshTime(currentTime);
                    }
                }
            }
        }
    };

    //缓存类
    class ViewHolder {
        private TextView mTvUserName;
        private TextView mTvTime;
        private TextView mTvName;
        private ImageView mIvPic;
        private TextView mTvDescription;
        private CountdownView mCountdownView;
        private Good mGood;
        private TextView mTvNowPrice;
        private ImageView mIvUserAvatar;

        public void initView(View convertView) {
            mTvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
            mCountdownView = (CountdownView) convertView.findViewById(R.id.cv_good);
            mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            mIvPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            mTvNowPrice = (TextView) convertView.findViewById(R.id.tv_now_price);
            mIvUserAvatar = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
        }

        //初始化数据
        public void bindData(Good Good) {
            this.mGood = Good;

            if (mGood.getGood_UpDateM() > 0) {
                refreshTime(System.currentTimeMillis()); //暂时用本地时间
            } else {
                mCountdownView.allShowZero();
            }
        }

        //刷新时间显示
        public void refreshTime(long curTimeMillis) {
            if (null == mGood || mGood.getGood_UpDateM() <= 0) return;
            //更新时间
            mCountdownView.updateShow(mGood.getGood_UpDateM() - curTimeMillis);
        }

        public Good getBean() {
            return mGood;
        }

        private void initData(int position) {

            BmobQuery<U_NormalP> userBmobQuery = new BmobQuery<>();
            userBmobQuery.addWhereEqualTo("objectId", getItem(position).getGood_User_ID());
            userBmobQuery.findObjects(mContext, new FindListener<U_NormalP>() {
                @Override
                public void onSuccess(List<U_NormalP> list) {
                }

                @Override
                public void onError(int i, String s) {

                }
            });

            mTvTime.setText(getItem(position).getCreatedAt());
            mTvName.setText(getItem(position).getGood_Name());

            Glide.with(mContext)
                    .load(getItem(position).getGood_Photos().get(0).toString())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(mContext))
                    .into(mIvPic);
            mTvNowPrice.setText(getItem(position).getGood_NowPrice() + "");
        }
    }
}
