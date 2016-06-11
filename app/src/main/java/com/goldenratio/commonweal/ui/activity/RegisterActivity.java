package com.goldenratio.commonweal.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.rl_code)
    RelativeLayout mRlCode;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;


    private String APPKEY = "139216e4958f6";
    private String APPSECRET = "63512a2fcc9c9e2f5c00bbdce60d920e";

    private String mPhone;  //暂存手机号
    private EventHandler mEh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        registerEventHandler();  //注册短信回调

    }

    //初始化短信回调
    private void registerEventHandler() {
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        mEh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(mEh);
    }

    //    发送验证码
    private void sendVerification() {
        Log.d("aaa", mPhone);
        SMSSDK.getVerificationCode("86", mPhone);
    }

    //    提交验证码
    private void submitVerification(String ver) {
        SMSSDK.submitVerificationCode("86", mPhone, ver);
    }

    //检测验证码提交状态
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                //  ((Throwable) data).printStackTrace();
                Log.d("ccc", ((Throwable) data).getMessage());
                String errorInfo = null;
                try {
                    //获取错误信息
                    errorInfo = new JSONObject(((Throwable) data).getMessage()).getString("detail");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), errorInfo, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEh);  //取消短信回调
    }

    @OnClick({R.id.btn_sendCode, R.id.btn_againSendCode, R.id.btn_commitCode, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sendCode:
                if (!TextUtils.isEmpty(mEtPhone.getText().toString())) {
                    Log.d("send", "发送");
                    mPhone = mEtPhone.getText().toString();
/*                    String regPhone = "(^(13\\d|15[^4,\\D]|17[13678]|18\\d)\\d{8}|170[^346,\\D]\\d{7})$";
                    Pattern p = Pattern.compile(regPhone);
                    Matcher m = p.matcher(mPhone);
                    m.matches();*/
                    sendVerification();
                } else {
                    Toast.makeText(RegisterActivity.this, "电话不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_againSendCode:
                if (!TextUtils.isEmpty(mPhone)) {
                    Log.d("again", "重新发送");
                    sendVerification();
                }
                break;
            case R.id.btn_commitCode:
                String verification = mEtCode.getText().toString();
                submitVerification(verification);
                Log.d("comm", "提交成功");
                break;
            case R.id.btn_register:
                break;
        }
    }
}
