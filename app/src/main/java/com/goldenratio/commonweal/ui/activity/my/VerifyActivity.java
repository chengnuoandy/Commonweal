package com.goldenratio.commonweal.ui.activity.my;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.api.Constants;
import com.goldenratio.commonweal.api.ErrorInfo;
import com.goldenratio.commonweal.api.User;
import com.goldenratio.commonweal.api.UsersAPI;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.bean.VerifyRecord;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.ui.fragment.MyFragment;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.goldenratio.commonweal.ui.fragment.MyFragment.mUserID;

/**
 * Created by Kiuber on 2016/8/31.
 */
public class VerifyActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvVer;
    private SsoHandler mSsoHandler;
    private UsersAPI mUsersAPI;
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken = null;
    private User upUser = null;
    private ProgressDialog mPd;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initView();
        initData();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initData() {
        // 创建微博实例
        // 快速授权时，不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, null);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
    }

    private void initView() {
        mTvVer = (TextView) findViewById(R.id.goto_verify_button);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvVer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_verify_button:
                progressDialog = ProgressDialog.show(VerifyActivity.this, null, "正在进行微博认证", false);
                wbAuthorize();
                break;
        }
    }


    /**
     * 微博绑定
     */
    private void wbAuthorize() {
        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("微博只能绑定一次，由于特殊原因，解绑需要联系官方，绑定之前请慎重考虑，你确定要绑定吗？")
                .setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSsoHandler.authorize(new AuthListener());
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User_Profile#parse 将JSON串解析成User对象
                upUser = User.parse(response);
                if (upUser != null) {
                    if (upUser.verified_reason.isEmpty()) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(VerifyActivity.this)
                                .setMessage("您的微博账号没有通过V认证,不能进行微博用户认证")
                                .setPositiveButton("我知道了", null)
                                .show();
                    } else {
                        isBind(upUser);
                    }
                } else {
                    closeProgressDialog();
                    Toast.makeText(VerifyActivity.this, response,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Log.d("lxc", "onWeiboException: openAPI访问错误 " + info);
            closeProgressDialog();
            Toast.makeText(VerifyActivity.this, "网络访问异常！", Toast.LENGTH_SHORT).show();
        }
    };

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            showProgressDialog();
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {

                mUsersAPI = new UsersAPI(VerifyActivity.this, Constants.APP_KEY, mAccessToken);
                //openAPI相关
                long uid = Long.parseLong(mAccessToken.getUid());

                mUsersAPI.show(uid, mListener);
                Toast.makeText(VerifyActivity.this,
                        "授权成功", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
                closeProgressDialog();
                Toast.makeText(VerifyActivity.this, message, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(VerifyActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(VerifyActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

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
            mPd.setCancelable(true);
            mPd.show();
        }
    }


    /**
     * 判断是否已经绑定
     *
     * @param upUser 用户实体
     */
    public void isBind(final User upUser) {
        BmobQuery<User_Profile> query = new BmobQuery<>();
        query.addWhereEqualTo("User_weiboID", upUser.id);
        query.findObjects(new FindListener<User_Profile>() {
            @Override
            public void done(List<User_Profile> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        addVerRecord(upUser);
                    } else {
                        closeProgressDialog();
                        Toast.makeText(VerifyActivity.this, "该用户已绑定其他账号！", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(VerifyActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void addVerRecord(final User upUser) {
        User_Profile user_profile = new User_Profile();
        user_profile.setObjectId(mUserID);

        VerifyRecord verifyRecord = new VerifyRecord();
        verifyRecord.setVer_User(user_profile);
        verifyRecord.setWbID(upUser.id);
        verifyRecord.setWBReson(upUser.verified_reason);
        verifyRecord.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    User_Profile user_profile1 = new User_Profile();
                    user_profile1.update(mUserID, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(VerifyActivity.this, "名人认证上传成功！很快就会出审核结果哦~", Toast.LENGTH_SHORT).show();
                            } else {
                                ErrorCodeUtil.switchErrorCode(VerifyActivity.this, e.getErrorCode() + "");
                            }
                        }
                    });
                } else {
                    ErrorCodeUtil.switchErrorCode(VerifyActivity.this, e.getErrorCode() + "");
                }
            }
        });
    }

    private void updateDB() {
        User_Profile userProfile = new User_Profile();
        userProfile.setUser_WbID(upUser.id);
        userProfile.setUser_IsV(upUser.verified);
        userProfile.setUser_VerifiedReason(upUser.verified_reason); //认证原因
        final String id = mUserID;
        userProfile.update(mUserID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(VerifyActivity.this, "已绑定", Toast.LENGTH_SHORT).show();
                    MyFragment.userWBid = upUser.id;
                    UserDao mUserDao = new UserDao(VerifyActivity.this);
                    mUserDao.execSQL("update User_Profile set User_weiboID = ? where objectId = ?", new String[]{upUser.id, id});
                    closeProgressDialog();
                    Toast.makeText(VerifyActivity.this, "绑定成功！", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                } else {
//                    Toast.makeText(VerifyActivity.this, "绑定失败！" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

}
