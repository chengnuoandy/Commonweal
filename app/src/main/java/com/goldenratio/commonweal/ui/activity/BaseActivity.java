package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by Kiuber on 2016/9/14.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiStatInterface.recordPageStart(this, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MiStatInterface.recordPageStart(this, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
        MobclickAgent.onPause(this);
    }
}
