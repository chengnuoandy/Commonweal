package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Kiuber on 2016/6/26.
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
    public String getItem(int position) {
        return mGoodList.get(position).getGoods_Name();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            convertView = mInflater.inflate(R.layout.view_good_all, null);
            viewHolder = new ViewHolder();
            viewHolder.initView(convertView); //初始化
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Good mGood = mGoodList.get(position);
        viewHolder.bindData(mGood);

        // 处理倒计时
        if (mGood.getGoods_UpDateM() > 0) {
            synchronized (mCountdownVHList) {
//                mCountdownVHList.put(Integer.parseInt(mGood.getGoods_ID()), viewHolder);
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
                    if (currentTime >= curMyViewHolder.getBean().getGoods_UpDateM()) {
                        // 倒计时结束
                        curMyViewHolder.getBean().setGoods_UpDateM((long) 0);
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

    class ViewHolder {
        private TextView mTvStarName;
        private CountdownView mCountdownView;
        private Good mGood;

        public void initView(View convertView) {
            mTvStarName = (TextView) convertView.findViewById(R.id.tv_star_name);
            mCountdownView = (CountdownView) convertView.findViewById(R.id.cv_good);
        }

        //初始化数据
        public void bindData(Good Good) {
            this.mGood = Good;

            if (mGood.getGoods_UpDateM() > 0) {
                refreshTime(System.currentTimeMillis()); //暂时用本地时间
            } else {
                mCountdownView.allShowZero();
            }

            mTvStarName.setText(mGood.getGoods_Name());
        }

        public void refreshTime(long curTimeMillis) {
            if (null == mGood || mGood.getGoods_UpDateM() <= 0) return;
            //更新时间
            mCountdownView.updateShow(mGood.getGoods_UpDateM() - curTimeMillis);
        }

        public Good getBean(){
            return mGood;
        }
    }
}
