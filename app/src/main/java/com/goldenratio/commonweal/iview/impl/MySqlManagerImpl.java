package com.goldenratio.commonweal.iview.impl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
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
import com.goldenratio.commonweal.bean.PayRecord;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.ui.activity.LoginActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.MD5Util;
import com.goldenratio.commonweal.widget.OnPasswordInputFinish;
import com.goldenratio.commonweal.widget.PasswordView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
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

    private Activity mContext;
    private TextView mTvType;
    private TextView mTvRemark;
    private TextView mTvCoin;
    private IMySqlManager mSqlManager;
    private String mUserId;
    private ProgressDialog mPd;

    private Double money;

    public MySqlManagerImpl(Activity context, IMySqlManager mySqlManager) {
        super(context);
        mUserId = ((MyApplication) context.getApplication()).getObjectID();
        this.mContext = context;
        this.mSqlManager = mySqlManager;
    }


    public MySqlManagerImpl(Activity context, IMySqlManager mySqlManager, String type, final String coin, String remark) {
        super(context);
        mUserId = ((MyApplication) context.getApplication()).getObjectID();
        this.mContext = context;
        this.mSqlManager = mySqlManager;
        initView();
        mTvType.setText(type);
        mTvCoin.setText(coin);
        mTvRemark.setText(remark);
        showAtLocation(context.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

/*    private void isObjectID() {
        if (mUserId.equals("")) {
            Toast.makeText(mContext, "您尚未登陆，请登陆后再试", Toast.LENGTH_SHORT).show();
            mContext.finish();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        }
    }*/

    public void pay(final boolean alipayOrWechatPay, final double price, final double allCoin, final String changeCoin) {

        final String[] orderID = new String[1];

        BP.pay("公益币充值", "描述", price, alipayOrWechatPay, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(mContext, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                isPay(orderID[0], allCoin, changeCoin);
                dismiss();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                money = price;
                isPay(orderID[0], allCoin, changeCoin);
//                updateUserCoinByObjectId("+" + allCoin, changeCoin, -1);
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderI) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                orderID[0] = orderI;
                closeProgressDialog();
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(mContext, "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
                    installBmobPayPlugin("bp.db");
                } else {
                    Toast.makeText(mContext, "支付中断!" + reason, Toast.LENGTH_SHORT)
                            .show();
                }
                Log.i("Fail", reason + code + "支付失败");
                closeProgressDialog();
            }
        });
    }

    /**
     * 确认支付是否成功
     */
    public void isPay(String orderID, final double allCoin, final String changeCoin) {

        if (TextUtils.isEmpty(orderID)) {
            closeProgressDialog();
            return;
        }
        BP.query(orderID, new QListener() {

            @Override
            public void succeed(String status) {
                Log.d("lxc", "succeed: " + status);
                if (status.equals("SUCCESS")) {
                    updateUserCoinByObjectId("+" + allCoin, changeCoin, -1);
                }
            }

            @Override
            public void fail(int code, String reason) {
                Toast.makeText(mContext, "确认支付失败！", Toast.LENGTH_SHORT).show();
                Log.d("lxc", "fail: " + code + ":" + reason);
                closeProgressDialog();

            }
        });
    }

    /**
     * 保存支付记录
     *
     * @param PR_Type
     * @param PR_Money
     * @param PR_Coin
     * @param PR_Number
     * @param PR_Status
     * @param PayType
     * @param PR_Name
     */
    public void savePayHistoryToBmob(boolean PR_Type,
                                     String PR_Money,
                                     String PR_Coin,
                                     String PR_Number,
                                     Boolean PR_Status,
                                     boolean PayType,
                                     int PR_Name) {
        PayRecord payRecord = new PayRecord();
        payRecord.setPR_Type((PR_Type ? "充值公益币" : "公益币支出"));
        payRecord.setPR_Name(PR_Name == 0 ? " 捐赠项目" : (PR_Name == 1 ? "交保证金" : (PR_Name == 2 ? "购买物品" : (PR_Name == -1 ? "" : ""))));
        payRecord.setPR_Money(PR_Type ? "花费" + PR_Money + "元" : "0");
        payRecord.setPR_PayWay(PR_Type ? (PayType ? 1 : 2) : 3);
        payRecord.setPR_Coin(PR_Coin);
        payRecord.setPR_Number(PR_Type ? PR_Number : createRndNumber());
        payRecord.setPR_Status(PR_Status);
        payRecord.setUser_ID(mUserId);
        payRecord.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(mContext, "本次交易记录已保存", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(mContext, "交易记录保存失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                }
            }
        });
    }

    /**
     * 创建唯一的随机订单号
     *
     * @return
     */
    private String createRndNumber() {
        Random rad = new Random();
        String result = rad.nextInt(9) + "";
        if (result.length() == 1) {
            result = "0" + result;
        }
        String orderId = result +
                (System.currentTimeMillis() + "").substring(1) +
                (System.nanoTime() + "").substring(7, 10);
        return orderId;
    }

    /**
     * 安装bmob安全支付控件
     *
     * @param fileName
     */
    private void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = mContext.getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化视图，设置支付验证框相关属性
     */
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


    /**
     * 更新用户公益币
     *
     * @param sumCoin    要更新的公益币--即用户现有公益币数量
     * @param changeCoin 变化的公益币--用户充值或花费的公益币
     * @param PRName     订单名称
     * @return
     */
    @Override
    public void updateUserCoinByObjectId(final String sumCoin, final String changeCoin, final int PRName) {
        showProgressDialog();
        String webServiceIp = ((MyApplication) (mContext.getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String url = webServiceIp + "UpdateUserCoinByObjectId";
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
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    final String e1 = e.getMessage();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, e1, Toast.LENGTH_SHORT).show();
                            savePayHistoryToBmob(PRName == -1, money + "", changeCoin, createRndNumber(), false, PRName == -1, PRName);
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
                            savePayHistoryToBmob(PRName == -1, money + "", changeCoin, createRndNumber(), true, PRName == -1, PRName);
                            mSqlManager.updateUserCoinByObjectId(sumCoin, changeCoin, PRName);
                            // TODO: 2016/8/21 继续
                        }
                    });
                }
            });
        } else {
            closeProgressDialog();
            MyApplication myApplication = (MyApplication) mContext.getApplication();
            myApplication.isLogin();
            Toast.makeText(mContext, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
        }
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
                        } else {
                            dismiss();
                            Toast.makeText(mContext, "支付密码错误", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        break;
                }
            }
        });
    }


    @Override
    public void updateUserSixPwdByObjectId(final String sixPwd) {
        showProgressDialog();
        String webServiceIp = ((MyApplication) (mContext.getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String url = webServiceIp + "UpdateUserSixPwdByObjectId";
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
            call.enqueue(new Callback() {
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
        } else {
            closeProgressDialog();
            MyApplication myApplication = (MyApplication) mContext.getApplication();
            myApplication.isLogin();
            Toast.makeText(mContext, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查询用户公益币
     *
     * @param mStrUserCoin
     * @param sixPwd
     * @return
     */
    @Override
    public void queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd, final String donateCoin) {
        showProgressDialog();
        String webServiceIp = ((MyApplication) (mContext.getApplication())).getWebServiceIp();

        String URL = webServiceIp + "QueryUserCoinAndSixPwdByObjectId";
        if (!(webServiceIp == null)) {
            OkHttpClient okHttpClient = new OkHttpClient();
            if (mUserId != null) {
                RequestBody body = new FormBody.Builder()
                        .add("ObjectId", mUserId)
                        .build();

                final Request request = new Request.Builder()
                        .url(URL)
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
                                String userDonateCoin = null;
                                JSONArray jsonArray;
                                try {
                                    jsonArray = new JSONArray(result);
                                    Log.i("返回json的长度", "run: " + jsonArray.length());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        userCoin = jsonObject.getString("User_Coin");
                                        payPwd = jsonObject.getString("User_SixPwd");
                                        userDonateCoin = jsonObject.getString("User_DonateCoin");
                                    }
                                } catch (JSONException e) {
                                    Log.d("Kiuber_LOG", e.getMessage() + request);
                                }
                                closeProgressDialog();
                                mSqlManager.queryUserCoinAndSixPwdByObjectId(userCoin, payPwd, userDonateCoin);
                            }
                        });
                    }
                });
            }
        } else {
            closeProgressDialog();
            MyApplication myApplication = (MyApplication) mContext.getApplication();
            myApplication.isLogin();
            Toast.makeText(mContext, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
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
            mPd = new ProgressDialog(mContext);
            mPd.setMessage("加载中");
            mPd.setCancelable(false);
            mPd.show();
        }
    }
}
