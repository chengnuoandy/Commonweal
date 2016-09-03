package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
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
import com.goldenratio.commonweal.bean.User_Profile;

import java.util.List;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wenmingvs on 16/5/2.
 */
public class PostActivity extends Activity {

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
                BmobQuery<User_Profile> user_profileBmobQuery = new BmobQuery<User_Profile>();
                String objectID = ((MyApplication) (getApplication())).getObjectID();
                user_profileBmobQuery.addWhereEqualTo("User_IsV", objectID);
                user_profileBmobQuery.addQueryKeys("User_IsV");
                user_profileBmobQuery.findObjects(new FindListener<User_Profile>() {
                    @Override
                    public void done(List<User_Profile> list, BmobException e) {
                        if (list.size() == 0) {
                            Toast.makeText(mContext, "未找到当前用户信息", Toast.LENGTH_SHORT).show();
                        } else if (list.size() == 1) {
                            if (list.get(0).isUser_IsV()) {
                                Intent intent = new Intent(mContext, GoodActivity.class);
                                startActivity(intent);
                                BmobPushManager bmobPush = new BmobPushManager();
                                bmobPush.pushMessage("p-我买了您的物品-么么哒");
                                finish();
                            } else {
                                Toast.makeText(mContext, "请先进行名人认证吧！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
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
