package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.lib.GoodEditText;
import com.goldenratio.commonweal.ui.lib.GoodKeypad;

/**
 * Created by lvxue on 2016/6/20 0020.
 * 自定义键盘相关
 */
public class GoodKeypadActivity extends Activity implements View.OnFocusChangeListener,View.OnClickListener {
    private static final String TAG = "lxc";
    private GoodEditText mEditText1;
    private GoodEditText mEditText2;
    private TextView TVok;
    private ImageView IVclos;

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
        TVok = (TextView) findViewById(R.id.billSK_ok);
        IVclos = (ImageView) findViewById(R.id.billSK_hide);

        mEditText1.setOnFocusChangeListener(this);
        mEditText2.setOnFocusChangeListener(this);
        TVok.setOnClickListener(this);
        IVclos.setOnClickListener(this);

        //屏蔽键盘
        mEditText1.setInputType(InputType.TYPE_NULL);
        mEditText2.setInputType(InputType.TYPE_NULL);

        Intent mIntent = getIntent();
        mEditText1.setText(mIntent.getStringExtra("price"));
        mEditText2.setText(mIntent.getStringExtra("prop"));

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.release1:
                if (hasFocus)
                    new GoodKeypad(getWindow().getDecorView(), mEditText1);
                break;
            case R.id.release2:
                if (hasFocus)
                    new GoodKeypad(getWindow().getDecorView(), mEditText2);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.billSK_ok:
                returnData();
                finish();
                break;
            case R.id.billSK_hide:
                finish();
                break;
        }
    }

    /**
     * 回传数据
     */
    private void returnData() {
        Intent intent = new Intent();
        intent.putExtra("price",mEditText1.getText().toString());
        intent.putExtra("prop",mEditText2.getText().toString());
        setResult(RESULT_OK,intent);
    }
}