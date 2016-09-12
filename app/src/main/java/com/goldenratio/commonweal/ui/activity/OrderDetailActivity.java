package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlOrder;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.goldenratio.commonweal.widget.PopEnterPassword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.bmob.v3.BmobRealTimeData.TAG;

/**
 * Created by Kiuber on 2016/8/17.
 */
public class OrderDetailActivity extends Activity implements View.OnClickListener, IMySqlManager {

    private Button mBtnPay;
    private TextView mTvName;
    private Button mBtnExpress;
    private Good mGood;
    private TextView mTvCoin;
    private String user_coin;
    private MySqlManagerImpl mySqlManager;
    private String mUserId;
    private String mUserCoin;
    private String mSixPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initData();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initData() {
        mGood = (Good) getIntent().getSerializableExtra("orderList");
        mTvName.setText(mGood.getGood_Name());
        mTvCoin.setText(mGood.getGood_NowCoin());
        mySqlManager = new MySqlManagerImpl(this, this);
        mUserId = ((MyApplication) getApplication()).getObjectID();
    }

    private void initView() {
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvCoin = (TextView) findViewById(R.id.tv_coin);
        mBtnPay = (Button) findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(this);
        mBtnExpress = (Button) findViewById(R.id.btn_express);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay:
//                queryUserCoinByObjectId();
                mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null);
                break;
            case R.id.btn_express:
                break;
        }
    }

    public void showPayKeyBoard(String order_coin, ProgressDialog progressDialog, String userCoin) {
        String mUserId = ((MyApplication) getApplication()).getObjectID();

        progressDialog.dismiss();

//        PopEnterPassword popEnterPassword = new PopEnterPassword(this, "物品支付", order_coin, "支付订单", mUserId, mySqlOrder.getObject_Id(), userCoin);

        // 显示窗口
//        popEnterPassword.showAtLocation(this.findViewById(R.id.layoutContent),
//                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    @Override
    public void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin) {

    }

    @Override
    public void showSixPwdOnFinishInput(String sixPwd, int event) {
        if (event == 1) {
            payOrder(mGood.getObjectId(), mUserId, mGood.getGood_NowCoin());
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean updateUserCoinByObjectId(String sumCoin, String changeCoin, int PRName) {
        return false;
    }

    @Override
    public boolean queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd) {
        mUserCoin = mStrUserCoin;
        mSixPwd = sixPwd;
        double payPoorCoin = Double.valueOf(mStrUserCoin) - Double.valueOf(mGood.getGood_NowCoin());
        double payPoorMoney = (-payPoorCoin) / 10;
        if (payPoorCoin < 0) {
            mySqlManager.pay(false, payPoorMoney + payPoorCoin * 0.05, payPoorMoney * 10 + Double.valueOf(mStrUserCoin), payPoorMoney + "");
        } else {
            mySqlManager = new MySqlManagerImpl(this, this, "支付物品订单", mGood.getGood_NowCoin() + "", "订单支付");
            mySqlManager.showSixPwdOnFinishInput(sixPwd, 1);
        }
        return false;
    }

    @Override
    public boolean updateUserSixPwdByObjectId(String sixPwd) {
        return false;
    }

    private void payOrder(String good, String user, String userCoin) {
        String webServiceIp = ((MyApplication) (getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String URL = webServiceIp + "PayOrder";
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("GoodId", good)
                    .add("UseId", user)
                    .add("UserCoin", userCoin)
                    .build();

            final Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    final String e1 = e.getMessage();
                    OrderDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrderDetailActivity.this, e1, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    OrderDetailActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (result.equals("success")) {
                                Toast.makeText(OrderDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                pushMessage();
                            } else {
                                Toast.makeText(OrderDetailActivity.this, "支付失败" + result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
        }
    }

    private void pushMessage() {
    }
}
