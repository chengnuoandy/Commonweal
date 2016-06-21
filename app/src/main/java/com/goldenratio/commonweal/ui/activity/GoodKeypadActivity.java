package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.lib.GoodEditText;
import com.goldenratio.commonweal.ui.lib.GoodKeypad;

/**
 * Created by lvxue on 2016/6/20 0020.
 * 自定义键盘相关
 */
public class GoodKeypadActivity extends Activity implements View.OnFocusChangeListener {
    private static final String TAG = "lxc";
    private GoodEditText mEditText1;
    private GoodEditText mEditText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_good_price);
        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;

        window.setAttributes(lp);

        mEditText1 = (GoodEditText) findViewById(R.id.release1);
        mEditText2 = (GoodEditText) findViewById(R.id.release2);
        mEditText1.setOnFocusChangeListener(this);
        mEditText2.setOnFocusChangeListener(this);

        //屏蔽键盘
        mEditText1.setInputType(InputType.TYPE_NULL);
        mEditText2.setInputType(InputType.TYPE_NULL);


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.release1:
                if (hasFocus)
                    new GoodKeypad(getWindow().getDecorView(), mEditText1);
                Log.d(TAG, "onFocusChange: 1");
                break;
            case R.id.release2:
                if (hasFocus)
                    new GoodKeypad(getWindow().getDecorView(), mEditText2);
                break;
        }
    }
}