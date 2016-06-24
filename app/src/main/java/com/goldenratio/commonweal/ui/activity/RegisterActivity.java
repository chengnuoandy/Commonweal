package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User;
import com.goldenratio.commonweal.util.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by 龙啸天 on 2016/6/20 0020.
 * <p/>
 * 承担注册与找回密码的功能
 */

public class RegisterActivity extends Activity {

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
    @BindView(R.id.tv_registerTitle)
    TextView mTvRegisterTitle;
    @BindView(R.id.ll_agreement)
    LinearLayout mLlAgreement;

    private String APPKEY = "139216e4958f6";
    private String APPSECRET = "63512a2fcc9c9e2f5c00bbdce60d920e";

    private ProgressDialog mPd;
    private String mPhone;  //暂存手机号
    private EventHandler mEh;

    private String mObjectId;
    private boolean isClickRegisterBtn = false;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        isClickRegister();

        if (!isClickRegisterBtn) {
            mTvRegisterTitle.setText("找回密码");
            mBtnRegister.setText("修改");
            mLlAgreement.setVisibility(View.GONE);
        }

        registerEventHandler();  //注册短信回调

        addTextChangeEvent(mEtPhone);
        addTextChangeEvent(mEtCode);

    }

    /**
     * 由LoginActivity传来的数据判断
     * 是否点击了注册按钮
     */
    private void isClickRegister() {
        Intent intent = getIntent();
        int code = intent.getIntExtra("type", 1);
        if (code == 0)
            isClickRegisterBtn = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEh);  //取消短信回调
    }

    /**
     * 为控件添加监听事件
     */

    //添加文本改变监听事件
    private void addTextChangeEvent(EditText mEtInput) {
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //   设置控件的相关属性
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

    //    为按钮添加点击事件
    @OnClick({R.id.btn_againSendCode, R.id.btn_sendCode, R.id.btn_commitCode, R.id.btn_register, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sendCode:
                mPhone = mEtPhone.getText().toString();
                Log.d("send", "发送");

                //getUDefAvatarUrl();
                showProgressDialog();
                Log.d("dialog", "显示进度条对话框");
                isRegister();
                break;
            case R.id.btn_againSendCode:
                if (!TextUtils.isEmpty(mPhone)) {
                    Log.d("again", "重新发送");
                    showProgressDialog();
                    sendVerification();
                    mEtCode.setText("");
                    mBtnCommitCode.setEnabled(false);
                    mBtnCommitCode.setBackgroundResource(R.drawable.register_default);
                    mBtnAgainSendCode.setBackgroundResource(R.drawable.register_default);
                }
                break;
            case R.id.btn_commitCode:
                String verification = mEtCode.getText().toString();
                showProgressDialog();
                submitVerification(verification);
                Log.d("comm", "提交验证码");
                break;
            case R.id.btn_register:
                if (!TextUtils.isEmpty(mEtPassword.getText().toString())) {
                    String mPassword = mEtPassword.getText().toString();
                    Log.d("pass", "注册");
                    if (mPassword.length() >= 8) {
                        //  ^[a-zA-Z]\w{5,17}$
                        if (checkPassword(mPassword)) {
                            mBtnRegister.setClickable(false);
                            showProgressDialog();
                            if (isClickRegisterBtn)
                                getUDefAvatarUrl();
                            else {
                                updateUserPwdToDb();
                            }
                        } else
                            Toast.makeText(RegisterActivity.this, "密码强度过低,请输入8~16位数字加字母组合", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "密码不能低于8位", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back:
                checkWhichStep();
                break;
        }
    }

    /**
     * 第三方相关逻辑
     */

    //注册短信回调（从远程（本地）获取到短信发送状况，发送给主线程）
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

    //检测验证码提交状态（异步）
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码发送成功", Toast.LENGTH_SHORT).show();
                    new TimeCount(60000, 1000).start();
                    closeProgressDialog();
                    showWhichStep(View.GONE, View.VISIBLE, View.GONE);
                    changeStepTextColor(R.color.ordinary, R.color.colorPrimary, R.color.ordinary);
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                    showWhichStep(View.GONE, View.GONE, View.VISIBLE);
                    changeStepTextColor(R.color.ordinary, R.color.ordinary, R.color.colorPrimary);
                }
            } else {
                closeProgressDialog();
                //  ((Throwable) data).printStackTrace();
                mBtnSendCode.setClickable(true);
                mEtCode.setText("");
                mBtnCommitCode.setEnabled(false);
                mBtnCommitCode.setBackgroundResource(R.drawable.register_default);
                Log.d("ccc", ((Throwable) data).getMessage());
                String errorInfo = null;
                try {
                    //获取错误信息
                    errorInfo = new JSONObject(((Throwable) data).getMessage()).getString("detail");
                    Toast.makeText(getApplicationContext(), errorInfo, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "未知的错误", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };


    /**
     * 改变控件属性
     *
     * @param first  第一步 控件的显隐性枚举值
     * @param second
     * @param third
     */
    private void showWhichStep(int first, int second, int third) {
        mEtPhone.setVisibility(first);
        mBtnSendCode.setVisibility(first);

        mRlCode.setVisibility(second);
        mBtnCommitCode.setVisibility(second);

        mEtPassword.setVisibility(third);
        mBtnRegister.setVisibility(third);

        Log.d("sh", "0000");
    }

    /**
     * 改变步骤标题的文本颜色
     *
     * @param color1 标题一颜色
     * @param color2
     * @param color3
     */
    private void changeStepTextColor(int color1, int color2, int color3) {
        mTvPhone.setTextColor(getResources().getColor(color1));
        mTvCode.setTextColor(getResources().getColor(color2));
        mTvPassword.setTextColor(getResources().getColor(color3));
    }

    //检测哪一步（如果进行完第一步，则会给用户弹出提示框）
    private void checkWhichStep() {
        Log.d("111", "弹出提示");
        if (mTvCode.getTextColors() == getResources().getColorStateList(R.color.colorPrimary) ||
                mTvPassword.getTextColors() == getResources().getColorStateList(R.color.colorPrimary)) {
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


    //关闭对话框

    private void closeProgressDialog() {
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
            mPd = null;
        }
    }

    private void showProgressDialog() {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setMessage("加载中");
            mPd.setCancelable(false);
            mPd.show();
        }
    }

    /**
     * 数据库相关逻辑
     *
     * @param hdUrl  高清头像url
     * @param maxUrl
     * @param minUrl
     * @param aut    用户默认签名
     */


    //注册完成后添加用户信息到数据库
    private void addUserInfoToDB(String hdUrl, String maxUrl, String minUrl, String aut) {
        User u = new User();
        u.setUser_Phone(mPhone);
        u.setUser_Password("");
        u.setUser_image_hd(hdUrl);
        u.setUser_image_max(maxUrl);
        u.setUser_image_min(minUrl);
        u.setUser_Autograph(aut);
        u.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
                returnUInfoToMyFra();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                closeProgressDialog();
                mBtnRegister.setClickable(true);
            }
        });
    }

    //更新用户密码
    private void updateUserPwdToDb() {
        User u = new User();
        u.setUser_Password(mEtPassword.getText().toString());
        closeProgressDialog();
        u.update(RegisterActivity.this, mObjectId, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                returnUInfoToMyFra();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(RegisterActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //判断此用户是否已经注册
    private void isRegister() {
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.addWhereEqualTo("User_Phone", mPhone);
        Log.d("queryPhone", mPhone);
        bmobQuery.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                closeProgressDialog();
                if (list.isEmpty()) {
                    //判断点击的是否是注册按钮
                    if (isClickRegisterBtn) {
                        mBtnSendCode.setClickable(false);
                        sendVerification();
                    } else {
                        Toast.makeText(RegisterActivity.this, "您好像并未注册哟", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (isClickRegisterBtn) {
                        Log.d("query", "查询成功");
                        Log.d("info", list + "");
                        Toast.makeText(RegisterActivity.this, "此用户已经注册", Toast.LENGTH_SHORT).show();
                    } else {
                        String id = list.get(0).getObjectId();
                        Log.i("id", "onSuccess: " + list.get(0).getObjectId());
                        mObjectId = id;
                        mBtnSendCode.setClickable(false);
                        sendVerification();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                closeProgressDialog();
                Log.d("query", "查询失败");
                Toast.makeText(RegisterActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // List<String> dataList = new ArrayList<String>();


    private void getUDefAvatarUrl() {
        int rndNum = (int) (Math.random() * 9);
        BmobQuery bmobQuery = new BmobQuery("User_Default");
        bmobQuery.addWhereEqualTo("User_Default_Res_ID", rndNum);
        bmobQuery.addQueryKeys("User_Def_Av_Hd_Url,User_Def_Av_Max_Url,User_Def_Av_Min_Url,User_Def_Aut");
        bmobQuery.findObjects(this, new FindCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                String data = jsonArray.toString();
                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String UDefAvHdUrl = jsonObject.getString("User_Def_Av_Hd_Url");
                    String UDefAvMaxUrl = jsonObject.getString("User_Def_Av_Max_Url");
                    String UDefAvMinUrl = jsonObject.getString("User_Def_Av_Min_Url");
                    String UDefAut = jsonObject.getString("User_Def_Aut");

                    Log.d("url", UDefAvHdUrl);
                    Log.d("aut", UDefAut);
                    addUserInfoToDB(UDefAvHdUrl, UDefAvMaxUrl, UDefAvMinUrl, UDefAut);
                } catch (JSONException e) {
                    Log.d("jsexc", e + "，出现异常");
                }
                Log.d("data", data);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d("data1", i + "");
                Log.d("data2", s);
            }
        });

    }


    /**
     * 返回用户信息给myFragment--由loginFragment间接传到
     * 判断用户是否点击的为忘记密码，是则将密码返回为空。
     */
    private void returnUInfoToMyFra() {
        Intent intent = new Intent();
        intent.putExtra("regi_phone", mPhone);
        String pwd = "";
        if (isClickRegisterBtn)
            pwd = mEtPassword.getText().toString();
        intent.putExtra("regi_password", pwd);
        setResult(RESULT_OK, intent);
        Log.i("return", "已返回数据");
        finish();
    }

    /**
     * 检测密码强度（必须为数字与字母组合）
     *
     * @param pw 要检测的密码
     * @return 匹配结果
     */
    private boolean checkPassword(String pw) {
        String regPw = "^[\\da-zA-Z]*((\\d+[\\da-zA-Z]*[a-zA-Z]+)|" +
                "([a-zA-Z]+[\\da-zA-Z]*\\d+))[\\da-zA-Z]*$";
        Pattern p = Pattern.compile(regPw);
        Matcher m = p.matcher(pw);
        return m.matches();
    }

    @Override
    public void onBackPressed() {
        checkWhichStep();
    }


}