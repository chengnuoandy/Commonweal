package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.util.ShareUtils;

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
    private LinearLayout mLayout;
    private TextView mTvExecute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mHelp = (Help) getIntent().getSerializableExtra("HelpList");
        initView();
        Glide.with(this).load(mHelp.getHelp_Pic()).into(mIvTop);
        mTvTitle.setText(mHelp.getHelp_Title());
        mTvInitiator.setText(mHelp.getHelp_Initiator());
        mTvExecute.setText(mHelp.getHelp_Execute());
        mTvSmile.setText(mHelp.getHelp_Smile());
        mTvContent.setText(mHelp.getHelp_Content());
        Close();
    }


    //失去焦点
    private void Close() {
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
        mLayout = (LinearLayout) findViewById(R.id.help_layout);
        mTvInitiator.setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_comment).setOnClickListener(this);
        findViewById(R.id.tv_donate).setOnClickListener(this);
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
                ShareUtils shareUtils = new ShareUtils();
                shareUtils.showShare(this);
                break;

            //评论
            case R.id.iv_comment:
                //开启评论页面
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Help", mHelp);
                intent.putExtras(bundle);

                startActivity(intent);
                break;

            //捐赠
            case R.id.tv_donate:
                Intent i = new Intent(HelpDetailActivity.this, HelpDonateActivity.class);
                startActivity(i);
                break;
            //发起方详情界面
            case R.id.tv_initiator:
                Intent intent2 = new Intent(this,HelpInitiatorDetailActivity.class);
                intent2.putExtra("name",mHelp.getHelp_Initiator());
                startActivity(intent2);
                break;

        }
    }

}
