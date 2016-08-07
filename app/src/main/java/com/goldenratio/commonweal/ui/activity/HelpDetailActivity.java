package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.goldenratio.commonweal.onekeyshare.OnekeyShare;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Administrator on 2016/6/22.
 */

public class HelpDetailActivity extends Activity implements View.OnClickListener {


    private Help mHelp;
    private ImageView mIvTop;
    private TextView mTvTitle;
    private TextView mTvInitiator;
    private TextView mTvContent;
    private TextView mTvSmile;
    private TextView mTvAll;
    private EditText mEtSpeak;
    private LinearLayout mLayout;
    private ListView mlistView;
    private TextView mTvExecute;

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
        mTvInitiator.setText(mHelp.getHelp_Initiator());
        mTvExecute.setText(mHelp.getHelp_Execute());
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
        mTvInitiator = (TextView) findViewById(R.id.tv_initiator);
        mTvExecute = (TextView) findViewById(R.id.tv_execute);
        mTvSmile = (TextView) findViewById(R.id.smile);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mTvAll = (TextView) findViewById(R.id.tv_all);
        mEtSpeak = (EditText) findViewById(R.id.et_speak);
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
                showShare();
                break;

            //评论
            case R.id.iv_comment:
                mEtSpeak.requestFocus();
                mEtSpeak.setFocusable(true);
                mEtSpeak.setFocusableInTouchMode(true);
                break;

            //捐赠
            case R.id.tv_donate:
                Intent i = new Intent(HelpDetailActivity.this, HelpDonateActivity.class);
                startActivity(i);
                break;

            //发送按钮
            case R.id.btn_speak:
                String speak = mEtSpeak.getText().toString();
                Toast.makeText(HelpDetailActivity.this, "" + speak, Toast.LENGTH_SHORT).show();
                //获取发送信息，和发送人信息

                break;
        }
    }


    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.ssdk_oks_share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
