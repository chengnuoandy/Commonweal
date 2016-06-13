package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.api.AccessTokenKeeper;
import com.goldenratio.commonweal.api.Constants;
import com.goldenratio.commonweal.api.ErrorInfo;
import com.goldenratio.commonweal.api.User;
import com.goldenratio.commonweal.api.UsersAPI;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.SharedUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lvxue on 2016/6/7 0007.
 * 登陆的相关功能
 */
public class LoginActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "lxc";
    @BindView(R.id.login_phone)
    EditText mLoginPhone;
    @BindView(R.id.login_password)
    EditText mLoginPassword;
    @BindView(R.id.tv_register)
    TextView mTvRegister;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.ib_sina)
    ImageButton mIbSina;
    @BindView(R.id.tv_sina)
    TextView mTvSina;
    @BindView(R.id.iv_return)
    ImageView mReturn;


    /**
     * 显示认证后的信息，如 AccessToken
     */
    private AuthInfo mAuthInfo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    /**
     * 用户信息接口
     */
    private UsersAPI mUsersAPI;

    //用户登陆ID
    private String userID;
    private Drawable draw1;
    private Drawable draw2;
    private User upUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, null);
        mSsoHandler = new SsoHandler(this, mAuthInfo);

        mLoginBtn.setOnClickListener(this);
        mIbSina.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mReturn.setOnClickListener(this);
        mLoginPassword.setOnFocusChangeListener(this);
        mLoginPhone.setOnFocusChangeListener(this);

        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        /*if (mAccessToken.isSessionValid()) {
            Log.d(TAG, "缓存有效");
            long uid = Long.parseLong(mAccessToken.getUid());
            isLogin(uid + "");
        }*/
        mUsersAPI = new UsersAPI(LoginActivity.this, Constants.APP_KEY, mAccessToken);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                //判断密码是否正确
                isLogin(mLoginPhone.getText().toString(), mLoginPassword.getText().toString());
                break;
            case R.id.ib_sina:
                mSsoHandler.authorize(new AuthListener());
                break;
            case R.id.tv_register:
                Intent mIntent = new Intent(this, RegisterActivity.class);
                startActivityForResult(mIntent, 1);
                break;
            case R.id.iv_return:
                finish();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.login_password:
                draw1 = ContextCompat.getDrawable(this, R.mipmap.login_user_icon);
                draw2 = ContextCompat.getDrawable(this, R.mipmap.password_fill);
                // 这一步必须要做,否则不会显示.
                draw1.setBounds(0, 0, draw1.getMinimumWidth(), draw1.getMinimumHeight());
                draw2.setBounds(0, 0, draw2.getMinimumWidth(), draw2.getMinimumHeight());
                mLoginPhone.setCompoundDrawables(draw1, null, null, null);
                mLoginPassword.setCompoundDrawables(draw2, null, null, null);
                break;
            case R.id.login_phone:
                draw1 = ContextCompat.getDrawable(this, R.mipmap.user_fill);
                draw2 = ContextCompat.getDrawable(this, R.mipmap.login_ic_password);
                // 这一步必须要做,否则不会显示.
                draw1.setBounds(0, 0, draw1.getMinimumWidth(), draw1.getMinimumHeight());
                draw2.setBounds(0, 0, draw2.getMinimumWidth(), draw2.getMinimumHeight());
                mLoginPhone.setCompoundDrawables(draw1, null, null, null);
                mLoginPassword.setCompoundDrawables(draw2, null, null, null);
                break;
        }
    }

    /**
     * 登陆相关逻辑(正常登陆)
     */
    private void isLogin(String Phone, final String Password) {
        BmobQuery<com.goldenratio.commonweal.bean.User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("User_Phone", Phone);
        //执行查询方法
        bmobQuery.findObjects(this, new FindListener<com.goldenratio.commonweal.bean.User>() {
            @Override
            public void onSuccess(List<com.goldenratio.commonweal.bean.User> object) {
                //判断查询到的行数
                if (object.size() == 1) {
                    com.goldenratio.commonweal.bean.User mUser = object.get(0);
                    if (Password.equals(mUser.getUser_Password())) {
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        //获得数据的objectId信息
                        userID = mUser.getObjectId();
                        returnData();
                        saveDB(mUser);
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "账户未注册", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(LoginActivity.this, "数据不存在", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 登陆相关逻辑(第三方授权)
     */
    private void isLogin(String id) {
        BmobQuery<com.goldenratio.commonweal.bean.User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("User_WbID", id);
        //执行查询方法
        bmobQuery.findObjects(this, new FindListener<com.goldenratio.commonweal.bean.User>() {
            @Override
            public void onSuccess(List<com.goldenratio.commonweal.bean.User> object) {
                //判断查询到的行数
                if (object.size() == 1) {
                    //如果此用户已存在，获得数据的objectId信息
                    com.goldenratio.commonweal.bean.User mUser = object.get(0);
                    userID = mUser.getObjectId();
                    returnData();
                    saveDB(mUser);
                } else if (object.size() == 0) {
                    if (upUser != null) {
                        //异步上传数据
                        new myAsyncTask(upUser).execute(upUser.profile_image_url);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(LoginActivity.this, "数据不存在", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                //openAPI相关
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);

                Toast.makeText(LoginActivity.this,
                        "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        //注册页面回调
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String registerReturnPhone = data.getStringExtra("regi_phone");
                    String registerReturnPassword = data.getStringExtra("regi_password");
                    mLoginPhone.setText(registerReturnPhone);
                    mLoginPassword.setText(registerReturnPassword);
                }
                break;
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                upUser = User.parse(response);
                if (upUser != null) {
                    //是否已经注册
                    isLogin(upUser.id);
                    Log.d(TAG, "onComplete: openAPI");
                } else {
                    Toast.makeText(LoginActivity.this, response,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Log.d("lxc", "onWeiboException: openAPI访问错误 " + info);
            Toast.makeText(LoginActivity.this, "网络访问异常！", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 回传数据
     */
    private void returnData() {
        //向上一个activity发送登陆用户的ID
        Intent intent = new Intent();
        intent.putExtra("objectId", userID);
        setResult(RESULT_OK, intent);
    }


    /**
     * 异步加载 内部类实现
     * 将获取到的第三方数据发送到数据库
     */
    class myAsyncTask extends AsyncTask<String, Void, Void> {

        private com.goldenratio.commonweal.bean.User user = new com.goldenratio.commonweal.bean.User();
        private User wbuser;

        public myAsyncTask(User wbuser) {
            this.wbuser = wbuser;
        }

        //耗时部分执行前运行，主线程中运行，可用来初始化需要的数据
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //耗时执行部分,只有此部分是运行在WorkerThread线程中
        //因为网络状况不确定
        @Override
        protected Void doInBackground(String... params) {
            //提交数据
            user.setUser_Name(wbuser.screen_name);
            user.setUser_Is_Real_Name(wbuser.verified);
            if ("m".equals(wbuser.gender)) {
                user.setUser_sex("男");
            } else if ("f".equals(wbuser.gender)) {
                user.setUser_sex("女");
            } else {
                user.setUser_sex("未知");
            }
            user.setUser_WbID(wbuser.id);
            user.setUser_image_max(wbuser.avatar_large);
            user.setUser_image_min(wbuser.profile_image_url);
            user.setUser_image_hd(wbuser.avatar_hd);
            user.setVerified_reason(wbuser.verified_reason); //认证原因
            user.setUser_Autograph(wbuser.description);
            user.save(LoginActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(LoginActivity.this, "成功提交数据", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            BmobQuery<com.goldenratio.commonweal.bean.User> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("User_WbID", wbuser.id);
            //执行查询方法
            bmobQuery.findObjects(LoginActivity.this, new FindListener<com.goldenratio.commonweal.bean.User>() {
                @Override
                public void onSuccess(List<com.goldenratio.commonweal.bean.User> object) {
                    //判断查询到的行数
                    if (object.size() == 1) {
                        //如果此用户已存在，获得数据的objectId信息
                        com.goldenratio.commonweal.bean.User mUser = object.get(0);
                        userID = mUser.getObjectId();
                        returnData();
                        saveDB(mUser);
                    }
                }
                @Override
                public void onError(int i, String s) {
                    Toast.makeText(LoginActivity.this, "查找数据失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    /**
     * 保存数据到本地
     */
    private void saveDB(com.goldenratio.commonweal.bean.User user) {
        UserDao mUserDao = new UserDao(LoginActivity.this);
        mUserDao.insert("insert into User (objectId,User_Name,User_Autograph,User_Avatar) values(?,?,?,?)",
                new String[]{userID,user.getUser_Name(),user.getUser_Autograph(),user.getUser_image_max()});
        finish();
    }

}
