package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;

import cn.iwgang.countdownview.CountdownView;

public class GoodDetailActivity extends Activity {

    private CountdownView mCountdownView;
    private Long endTime;
    private Good mGood;
    private TextView mTvGoodName;
    private TextView mTvGoodDescription;
    private TextView mTvUserName;
    private GridView mGvPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        initData();
    }

    /**
     * 初始化数据
     * 从intent获取传过来的数据
     * 设置相应控件的数据显示
     */
    private void initData() {
        Intent intent = getIntent();
        endTime = intent.getLongExtra("EndTime", 0);
        mGood = (Good) intent.getSerializableExtra("Good");
        Log.d("lxc", "initData: ----> " + mGood.getGood_ID() + "endtime-->" + endTime);
        mCountdownView.start(endTime);

        mTvGoodName.setText(mGood.getGood_Name());
        mTvGoodDescription.setText(mGood.getGood_Description());
    //    mGvPic.setAdapter(new mAdapter(GoodDetailActivity.this));
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mTvGoodName = (TextView) findViewById(R.id.tv_good_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mCountdownView = (CountdownView) findViewById(R.id.cv_endtime);
        mTvGoodDescription = (TextView) findViewById(R.id.tv_good_description);
        mGvPic = (GridView) findViewById(R.id.gv_show_detail_pic);
    }

    class mAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public mAdapter(Context context) {
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mGood.getGood_Photos().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (viewHolder == null) {
                convertView = mLayoutInflater.inflate(R.layout.view_good_detail, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_good_detail_pic_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Glide.with(getBaseContext()).load(mGood.getGood_Photos().get(position)).into(viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }
    }
}
