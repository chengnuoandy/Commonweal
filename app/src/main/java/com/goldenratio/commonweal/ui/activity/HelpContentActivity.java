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

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.fragment.HelpContentFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016/6/22.
 */

public class HelpContentActivity extends Activity{
   private TextView tv_content_title;//标题
   private ImageView iv_content_pic;//图片
    private TextView tv_content_sponsor;//赞助方
    private TextView tv_content_initiator;//发起方&执行方
    private TextView tv_content_content;//项目简介



    private TextView tv_btn_reading;//阅读全文  点击
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_content_activity);


        //数据库填充内容
//        Help help = new Help();
//        help.setHelp_Content_content("也许有的人天生就很富有，但是没有人注定要贫穷。一个大多数不肯承认的事实是，贫穷的本质是认知的落后，而对财富认知的落后是其中一大因素为此，星公益希望征集20名乡村教师志愿者，为孩子们带来一节财商课，让争取的财富观在贫困的家庭的孩子心中发芽成长！");
//        help.setHelp_Initiator("山东公益");
//        help.setHelp_Sponsor("****公益基金");
//        help.save(HelpContentActivity.this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                Log.i("CN", "onSuccess: ++++++++++++++");
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                Log.i("CN", "onFailure++++++++++++: "+s);
//            }
//        });

        tv_content_title = (TextView) findViewById(R.id.tv_content_title);
        iv_content_pic = (ImageView) findViewById(R.id.iv_content_top);
        tv_content_sponsor = (TextView) findViewById(R.id.sponsor);
        tv_content_initiator = (TextView) findViewById(R.id.initiator);
        tv_content_content = (TextView) findViewById(R.id.tv_content_content);

        tv_btn_reading = (TextView) findViewById(R.id.tv_reading);

        //help_content_fragment中 TextViwe 的 阅读全文 的点击事件
        tv_btn_reading.setClickable(true);
        tv_btn_reading.setFocusable(true);
        tv_btn_reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mintent = new Intent(HelpContentActivity.this, HelpContentFragment.class);
                Log.i("CN", "onClick: ++++++++++++"+mintent);
                startActivity(mintent);
            }
        });



        //获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
         //获取屏幕信息
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;
        Log.i("CN", "onCreate:++screenWidth+++++++++++++++++++++++ "+screenWidth);
                int screenHeigh = dm.heightPixels;

        //获取HelpFragment传递过来的数据
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String pic = intent.getStringExtra("pic");
        String patron = intent.getStringExtra("patron");
        String sponsor = intent.getStringExtra("sponsor");
        String initiator = intent.getStringExtra("initiator");
        String content = intent.getStringExtra("content");
//        Log.d("CN", "Activity+++++++++++++onCreate: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+title);
        tv_content_title .setText(title);
        tv_content_sponsor.setText(patron);
        tv_content_initiator.setText(initiator);
        tv_content_sponsor.setText(sponsor);
        tv_content_content.setText(content);
        Picasso.with(HelpContentActivity.this).load(pic).resize(screenWidth,(screenHeigh/3)).into(iv_content_pic);//.override(screenWidth,200)

    }


}
