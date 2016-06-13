package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.goldenratio.commonweal.R;

/**
 * Created by Administrator on 2016/6/11.
 *
 */

public class GoodActivity extends Activity {

    private Button mBtnPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good);
    }
}