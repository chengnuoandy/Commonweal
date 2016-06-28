package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;

import cn.iwgang.countdownview.CountdownView;

public class GoodDetailActivity extends Activity {

    private CountdownView mCountdownView;
    private Long endTime;
    private Good mGood;

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
        Log.d("lxc", "initData: ----> " + mGood.getGoods_ID() +"endtime-->"+endTime);
        mCountdownView.start(endTime);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mCountdownView = (CountdownView) findViewById(R.id.cv_endtime);
    }
}
