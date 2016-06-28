package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Top;
import com.goldenratio.commonweal.ui.fragment.HelpContentFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/6/22.
 */

public class HelpDetailActivity extends Activity implements View.OnClickListener {

    private Help mHelp;
    private ImageView mIvTop;
    private TextView mTvTitle;
    private TextView mTvOrg;
    private TextView mTvContent;
    private TextView mTvSmile;
    private TextView mTvAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mHelp = (Help) getIntent().getSerializableExtra("HelpList");
        DisplayMetrics dm = new DisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;

        initView();

//        Picasso.with(HelpDetailActivity.this).load(mHelp.getHelp_Top_pic()).into(mIvTop);//.override(screenWidth,200)
        Glide.with(this).load(mHelp.getHelp_Pic()).into(mIvTop);
        mTvTitle.setText(mHelp.getHelp_Title());
        mTvOrg.setText(mHelp.getHelp_Org());
        mTvSmile.setText(mHelp.getHelp_Smile());
        mTvContent.setText(mHelp.getHelp_Content());
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_rank).setOnClickListener(this);
        mIvTop = (ImageView) findViewById(R.id.iv_top);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvOrg = (TextView) findViewById(R.id.tv_org);
        mTvSmile = (TextView) findViewById(R.id.smile);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mTvAll = (TextView) findViewById(R.id.tv_all);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_comment).setOnClickListener(this);
        findViewById(R.id.tv_donate).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_rank:
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_comment:
                break;
            case R.id.tv_donate:
                break;
        }
    }
}
