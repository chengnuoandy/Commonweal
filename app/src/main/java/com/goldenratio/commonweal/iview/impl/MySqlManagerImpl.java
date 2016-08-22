package com.goldenratio.commonweal.iview.impl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.ui.activity.LoginActivity;
import com.goldenratio.commonweal.util.MD5Util;
import com.goldenratio.commonweal.widget.OnPasswordInputFinish;
import com.goldenratio.commonweal.widget.PasswordView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/20.
 * Email:jxfengmtx@163.com
 */
public class MySqlManagerImpl extends PopupWindow implements IMySqlManager {


    private PasswordView pwdView;
    private View mMenuView;

    private Activity mContext;
    private TextView mTvType;
    private TextView mTvRemark;
    private TextView mTvCoin;
    private IMySqlManager mSqlManager;
    private String mUserId;
    private ProgressDialog mPd;

    public MySqlManagerImpl(Activity context, IMySqlManager mySqlManager) {
        super(context);
        isObjectID();
        mUserId = ((MyApplication) context.getApplication()).getObjectID();
        this.mContext = context;
        this.mSqlManager = mySqlManager;
    }

    public MySqlManagerImpl(Activity context, IMySqlManager mySqlManager, String type, final String coin, String remark) {
        super(context);
        mUserId = ((MyApplication) context.getApplication()).getObjectID();
        isObjectID();
        this.mContext = context;
        this.mSqlManager = mySqlManager;
        initView();
        mTvType.setText(type);
        mTvCoin.setText(coin);
        mTvRemark.setText(remark);
        showAtLocation(context.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    private void isObjectID() {
        if (TextUtils.isEmpty(mUserId)) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            Toast.makeText(mContext, "您尚未登陆，请登陆后再试", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mMenuView = inflater.inflate(R.layout.pop_enter_password, null);
        pwdView = (PasswordView) mMenuView.findViewById(R.id.pwd_view);

        mTvType = (TextView) mMenuView.findViewById(R.id.tv_type);
        mTvCoin = (TextView) mMenuView.findViewById(R.id.tv_coin);
        mTvRemark = (TextView) mMenuView.findViewById(R.id.tv_remark);
        // 监听X关闭按钮
        pwdView.getImgCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 监听键盘上方的返回
        pwdView.getVirtualKeyboardView().getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_add_ainm);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x66000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }


    @Override
    public boolean updateUserCoinByObjectId(final String sumCoin) {
        showProgressDialog();
        String url = "http://123.206.89.67/WebService1.asmx/UpdateUserCoinByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("ObjectId", mUserId)
                .add("UserCoin", sumCoin + "")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String e1 = e.getMessage();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, e1, Toast.LENGTH_SHORT).show();
                        closeProgressDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                        closeProgressDialog();
                        dismiss();
                        mSqlManager.updateUserCoinByObjectId(sumCoin);
                        // TODO: 2016/8/21 继续
                    }
                });
            }
        });
        return false;
    }

    /**
     * @param pwd   判断传入的密码是否正确，回传输入的密码
     * @param event 传入的事件---0-重置密码。1-检测密码是否正确（已判断支付密码）。。。传入==回传
     */

    @Override
    public void showSixPwdOnFinishInput(final String pwd, final int event) {
        //添加密码输入完成的响应
        pwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String password) {
                password = MD5Util.createMD5(password);
                //重置密码
                switch (event) {
                    case 0:
                        mSqlManager.showSixPwdOnFinishInput(password, event);
                        break;
                    case 1:
                        if (pwd != null && pwd.equals(password)) {
                            dismiss();
                            mSqlManager.showSixPwdOnFinishInput(password, event);
                        } else Toast.makeText(mContext, "支付密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        break;
                }
            }
        });
    }


    @Override
    public boolean updateUserSixPwdByObjectId(final String sixPwd) {
        showProgressDialog();
        String url = "http://123.206.89.67/WebService1.asmx/UpdateUserSixPwdByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("ObjectId", mUserId)
                .add("SixPwd", sixPwd)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String e1 = e.getMessage();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "密码设置失败" + e1, Toast.LENGTH_SHORT).show();
                        closeProgressDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.contains("success")) {
                            dismiss();
                            mSqlManager.updateUserSixPwdByObjectId(sixPwd);
                            Toast.makeText(mContext, "密码设置成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Kiuber_LOG", "fail: " + result);
                        }
                        closeProgressDialog();
                    }
                });
            }
        });
        return false;
    }

    @Override
    public boolean queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd) {
        showProgressDialog();
        String rootCatalog = "http://123.206.89.67/WebService1.asmx/";
        String method = "QueryUserCoinAndSixPwdByObjectId";
        String url = rootCatalog + method;
        OkHttpClient okHttpClient = new OkHttpClient();
        if (mUserId != null) {
            RequestBody body = new FormBody.Builder()
                    .add("ObjectId", mUserId)
                    .build();

            final Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    final String e1 = e.getMessage();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, e1, Toast.LENGTH_SHORT).show();
                            closeProgressDialog();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String userCoin = null;
                            String payPwd = null;
                            JSONArray jsonArray;
                            try {
                                jsonArray = new JSONArray(result);
                                Log.i("返回json的长度", "run: " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    userCoin = jsonObject.getString("User_Coin");
                                    payPwd = jsonObject.getString("User_SixPwd");
                                }
                            } catch (JSONException e) {
                                Log.d("Kiuber_LOG", e.getMessage() + request);
                            }
                            closeProgressDialog();
                            mSqlManager.queryUserCoinAndSixPwdByObjectId(userCoin, payPwd);
                        }
                    });
                }
            });
        }
        return false;
    }


    private void closeProgressDialog() {
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
            mPd = null;
        }
    }

    private void showProgressDialog() {
        if (mPd == null) {
            mPd = new ProgressDialog(mContext);
            mPd.setMessage("加载中");
            mPd.setCancelable(false);
            mPd.show();
        }
    }
}
