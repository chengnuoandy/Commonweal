package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import c.b.BP;
import c.b.PListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/7/3.
 * 捐赠时弹出的页面
 */

public class HelpDonateActivity extends Activity implements IMySqlManager {

    @BindView(R.id.checkBox6)
    RadioButton mCheckBox6;
    @BindView(R.id.rg_money)
    RadioGroup mRgMoney;
    @BindView(R.id.et_other)
    EditText mEtOther;
    @BindView(R.id.avail)
    TextView mAvail;

    private String mCoin;

    private String mStrUserCoin;
    private String mSixPwd;
    private String mUserId;

    ProgressDialog progressDialog;
    private MySqlManagerImpl mySqlManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_donate);
        ButterKnife.bind(this);

        mCoin = "10";

        mUserId = ((MyApplication) getApplication()).getObjectID();

        mySqlManager = new MySqlManagerImpl(this, this);
        mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null);

        mRgMoney.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == mCheckBox6.getId()) {
                    mEtOther.setVisibility(View.VISIBLE);
                } else {
                    mEtOther.setVisibility(View.GONE);
                    mCoin = ((RadioButton) findViewById(checkedId)).getText().toString();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.tv_donate_mo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_donate_mo:
                if (mEtOther.getVisibility() == View.VISIBLE) {
                    mCoin = mEtOther.getText().toString();
                }
                //是否已设置支付密码
                if (mSixPwd.equals("0") || TextUtils.isEmpty(mSixPwd)) {
                    Toast.makeText(this, "请先设置六位数密码", Toast.LENGTH_SHORT).show();
                    showPayKeyBoard2();
                } else {
                    double usercoin = Double.valueOf(mStrUserCoin);
                    final double choiceCoin = Double.valueOf(mCoin);

                    final double poor = choiceCoin - usercoin;  //所缺公益币,充值
                    final double sxf = (poor / 10) * 0.05;
                    //选择的币数大于用户现有的金币，公益币不足，提示充值
                    if (poor > 0 || poor == 0) {
                        final AlertDialog.Builder builder =
                                new AlertDialog.Builder(HelpDonateActivity.this);
                        builder.setMessage("账号公益币：" + usercoin + "，还缺" + poor + "公益币，您将充值"
                                + poor + "公益币（由于个人开发团队限制，" +
                                "平台收取5%的手续费）");
                        builder.setPositiveButton("充值", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //充值金额包括公益币、充值Bmob收取的0.05的费率。
                                pay(false, sxf + poor / 10, choiceCoin);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        showPayKeyBoard1(mSixPwd);
                    }
                }
                break;
        }
    }


    private void installBmobPayPlugin(String fileName) {
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

    private void pay(final boolean alipayOrWechatPay, double price, final double allCoin) {

        BP.pay("公益币充值", "描述", price, alipayOrWechatPay, new PListener() {


            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(getApplicationContext(), "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                progressDialog.dismiss();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                updateUserCoin(allCoin);
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderI) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                progressDialog.dismiss();
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(getApplication(), "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
                    installBmobPayPlugin("bp.db");
                } else {
                    Toast.makeText(getApplication(), "支付中断!" + reason, Toast.LENGTH_SHORT)
                            .show();
                }
                progressDialog.dismiss();
            }
        });
    }

    public void showPayKeyBoard1(String inputPwd) {
        mySqlManager = new MySqlManagerImpl(this, this, "请输入支付密码", "-" + mCoin, "您将支付" + mCoin + "公益币");
        mySqlManager.showSixPwdOnFinishInput(inputPwd, 1);
    }

    public void showPayKeyBoard2() {
        mySqlManager = new MySqlManagerImpl(this, this, "设置六位数密码", "", "新密码");
        mySqlManager.showSixPwdOnFinishInput("", 0);
    }


    private void updateUserCoin(double sumCoin) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "正在加载", false);
        String url = "http://123.206.89.67/WebService1.asmx/UpdateUserCoinByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        final String mStrObjectId = ((MyApplication) getApplication()).getObjectID();
        if (mStrObjectId != null) {
            RequestBody body = new FormBody.Builder()
                    .add("ObjectId", mStrObjectId)
                    .add("UserCoin", sumCoin + "")
                    .build();
            Log.d("LXT", "updateUserCoin: " + mStrObjectId + "-->" + sumCoin);

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
                            Toast.makeText(HelpDonateActivity.this, e1, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HelpDonateActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    //完成之后
    @Override
    public void showSixPwdOnFinishInput(String sixPwd, int event) {
        Log.i("六位数密码", "showSixPwdOnFinishInput: " + sixPwd);
        if (event == 0) {
            mySqlManager.updateUserSixPwdByObjectId(sixPwd);
        } else {
            double usercoin = Double.valueOf(mStrUserCoin);
            final double choiceCoin = Double.valueOf(mCoin);
            mySqlManager.updateUserCoinByObjectId(usercoin - choiceCoin + "");
        }
    }

    @Override
    public boolean updateUserCoinByObjectId(String sumCoin) {
        mStrUserCoin = sumCoin;
        mAvail.setText(mStrUserCoin);
        return false;
    }

    @Override
    public boolean queryUserCoinAndSixPwdByObjectId(String mUserCoin, String sixPwd) {
        mStrUserCoin = mUserCoin;
        mSixPwd = sixPwd;
        mAvail.setText(mStrUserCoin);
        return false;
    }

    @Override
    public boolean updateUserSixPwdByObjectId(String sixPwd) {
        mSixPwd = sixPwd;
        return false;
    }
}
