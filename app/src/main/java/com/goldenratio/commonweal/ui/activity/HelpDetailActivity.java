package com.goldenratio.commonweal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.goldenratio.commonweal.util.ShareUtils;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Kiuber on 2016/6/22.
 */

public class HelpDetailActivity extends BaseActivity implements View.OnClickListener {


    private Help mHelp;
    private ImageView mIvTop;
    private TextView mTvTitle;
    private TextView mTvInitiator;
    private TextView mTvContent;
    private TextView mTvSmile;
    private LinearLayout mLayout;
    private TextView mTvExecute;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_help);

        mHelp = (Help) getIntent().getSerializableExtra("HelpList");
        initView();
        Glide.with(this).load(mHelp.getHelp_Pic()).into(mIvTop);
        mTvTitle.setText(mHelp.getHelp_Title());
        mTvInitiator.setText(mHelp.getHelp_Initiator());
        mTvExecute.setText(mHelp.getHelp_Execute());
        mTvSmile.setText(mHelp.getHelp_Smile());
        mTvContent.setText(mHelp.getHelp_Content());
        Close();
        new ImmersiveUtil(this, R.color.white, true);
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
        mLayout = (LinearLayout) findViewById(R.id.help_layout);
        mTvInitiator.setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_comment).setOnClickListener(this);
        findViewById(R.id.tv_donate).setOnClickListener(this);
        findViewById(R.id.tv_all).setOnClickListener(this);
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
                Intent intent0 = new Intent(this, DonateInfoActivity.class);
                intent0.putExtra("help_id", mHelp.getObjectId());
                startActivity(intent0);
                break;

            //分享
            case R.id.iv_share:
                ShareUtils shareUtils = new ShareUtils();
                shareUtils.showShare(this);
                break;

            //评论
            case R.id.iv_comment:
                //开启评论页面
                Intent intent = new Intent(getApplicationContext(), HelpCommentActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Help", mHelp);
                intent.putExtras(bundle);

                startActivity(intent);
                break;

            //捐赠
            case R.id.tv_donate:
                String mUserId = ((MyApplication) getApplication()).getObjectID();
                if (mUserId.equals("")) {
                    Toast.makeText(this, "您尚未登陆，请登陆后再试", Toast.LENGTH_SHORT).show();
                } else {
                    long endTime = BmobDate.getTimeStamp(mHelp.getHelp_EndDate().getDate());
                    long leftTime = (endTime - System.currentTimeMillis()) / (86400000);
                    if (leftTime > 0) {
                        Intent i = new Intent(HelpDetailActivity.this, HelpDonateActivity.class);
                        i.putExtra("nowCoin",mHelp.getHelp_Now_Coin());
                        i.putExtra("help_id", mHelp.getObjectId());
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "项目已经结束", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //发起方详情界面
            case R.id.tv_initiator:
                Intent intent2 = new Intent(this, HelpInitiatorDetailActivity.class);
                intent2.putExtra("name", mHelp.getHelp_Initiator());
                startActivity(intent2);
                break;
            case R.id.tv_all:
                Intent intent1 = new Intent(this, HelpTopDetailActivity.class);
                intent1.putExtra("TopUrl", mHelp.getHelp_Url());
                startActivity(intent1);
                break;

        }
    }
}
