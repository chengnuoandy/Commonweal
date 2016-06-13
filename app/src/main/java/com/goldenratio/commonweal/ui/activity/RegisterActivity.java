package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import java.security.KeyStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/*
*
* fengmtx创建于2016/06/01
*
* */
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
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Log.d("ccc", mTvPhone.getTextColors() + "");
        registerEventHandler();  //注册短信回调

        addTextChangeEvent(mEtPhone);
        addTextChangeEvent(mEtCode);

    }

    //添加文本改变监听事件
    private void addTextChangeEvent(EditText mEtInput) {
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEtPhone.getText().length() >= 11 && mEtCode.getText().length() == 0) {
                    mBtnSendCode.setEnabled(true);
                    mBtnSendCode.setBackgroundResource(R.drawable.register_reset);
                } else if (mEtCode.getText().length() == 4) {
                    mBtnCommitCode.setEnabled(true);
                    mBtnCommitCode.setBackgroundResource(R.drawable.register_reset);
                } else {
                    mBtnSendCode.setEnabled(false);
                    mBtnCommitCode.setEnabled(false);
                    mBtnSendCode.setBackgroundResource(R.drawable.register_default);
                    mBtnCommitCode.setBackgroundResource(R.drawable.register_default);
                    
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

                   /* showWhichStep(View.GONE, View.VISIBLE, View.GONE);
                    changeStepTextColor(R.color.ordinary, R.color.main_hue, R.color.ordinary);
*/
                    sendVerification();
                } else {
                    Toast.makeText(getApplicationContext(), "电话不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_againSendCode:
                if (!TextUtils.isEmpty(mPhone)) {
                    Log.d("again", "重新发送");
                    sendVerification();
                    mBtnAgainSendCode.setBackgroundResource(R.drawable.register_default);
                }
                break;
            case R.id.btn_commitCode:
                String verification = mEtCode.getText().toString();
                submitVerification(verification);

             /*   showWhichStep(View.GONE, View.GONE, View.VISIBLE);
                changeStepTextColor(R.color.ordinary, R.color.ordinary, R.color.main_hue);
*/
                Log.d("comm", "提交验证码");
                break;
            case R.id.btn_register:
                if (!TextUtils.isEmpty(mEtPassword.getText().toString())) {
                    String mPassword = mEtPassword.getText().toString();
                    Log.d("pass", "注册");
                    if (mPassword.length() >= 8) {
                        //  ^[a-zA-Z]\w{5,17}$
                        if (checkPassword(mPassword))
                            addUserInfoToDB();
                        else
                            Toast.makeText(RegisterActivity.this, "密码强度过低", Toast.LENGTH_SHORT).show();
                    } else
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
                    changeStepTextColor(R.color.ordinary, R.color.main_hue, R.color.ordinary);

                    new TimeCount(60000, 1000).start();
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    showWhichStep(View.GONE, View.GONE, View.VISIBLE);
                    changeStepTextColor(R.color.ordinary, R.color.ordinary, R.color.main_hue);
                }
            } else {
                //  ((Throwable) data).printStackTrace();
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
            mBtnAgainSendCode.setBackgroundResource(R.drawable.register_reset);
            mBtnAgainSendCode.setText("重发");
        }

    }

    private boolean addUserInfoToDB() {
        boolean result = false;
        User u = new User();
        u.setUser_Phone(mPhone);
        u.setUser_Password(mEtPassword.getText().toString());
        u.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                returnUInfoToMyFra();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "注册失败，请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    //返回用户信息给myFragment
    private void returnUInfoToMyFra() {
        Intent intent = new Intent();
        intent.putExtra("regi_phone", mPhone);
        intent.putExtra("regi_password", mEtPassword.getText().toString());
        setResult(RESULT_OK, intent);
        Log.d("return", "已返回数据");
        finish();
    }

    private void checkWhichStep() {
        Log.d("111", "弹出提示");
        if (mTvCode.getTextColors() == getResources().getColorStateList(R.color.main_hue) ||
                mTvPassword.getTextColors() == getResources().getColorStateList(R.color.main_hue)) {
            Log.d("333", "成功判断");
            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("您确定要返回到注册前的界面？");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.show();
            Log.d("222", "弹出成功");
        } else finish();
    }

    private boolean checkPassword(String pw) {
        String regPw = "[\\da-zA-Z]*\\d+[a-zA-Z]+[\\da-zA-Z]*";
        Pattern p = Pattern.compile(regPw);
        Matcher m = p.matcher(pw);
        return m.matches();
    }

    @Override
    public void onBackPressed() {
        checkWhichStep();
    }
}