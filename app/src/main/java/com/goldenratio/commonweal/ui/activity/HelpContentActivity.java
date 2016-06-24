package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.squareup.picasso.Picasso;

import org.xutils.x;

/**
 * Created by Administrator on 2016/6/22.
 */

public class HelpContentActivity extends Activity {
   private TextView tv_content_title;//标题
   private ImageView iv_content_pic;//图片
    private TextView tv_content_sponsor;//赞助方
    private TextView tv_content_initiator;//发起方&执行方
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_content_fragment);


        tv_content_title = (TextView) findViewById(R.id.tv_content_title);
        iv_content_pic = (ImageView) findViewById(R.id.iv_content_top);
        tv_content_sponsor = (TextView) findViewById(R.id.sponsor);
        tv_content_initiator = (TextView) findViewById(R.id.initiator);
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
        String initiator = intent.getStringExtra("initiator");
        Log.d("CN", "Activity+++++++++++++onCreate: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"+title);
        tv_content_title .setText(title);
        tv_content_sponsor.setText(patron);
        tv_content_initiator.setText(initiator);
        Picasso.with(HelpContentActivity.this).load(pic).resize(screenWidth,(screenHeigh/3)).into(iv_content_pic);//.override(screenWidth,200)

    }
}
