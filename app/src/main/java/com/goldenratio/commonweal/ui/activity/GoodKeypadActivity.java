package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.lib.GoodEditText;
import com.goldenratio.commonweal.ui.lib.GoodKeypad;

import java.math.BigDecimal;

/**
 * Created by lvxue on 2016/6/20 0020.
 * 自定义键盘相关
 */
public class GoodKeypadActivity extends Activity implements View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "lxc";
    private static final int MAX_MARK = 100;
    private static final int MIN_MARK = 0;
    private GoodEditText mEditText1;
    private GoodEditText mEditText2;
    private TextView TVok;
    private ImageView IVclos;
    private TextView TVshow;

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
        TVshow = (TextView) findViewById(R.id.show_re);

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

        mEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (start > 1) {
                        int num = Integer.parseInt(s.toString());
                        if (num > MAX_MARK) {
                            s = String.valueOf(MAX_MARK);
                            mEditText2.setText(s);
                        } else if (num < MIN_MARK)
                            s = String.valueOf(MIN_MARK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /**
                 * 限制捐款输入范围 0 - 100
                 */
                float temp1 = Float.parseFloat(mEditText1.getText().toString()); //底价
                float temp2 = Float.parseFloat(s.toString()) / 100; //比例
                if (s != null && !s.toString().equals("")) {
                    int markVal = 0;
                    try {
                        markVal = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        markVal = 0;
                    }
                    if (markVal > MAX_MARK) {
                        Toast.makeText(getBaseContext(), "比例不能超过100", Toast.LENGTH_SHORT).show();
                        mEditText2.setText(String.valueOf(MAX_MARK));
                        temp2 = 1;
                    }
                    //设置textview的显示,保留两位小数
                    BigDecimal   b  =   new  BigDecimal(temp1 * temp2);
                    float   f   =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();
                    TVshow.setText("您将捐出：" + f + "  剩余：" + (temp1 - (temp1 * temp2)));
                    Log.d(TAG, "afterTextChanged: " + temp1 + ":" + s.toString() + ":" + temp2);
                    return;
                }
                TVshow.setText("您未设置捐款");
            }
        });
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
        switch (v.getId()) {
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
        intent.putExtra("price", mEditText1.getText().toString());
        intent.putExtra("prop", mEditText2.getText().toString());
        setResult(RESULT_OK, intent);
    }


}