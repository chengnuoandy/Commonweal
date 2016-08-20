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
import com.goldenratio.commonweal.bean.MySqlOrder;
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
public class OrderDetailActivity extends Activity implements View.OnClickListener {

    private Button mBtnPay;
    private TextView mTvName;
    private MySqlOrder mySqlOrder;
    private Button mBtnExpress;
    private TextView mTvCoin;
    private String user_coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initData();
    }

    private void initData() {
        mySqlOrder = (MySqlOrder) getIntent().getSerializableExtra("orderList");
        mTvName.setText(mySqlOrder.getOrder_Name());
        mTvCoin.setText(mySqlOrder.getOrder_Coin());
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
                queryUserCoinByObjectId();
                break;
            case R.id.btn_express:

                break;
        }
    }

    private void queryUserCoinByObjectId() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "正在加载", false);
        String url = "http://123.206.89.67/WebService1.asmx/QueryUserCoinByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        String mStrObjectId = ((MyApplication) getApplication()).getObjectID();
        if (mStrObjectId != null) {
            RequestBody body = new FormBody.Builder()
                    .add("ObjectId", mStrObjectId)
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrderDetailActivity.this, e1, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    user_coin = jsonObject.getString("User_Coin");
                                }
                                final double poor = Double.valueOf(mySqlOrder.getOrder_Coin()) - Double.valueOf(user_coin);
                                if (poor > 0) {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                                    builder.setMessage("您的账号不足" + mySqlOrder.getOrder_Coin() + "，是否立即充值？");
                                    builder.setPositiveButton("去充值", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            pay(false, poor, progressDialog);
                                        }
                                    });
                                    builder.setNegativeButton("取消", null);
                                    builder.show();
                                } else {
                                    Log.d(TAG, "run: " + result);
                                    //poor1 用户支付完成剩下的公益币
                                    double poor1 = Double.valueOf(user_coin) - Double.valueOf(mySqlOrder.getOrder_Coin());
                                    showPayKeyBoard(mySqlOrder.getOrder_Coin(), progressDialog, poor1 + "");
                                }
                            } catch (JSONException e) {
                                Log.d("Kiuber_LOG", e.getMessage() + request);
                            }
                        }
                    });
                }
            });
        }
    }

    public void showPayKeyBoard(String order_coin, ProgressDialog progressDialog, String userCoin) {
        String mUserId = ((MyApplication) getApplication()).getObjectID();

        progressDialog.dismiss();

        PopEnterPassword popEnterPassword = new PopEnterPassword(this, "物品支付", order_coin, "支付订单", mUserId, mySqlOrder.getObject_Id(), userCoin);

        // 显示窗口
        popEnterPassword.showAtLocation(this.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    private void pay(final boolean alipayOrWechatPay, double price, final ProgressDialog progressDialog) {

        BP.pay("名称", "描述", price, alipayOrWechatPay, new PListener() {


            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(OrderDetailActivity.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                progressDialog.dismiss();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(OrderDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                progressDialog.dismiss();
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            OrderDetailActivity.this,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
                    installBmobPayPlugin("bp.db");
                } else {
                    Toast.makeText(OrderDetailActivity.this, "支付中断!" + reason, Toast.LENGTH_SHORT)
                            .show();
                }
                progressDialog.dismiss();
            }
        });
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
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
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
