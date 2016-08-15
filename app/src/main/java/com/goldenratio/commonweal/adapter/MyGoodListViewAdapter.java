package com.goldenratio.commonweal.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlGood;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Kiuber on 2016/6/26.
 * 物品列表适配器相关
 */

public class MyGoodListViewAdapter extends BaseAdapter {
    private static final String TAG = "lxc";
    private final SparseArray<ViewHolder> mCountdownVHList;
    private final List<MySqlGood> mySqlGoods;
    private Context mContext;
    private List<Good> mGoodList;
    private LayoutInflater mInflater;
    private Handler mHandler = new Handler();
    private Timer mTimer;
    private boolean isCancel = true;
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

    public MyGoodListViewAdapter(Context mContext, List<MySqlGood> mySqlGoods, List<Good> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.mInflater = LayoutInflater.from(mContext);
        this.mySqlGoods = mySqlGoods;
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
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            convertView = mInflater.inflate(R.layout.item_good_listview, null);
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

    //缓存类
    class ViewHolder implements View.OnClickListener {
        private TextView mTvUserName;
        private TextView mTvTime;
        private TextView mTvName;
        private ImageView mIvPic;
        private TextView mTvDescription;
        private CountdownView mCountdownView;
        private Good mGood;
        private TextView mTvNowPrice;
        private ImageView mIvUserAvatar;
        private TextView mTvThumbUp;
        private Integer position;
        private View conView;
        private TextView mTvGoodStatus;

        public void initView(View convertView) {
            conView = convertView;
            mTvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
            mCountdownView = (CountdownView) convertView.findViewById(R.id.cv_good);
            mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            mIvPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            mTvNowPrice = (TextView) convertView.findViewById(R.id.tv_now_price);
            mIvUserAvatar = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
            mTvThumbUp = (TextView) convertView.findViewById(R.id.tv_thumb_up);
            mTvGoodStatus = (TextView) convertView.findViewById(R.id.tv_good_status);
            mTvThumbUp.setOnClickListener(this);
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

        @TargetApi(Build.VERSION_CODES.KITKAT)
        private void initData(final int position) {
            this.position = position;
            if (mySqlGoods.size() != 0) {
                String mStr = mySqlGoods.get(position).getGood_Status();
//                Toast.makeText(mContext, position + mStr, Toast.LENGTH_SHORT).show();
                if (Objects.equals(mStr, "1")) {
                    mTvGoodStatus.setText("正在进行");
                } else if (Objects.equals(mStr, "0")) {
                    mTvGoodStatus.setText("已经结束");
                    mTvGoodStatus.setTextColor(Color.RED);
                } else {
                    mTvGoodStatus.setText("未知的错误");
                    mTvGoodStatus.setTextColor(Color.RED);
                }
            }
            mTvTime.setText(getItem(position).getCreatedAt());
            mTvName.setText(getItem(position).getGood_Name());
            mTvUserName.setText(getItem(position).getGood_User().getUser_Nickname());

//            Glide.with(mContext)
//                    .load(getItem(position).getGood_Photos().get(0).toString())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transform(new GlideCircleTransform(mContext))
//                    .into(mIvPic);

            Glide.with(mContext).load(getItem(position).getGood_Photos().get(0).toString()).into(mIvPic);

            mTvNowPrice.setText(getItem(position).getGood_NowCoin() + "");
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_thumb_up:
                    Good good = new Good();
                    good.increment("Good_Praise");
                    good.update(getItem(position).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(mContext, "点赞失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    break;
            }
        }
    }
}
