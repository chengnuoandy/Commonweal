package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText mEtSpeak;
    private LinearLayout mLayout;
    private ListView mlistView;

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

        //失去焦点
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭软键盘？？
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(HelpDetailActivity.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
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
        mEtSpeak= (EditText) findViewById(R.id.et_speak);
        mLayout = (LinearLayout) findViewById(R.id.help_layout);
//        mBten = (Button) findViewById(R.id.btn_speak);
        mlistView = (ListView) findViewById(R.id.lv_helpspeak);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_comment).setOnClickListener(this);
        findViewById(R.id.tv_donate).setOnClickListener(this);
        findViewById(R.id.btn_speak).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_back:
                finish();
                break;

            //奖杯图标 查看排名
            case R.id.iv_rank:
                break;

            //分享
            case R.id.iv_share:
                break;

            //评论
            case R.id.iv_comment:
                mEtSpeak.requestFocus();
                mEtSpeak.setFocusable(true);
                mEtSpeak.setFocusableInTouchMode(true);
                break;

            //捐赠
            case R.id.tv_donate:
                Intent i = new Intent(HelpDetailActivity.this,HelpDonateActivity.class);
                startActivity(i);
                break;

            //发送按钮
            case R.id.btn_speak:
                String speak = mEtSpeak.getText().toString();
                Toast.makeText(HelpDetailActivity.this, ""+speak, Toast.LENGTH_SHORT).show();
                //获取发送信息，和发送人信息

                break;
        }
    }
}
