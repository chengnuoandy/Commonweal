package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goldenratio.commonweal.R;

/**
 * Created by Administrator on 2016/7/3.
 * 捐赠时弹出的页面
 */

public class HelpDonateActivity extends Activity implements View.OnClickListener {
    private RadioButton rb;
    private EditText et_other;
    private int radioButtonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_donate);
        initView();

        /*
        *  单选框的监听事件
        */
        RadioGroup group = (RadioGroup) findViewById(R.id.rg_money);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //获取变更后的选中项的ID
                radioButtonId = group.getCheckedRadioButtonId();
                Toast.makeText(getApplication(), "id" + radioButtonId, Toast.LENGTH_SHORT).show();

                /**
                 * 其他按钮
                 */

                if (radioButtonId == 2131624105) {
                    rb = (RadioButton) findViewById(radioButtonId);
                    findViewById(R.id.checkBox6).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            et_other.setVisibility(View.VISIBLE);
                            et_other.getText();
                            Toast.makeText(getApplication(), "被电击了" + et_other.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    et_other.setVisibility(View.GONE);
                    //根据ID获取RadioButton的实例
                    rb = (RadioButton) findViewById(radioButtonId);
                    // Toast.makeText(getApplication(),"充值"+rb.getText(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_donate_mo).setOnClickListener(this);
        et_other = (EditText) findViewById(R.id.et_other);
        //findViewById(R.id.ll_rb_other).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_back:
                finish();
                break;
            //充值
            case R.id.tv_donate_mo:
                if (radioButtonId == 2131624105) {
                    Toast.makeText(getApplicationContext(), "确认充值" + et_other.getText(), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "确认充值" + rb.getText(), Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
