package com.goldenratio.commonweal.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.util.ImmersiveUtil;

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
 * Created by Kiuber on 2016/8/23.
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener, IMySqlManager {
    private View mTvPayRecord;
    private LinearLayout TopUP;
    private ProgressDialog mPd;
    private TextView mTvCoin;

    private String mUserId;
    private String mUserCoin = "0";
    private LinearLayout mLlCoin;
    private String mDonateCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
        initListtener();

        mUserId = ((MyApplication) getApplication()).getObjectID();
        queryUserCoinByObjectId();
        new ImmersiveUtil(this, R.color.white, false);
    }

    private void initView() {
        mTvPayRecord = findViewById(R.id.ll_pay_record);
        TopUP = (LinearLayout) findViewById(R.id.top_up);
        mTvCoin = (TextView) findViewById(R.id.ntv_coin);
        mLlCoin = (LinearLayout) findViewById(R.id.ll_coin);
    }

    private void initListtener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mTvPayRecord.setOnClickListener(this);
        TopUP.setOnClickListener(this);
        mLlCoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_pay_record:
                startActivity(new Intent(this, PayRecordActivity.class));
                break;
            case R.id.top_up:
                showInputDialog();
                break;
            case R.id.ll_coin:
                if (mUserCoin != null && mDonateCoin != null) {
                    int mIntUserCoin = Integer.parseInt(mUserCoin);
                    int mIntDonateCoin = Integer.parseInt(mDonateCoin);
                    View view = View.inflate(this, R.layout.dialog_wallet_detail, null);
                    TextView mTvUserCoinAll = (TextView) view.findViewById(R.id.tv_user_coin_all);
                    TextView mTvUserCoin = (TextView) view.findViewById(R.id.tv_user_coin);
                    TextView mTvDonateCoin = (TextView) view.findViewById(R.id.tv_donate_coin);
                    mTvUserCoinAll.setText("用户总公益币：" + (mIntUserCoin + mIntDonateCoin));
                    mTvUserCoin.setText("账号公益币：" + mIntUserCoin);
                    mTvDonateCoin.setText("爱心公益币：" + mIntDonateCoin);
                    view.findViewById(R.id.detail_problem).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.this);
                            builder.setTitle("联系我们");
                            builder.setMessage("本产品由0P618团队开发\n" + "联系电话：15589618535\n" + "qq邮箱：18454837733@163.com");
                            builder.setPositiveButton("确定", null);
                            builder.setCancelable(false);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.this);
                    builder.setTitle("公益币详情");
                    builder.setView(view);
                    final Dialog dialog = builder.create();
                    dialog.show();
                    view.findViewById(R.id.detail_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }

                break;
        }
    }

    private void showInputDialog() {
        // 设置内容区域为自定义View
        LinearLayout SetDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_register, null);
        final EditText ETUSER = (EditText) SetDialog.findViewById(R.id.et_userName);
        TextView tvTitle = (TextView) SetDialog.findViewById(R.id.tv_title_nickname);
        tvTitle.setText("充值公益币");
        ETUSER.setHint("请输入充值公益币数量");
        ETUSER.setInputType(InputType.TYPE_CLASS_NUMBER);
        tvTitle.setText("充值公益币");
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        // builder.setTitle(dialogTitle);
        builder.setView(SetDialog);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(ETUSER.getText().toString())) {
                            final int inputCoin = Integer.valueOf(ETUSER.getText().toString());
                            final double sumCoin = Double.valueOf(mUserCoin) + inputCoin;
                            final double money = (inputCoin / 10) * (1.05);
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(WalletActivity.this);
                            builder.setMessage("您将花费" + money + "元（由于个人开发团队限制，" +
                                    "平台收取5%的手续费），充值" + inputCoin + "公益币");
                            builder.setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //充值金额包括公益币、充值Bmob收取的0.05的费率。
                                    new MySqlManagerImpl(WalletActivity.this, WalletActivity.this)
                                            .pay(false, money, sumCoin, ETUSER.getText() + "");
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.show();

                        }
                    }
                }

        );
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void queryUserCoinByObjectId() {
        showProgressDialog();
        String webServiceIp = ((MyApplication) (getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String method = "QueryUserCoinAndSixPwdByObjectId";
            String url = webServiceIp + method;
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WalletActivity.this, e1, Toast.LENGTH_SHORT).show();
                                closeProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                JSONArray jsonArray;
                                try {
                                    jsonArray = new JSONArray(result);
                                    Log.i("返回json的长度", "run: " + jsonArray.length());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        mUserCoin = jsonObject.getString("User_Coin");
                                        mDonateCoin = jsonObject.getString("User_DonateCoin");
                                        mTvCoin.setText(Integer.parseInt(mUserCoin) + Integer.parseInt(mDonateCoin) + "");
                                    }
                                } catch (JSONException e) {
                                    Log.d("Kiuber_LOG", e.getMessage() + request);
                                }
                                closeProgressDialog();
                            }
                        });
                    }
                });
            }
        } else {
            closeProgressDialog();
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.isLogin();
            Toast.makeText(this, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
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
            mPd.setCancelable(false);
            mPd.show();
        }
    }

    @Override
    public void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin) {

    }

    @Override
    public void showSixPwdOnFinishInput(String sixPwd, int event) {

    }

    @Override
    public void updateUserCoinByObjectId(String sumCoin, String changeCoin, int PRName) {
        mTvCoin.setText(sumCoin);
    }

    @Override
    public void queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd, String donateCoin) {
    }

    @Override
    public void updateUserSixPwdByObjectId(String sixPwd) {

    }
}
