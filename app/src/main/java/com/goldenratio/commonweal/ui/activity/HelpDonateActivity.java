package com.goldenratio.commonweal.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.goldenratio.commonweal.bean.Donate_Info;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
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

public class HelpDonateActivity extends BaseActivity implements IMySqlManager {

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

    private boolean isHasDonate;
    private String helpID;
    private String DonateInfoID;
    private MySqlManagerImpl mySqlManager;

    private Double DonateCoin;
    private ProgressDialog mPd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_donate);
        ButterKnife.bind(this);

        mCoin = "10";
        helpID = getIntent().getStringExtra("help_id");
        mySqlManager = new MySqlManagerImpl(this, this);
        mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null, null);
        queryDonateInfoFromBmob();

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
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void queryDonateInfoFromBmob() {
        String objectID = ((MyApplication) getApplication()).getObjectID();
        BmobQuery<Donate_Info> query = new BmobQuery<Donate_Info>();
        query.addQueryKeys("Donate_Coin");
        query.addWhereEqualTo("Help_ID", helpID);
        query.addWhereEqualTo("User_ID", objectID);
        query.findObjects(new FindListener<Donate_Info>() {
            @Override
            public void done(List<Donate_Info> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        isHasDonate = false;
                    } else {
                        DonateInfoID = list.get(0).getObjectId();
                        DonateCoin = list.get(0).getDonate_Coin();
                        isHasDonate = true;
                        Log.i("lxt", "done: " + DonateInfoID + "00000" + DonateCoin);
                    }
                } else {
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void updateDonateInfoFromBmob(Double sumDonateCoin) {
        Donate_Info donate_info = new Donate_Info();
        donate_info.setDonate_Coin(sumDonateCoin);
        donate_info.update(DonateInfoID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(HelpDonateActivity.this, "更新捐赠记录成功", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(HelpDonateActivity.this, "更新捐赠记录失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void updateHelpCoin(Integer DonateCoin) {
        Help help = new Help();
        help.increment("Help_Now_Coin", DonateCoin);
        help.update(helpID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    cleanUserDonateObjectId();
                    Toast.makeText(HelpDonateActivity.this, "更新公益币成功", Toast.LENGTH_SHORT).show();
                } else {
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void saveDoanteInfoToBmob(Double changeCoin) {
        Donate_Info donate_info = new Donate_Info();
        String objectID = ((MyApplication) getApplication()).getObjectID();
        donate_info.setUser_ID(objectID);
        donate_info.setHelp_ID(helpID);
        donate_info.setDonate_Coin(changeCoin);
        User_Profile u = new User_Profile();
        u.setObjectId(objectID);
        donate_info.setUser_Info(u);
        donate_info.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    DonateInfoID = s;
                    isHasDonate = true;
                    Toast.makeText(HelpDonateActivity.this, "捐赠记录保存成功", Toast.LENGTH_SHORT).show();
                } else
//                    Toast.makeText(HelpDonateActivity.this, "捐赠记录保存失败", Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
            }
        });
    }

    private void cleanUserDonateObjectId() {
        showProgressDialog();
        String objectID = ((MyApplication) getApplication()).getObjectID();
        String webServiceIp = ((MyApplication) (getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String url = webServiceIp + "ClearDonateCoin";
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("userId", objectID)
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
                            Log.i("lxt", "run: " + "清除失败");
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
                            Log.i("lxt", "run: " + "清除成功");
                            if (result.contains("success")) {
                                Log.i("lxt", "run: " + "清除成功");
                            } else {
                                Log.d("Kiuber_LOG", "fail: " + result);
                            }
                            closeProgressDialog();
                        }
                    });
                }
            });
        } else {
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.isLogin();
            Toast.makeText(this, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
        }
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
                if (TextUtils.isEmpty(mSixPwd) || mSixPwd.equals("0")) {
                    Toast.makeText(this, "请先设置六位数密码", Toast.LENGTH_SHORT).show();
                    showPayKeyBoard2();
                } else {
                    double usercoin = Double.valueOf(mStrUserCoin);
                    final double choiceCoin = Double.valueOf(mCoin);

                    final double poor = choiceCoin - usercoin;  //所缺公益币,充值
                    final double sxf = (poor / 10) * 0.05;  //手续费
                    final double money = (poor / 10) * (1 + 0.05);
                    //选择的币数大于用户现有的金币，公益币不足，提示充值
                    if (poor > 0) {
                        final AlertDialog.Builder builder =
                                new AlertDialog.Builder(HelpDonateActivity.this);
                        builder.setMessage("账号公益币：" + usercoin + "，还缺" + poor + "公益币，您将花费"
                                + money + "元（由于个人开发团队限制，" +
                                "平台收取5%的手续费）");
                        builder.setPositiveButton("充值", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //充值金额包括公益币、充值Bmob收取的0.05的费率。
                                mySqlManager.pay(false, money, choiceCoin, poor + "");
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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

    @Override
    public void pay(boolean alipayOrWechatPay, double price, final double allCoin, String changeCoin) {

    }

    public void showPayKeyBoard1(String inputPwd) {
        mySqlManager = new MySqlManagerImpl(this, this, "请输入支付密码", "" + mCoin, "您将支付" + mCoin + "公益币");
        mySqlManager.showSixPwdOnFinishInput(inputPwd, 1);
    }

    public void showPayKeyBoard2() {
        mySqlManager = new MySqlManagerImpl(this, this, "设置六位数密码", "", "新密码");
        mySqlManager.showSixPwdOnFinishInput("", 0);
    }

    public void showSixPwdOnFinishInput(String sixPwd, int event) {
        Log.i("六位数密码", "showSixPwdOnFinishInput: " + sixPwd);
        if (event == 0) {
            mySqlManager.updateUserSixPwdByObjectId(sixPwd);
        } else {
            double usercoin = Double.valueOf(mStrUserCoin);
            final Integer choiceCoin = Integer.valueOf(mCoin);
            updateHelpCoin(choiceCoin);
            mySqlManager.updateUserCoinByObjectId(usercoin - choiceCoin + "", "-" + mCoin, 0);
        }
    }


    @Override
    public void updateUserCoinByObjectId(String sumCoin, String changeCoin, int flag) {
        mStrUserCoin = sumCoin;
        mAvail.setText(sumCoin);
        double coin = Double.valueOf(changeCoin);
        if (coin < 0 || coin == 0) {
            if (isHasDonate) {
                updateDonateInfoFromBmob(DonateCoin - coin);
            } else saveDoanteInfoToBmob(-coin);
        }
    }

    @Override
    public void queryUserCoinAndSixPwdByObjectId(String mUserCoin, String sixPwd, final String DonateCoin) {
        mStrUserCoin = mUserCoin;
        mSixPwd = sixPwd;
        mAvail.setText(mUserCoin);
        if (!TextUtils.isEmpty(DonateCoin)) {
            if (!DonateCoin.equals("0")) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("提示");
                dialog.setMessage("您拍卖取得公益币数量为" + DonateCoin + "，是否全部捐出？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateHelpCoin(Integer.valueOf(DonateCoin));
                        if (isHasDonate) {
                            updateDonateInfoFromBmob(Double.valueOf(DonateCoin));
                        } else saveDoanteInfoToBmob(Double.valueOf(DonateCoin));
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HelpDonateActivity.this.finish();
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    public void updateUserSixPwdByObjectId(String sixPwd) {
        mSixPwd = sixPwd;
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
}
