package com.goldenratio.commonweal.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.ui.activity.GoodDetailActivity;
import com.goldenratio.commonweal.ui.activity.StarInfoActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Kiuber on 2016/6/26.
 * 物品列表适配器相关
 */

public class MyGoodListViewAdapter extends BaseAdapter {
    private static final String TAG = "lxc";
    private final SparseArray<ViewHolder> mCountdownVHList;
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
    private final int width;
    private final int height;

    public MyGoodListViewAdapter(Context mContext, List<Good> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.mInflater = LayoutInflater.from(mContext);
        mCountdownVHList = new SparseArray<>();
        startRefreshTime();


        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
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
        private CircleImageView mCivAvatar;
        private TextView mTvUserName;
        private CountdownView mCountdownView;
        private TextView mTvStatus;
        private TextView mTvName;
        private TextView mTvNowCoin;
        private ImageView mIvPic;
        private TextView mTvTime;
        private ImageView mIvThumbUp;
        private ImageView mIvShare;
        private Good mGood;
        private Integer position;
        private CardView mCv;

        public void initView(View convertView) {
            mCivAvatar = (CircleImageView) convertView.findViewById(R.id.civ_user_avatar);
            mTvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
            mCountdownView = (CountdownView) convertView.findViewById(R.id.cv_good);
            mTvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            mTvNowCoin = (TextView) convertView.findViewById(R.id.tv_now_coin);
            mIvPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            mIvThumbUp = (ImageView) convertView.findViewById(R.id.iv_thumb_up);
            mIvShare = (ImageView) convertView.findViewById(R.id.iv_share);
            mCv = (CardView) convertView.findViewById(R.id.cv_good_item);

            mIvThumbUp.setOnClickListener(this);
            mIvShare.setOnClickListener(this);
            mCivAvatar.setOnClickListener(this);
            mCv.setOnClickListener(this);
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
            mTvTime.setText(getItem(position).getCreatedAt());
            mTvName.setText(getItem(position).getGood_Name());
            mTvUserName.setText(getItem(position).getGood_User().getUser_Nickname());

//            Glide.with(mContext)
//                    .load(getItem(position).getGood_Photos().get(0).toString())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transform(new GlideCircleTransform(mContext))
//                    .into(mIvPic);
            //TODO 图片尺寸，物品详情页的状态
            Glide.with(mContext).load(getItem(position).getGood_User().getUser_image_hd()).into(mCivAvatar);
            Glide.with(mContext).load(getItem(position).getGood_Photos().get(0).toString()).override(width * 2 / 3, height / 3).into(mIvPic);
            String good_nowCoin = getItem(position).getGood_NowCoin();
            String good_startCoin = getItem(position).getGood_StartCoin();
            if (TextUtils.equals(good_nowCoin, (good_startCoin))) {
                mTvNowCoin.setText("暂未出价");
            } else {
                mTvNowCoin.setText(getItem(position).getGood_NowCoin());
            }
            long nowTime = System.currentTimeMillis();
            long endTime = getItem(position).getGood_UpDateM();
            long result = nowTime - endTime;
            if (result > 0) {
                mTvStatus.setText("已经结束");
                mCountdownView.setVisibility(View.GONE);
                mTvStatus.setTextColor(Color.RED);
                mTvStatus.setBackground(mContext.getResources().getDrawable(R.drawable.default_frame));
            } else if (result < 0) {
                mTvStatus.setText("正在进行");
            } else {
                mTvStatus.setText("未知状态");
                mTvStatus.setTextColor(Color.RED);
                mTvStatus.setBackground(null);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_thumb_up:
                    Good good = new Good();
                    good.increment("Good_Praise");
                    good.update(getItem(position).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();

                            } else {
//                                Toast.makeText(mContext, "点赞失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                                ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                            }
                        }

                    });
                    break;
                case R.id.civ_user_avatar:
                    MyApplication myApplication = (MyApplication) ((Activity) mContext).getApplication();
                    String mStrObjectId = myApplication.getObjectID();
                    if (!mGoodList.get(position).getGood_User().getObjectId().equals(mStrObjectId)) {
                        List<String> attenList;
                        attenList = mGoodList.get(position).getGood_User().getUser_Attention();
                        int isHas = -1;
                        if (attenList != null)
                            isHas = attenList.indexOf(mGoodList.get(position).getGood_User().getObjectId());
                        Intent intent = new Intent(mContext, StarInfoActivity.class);
                        intent.putExtra("ishas", isHas != -1);
                        intent.putExtra("id", mGoodList.get(position).getGood_User().getObjectId());
                        intent.putExtra("autograph", mGoodList.get(position).getGood_User().getUser_Autograph());
                        intent.putExtra("nickName", mGoodList.get(position).getGood_User().getUser_Nickname());
                        intent.putExtra("isv", mGoodList.get(position).getGood_User().isUser_IsV());
                        intent.putExtra("Avatar", mGoodList.get(position).getGood_User().getUser_image_hd());
                        mContext.startActivity(intent);
                    }
                    break;
                case R.id.cv_good_item:
                    Intent intent = new Intent(mContext, GoodDetailActivity.class);
                    intent.putExtra("objectId", mGoodList.get(position).getObjectId());
                    mContext.startActivity(intent);
                    break;
            }
        }
    }
}
