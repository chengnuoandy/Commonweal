package com.goldenratio.commonweal.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User;

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
    @BindView(R.id.btn_sendCode)
    Button mBtnSendCode;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.btn_againSendCode)
    Button mBtnAgainSendCode;
    @BindView(R.id.rl_code)
    RelativeLayout mRlCode;
    @BindView(R.id.btn_commitCode)
    Button mBtnCommitCode;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_register)
    Button mBtnRegister;
    @BindView(R.id.tv_phone)
    TextView mTvPhone;
    @BindView(R.id.tv_code)
    TextView mTvCode;
    @BindView(R.id.tv_password)
    TextView mTvPassword;
    @BindView(R.id.ch_check)
    CheckBox chCheck;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;

    private String APPKEY = "139216e4958f6";
    private String APPSECRET = "63512a2fcc9c9e2f5c00bbdce60d920e";

    private String mPhone;  //暂存手机号
    private EventHandler mEh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("ccc", mTvPhone.getTextColors() + "");
        registerEventHandler();  //注册短信回调

        //设置文本改变监听事件
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEtPhone.getText().length() >= 11) {
                    mBtnSendCode.setEnabled(true);
                } else if (mEtCode.getText().length() >= 1) {
                    mBtnCommitCode.setEnabled(true);
                } else {
                    mBtnSendCode.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEh);  //取消短信回调
    }

    @OnClick({R.id.btn_againSendCode, R.id.btn_sendCode, R.id.btn_commitCode, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sendCode:
                if (!TextUtils.isEmpty(mEtPhone.getText().toString())) {
                    mPhone = mEtPhone.getText().toString();

                    Log.d("send", "发送");
                    showWhichStep(View.GONE, View.VISIBLE, View.GONE);
                    changeStepTextColor(R.color.ordinary, R.color.colorPrimary, R.color.ordinary);
                    new TimeCount(60000, 1000).start();

/*                    String regPhone = "(^(13\\d|15[^4,\\D]|17[13678]|18\\d)\\d{8}|170[^346,\\D]\\d{7})$";
                    Pattern p = Pattern.compile(regPhone);
                    Matcher m = p.matcher(mPhone);
                    m.matches();*/

                    sendVerification();
                } else {
                    Toast.makeText(getApplicationContext(), "电话不能为空", Toast.LENGTH_SHORT).show();
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

                showWhichStep(View.GONE, View.GONE, View.VISIBLE);
                changeStepTextColor(R.color.ordinary, R.color.ordinary, R.color.colorPrimary);

                Log.d("comm", "提交成功");
                break;
            case R.id.btn_register:
                if (!TextUtils.isEmpty(mEtPassword.getText().toString())) {
                    String mPassword = mEtPassword.getText().toString();
                    Log.d("pass", "注册");
                    if (mPassword.length() >= 8)
                        //  ^[a-zA-Z]\w{5,17}$
                        addUserinfo();
                    else
                        Toast.makeText(getApplicationContext(), "密码不能低于8位", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
        Log.d("phone", mPhone);
        SMSSDK.getVerificationCode("86", mPhone);
    }

    //    提交验证码
    private void submitVerification(String ver) {
        SMSSDK.submitVerificationCode("86", mPhone, ver);
    }

    // 显示哪一步
    private void showWhichStep(int first, int second, int third) {
        mEtPhone.setVisibility(first);
        mBtnSendCode.setVisibility(first);

        mRlCode.setVisibility(second);
        mBtnCommitCode.setVisibility(second);

        mEtPassword.setVisibility(third);
        mBtnRegister.setVisibility(third);

        Log.d("sh", "0000");
    }

    //改变步骤标题的文本颜色
    private void changeStepTextColor(int color1, int color2, int color3) {
        mTvPhone.setTextColor(getResources().getColor(color1));
        mTvCode.setTextColor(getResources().getColor(color2));
        mTvPassword.setTextColor(getResources().getColor(color3));
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
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();

                    showWhichStep(View.GONE, View.VISIBLE, View.GONE);
                    changeStepTextColor(R.color.ordinary, R.color.colorPrimary, R.color.ordinary);

                    new TimeCount(60000, 1000).start();
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    showWhichStep(View.GONE, View.GONE, View.VISIBLE);
                    changeStepTextColor(R.color.ordinary, R.color.ordinary, R.color.colorPrimary);
                }
            } else {
                //  ((Throwable) data).printStackTrace();
                if (mBtnCommitCode.getText() == "注册") {
                } else {
                    Log.d("ccc", ((Throwable) data).getMessage());
                    String errorInfo = null;
                    try {
                        //获取错误信息
                        errorInfo = new JSONObject(((Throwable) data).getMessage()).getString("detail");
                        Toast.makeText(getApplicationContext(), errorInfo, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "亲，没有网络哟", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    //实现按钮倒计时
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            mBtnAgainSendCode.setEnabled(false);
            mBtnAgainSendCode.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {// 计时完毕
            mBtnAgainSendCode.setEnabled(true);
            mBtnAgainSendCode.setText("重发");
        }
    }

    private void addUserinfo() {
        User u = new User();
        u.setUser_Phone(mPhone);
        u.setUser_Password(mEtPassword.getText().toString());
        u.save(this);
    }

}