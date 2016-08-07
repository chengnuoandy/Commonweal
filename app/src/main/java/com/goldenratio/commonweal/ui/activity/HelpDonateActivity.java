package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.goldenratio.commonweal.R;

/**
 * Created by Administrator on 2016/7/3.
 * 捐赠时弹出的页面
 */

public class HelpDonateActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_donate);
    }

    private void initView(){
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
