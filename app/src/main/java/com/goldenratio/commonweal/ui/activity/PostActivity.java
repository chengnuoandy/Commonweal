package com.goldenratio.commonweal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;

import cn.bmob.v3.BmobPushManager;

/**
 * Created by wenmingvs on 16/5/2.
 */
public class PostActivity extends BaseActivity {

    private Context mContext;
    private ImageView composeDy;
    private ImageView composeGood;
    private ImageView composeClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postfragment_layout);

        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        mContext = this;
        composeDy = (ImageView) findViewById(R.id.compose_dy);
        composeGood = (ImageView) findViewById(R.id.compose_good);
        composeClose = (ImageView) findViewById(R.id.compose_close);

        composeDy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DynamicReleaseActivity.class);
                startActivity(intent);
                finish();
            }
        });
        composeGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean userIsV = ((MyApplication) (getApplicationContext())).isUserIsV();
                if (userIsV) {
                    Intent intent = new Intent(mContext, GoodActivity.class);
                    startActivity(intent);
                    BmobPushManager bmobPush = new BmobPushManager();
                    bmobPush.pushMessage("p-我买了您的物品-么么哒");
                    finish();
                } else {
                    Toast.makeText(mContext, "请先进行名人认证吧！" + userIsV, Toast.LENGTH_SHORT).show();
                }
            }
        });

        composeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
