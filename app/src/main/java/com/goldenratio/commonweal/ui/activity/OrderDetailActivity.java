package com.goldenratio.commonweal.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.SetAddressListAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlOrder;
import com.goldenratio.commonweal.bean.PayCoinRecord;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.ui.activity.my.LogisticsInformation;
import com.goldenratio.commonweal.ui.activity.my.SetAddressActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kiuber on 2016/8/17.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener, IMySqlManager {

    private TextView mBtnPay;
    private TextView mTvName;
    private TextView mBtnExpress;
    private MySqlOrder mySqlOrder;
    private TextView mTvCoin;
    private String user_coin;
    private MySqlManagerImpl mySqlManager;
    private String mUserId;
    private String mUserCoin;
    private String mSixPwd;
    private ProgressDialog progressDialog;
    private ImageView mIvGood;
    private RadioButton mRvPayType;
    private TextView mTvAdress;
    private TextView mTvAddressName;
    private TextView mTvAddressTel;
    private TextView mTvAddress;
    private TextView mTvChangeAddress;
    private ArrayList<String> address;  //存储收货地址


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initData();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initData() {
        mySqlOrder = (MySqlOrder) getIntent().getSerializableExtra("orderList");
        Good order_good = mySqlOrder.getOrder_Good();
        Glide.with(this).load(mySqlOrder.getOrder_Good().getGood_Photos().get(0)).into(mIvGood);
        mTvName.setText(order_good.getGood_Name());
        mTvCoin.setText(order_good.getGood_NowCoin());
        if (order_good.getGood_Status() == 1) {
            mRvPayType.setVisibility(View.VISIBLE);
            mBtnPay.setVisibility(View.VISIBLE);
        }
        if (order_good.getGood_Status() == 3) {
            mBtnExpress.setVisibility(View.VISIBLE);
        }
        mySqlManager = new MySqlManagerImpl(this, this);
        mUserId = ((MyApplication) getApplication()).getObjectID();


        List<String> user_receive_address = mySqlOrder.getOrder_User().getUser_Receive_Address();
        List list = checkDefaultAddress(user_receive_address);

        address = (ArrayList<String>) user_receive_address;
        if (address.size() != 1 && (address.size() - 1) % 3 == 0) {
            mTvAddressName.setText(list.get(0).toString());
            mTvAddressTel.setText(list.get(1).toString());
            mTvAdress.setText(list.get(2).toString());
        } else {
            mBtnPay.setText("请先设置收货地址");
            mBtnPay.setClickable(false);
        }
    }

    private void initView() {
        mIvGood = (ImageView) findViewById(R.id.iv_good);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvCoin = (TextView) findViewById(R.id.tv_coin);
        mBtnPay = (TextView) findViewById(R.id.btn_pay);
        mTvAdress = (TextView) findViewById(R.id.tv_address);
        mRvPayType = (RadioButton) findViewById(R.id.rb_pay_type);
        mTvAddressName = (TextView) findViewById(R.id.tv_address_name);
        mTvAddressTel = (TextView) findViewById(R.id.tv_address_name);
        mTvAddress = (TextView) findViewById(R.id.tv_address_name);
        mTvChangeAddress = (TextView) findViewById(R.id.tv_change_address);
        mTvChangeAddress.setOnClickListener(this);
        mBtnPay.setOnClickListener(this);
        mBtnExpress = (TextView) findViewById(R.id.btn_express);
        mBtnExpress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay:
                mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null);
                break;
            case R.id.btn_express:
                Intent intent = new Intent(this, LogisticsInformation.class);
                intent.putExtra("company", mySqlOrder.getOrder_Company());
                intent.putExtra("code", mySqlOrder.getOrder_Code());
                startActivity(intent);
                break;
            case R.id.iv_good:
                Intent intent1 = new Intent(this, GoodDetailActivity.class);
                intent1.putExtra("objectId", mySqlOrder.getOrder_Good().getObjectId());
                startActivity(intent1);
                break;
            case R.id.tv_change_address:
                startActivity(new Intent(this, SetAddressActivity.class));
                break;
        }
    }

    @Override
    public void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin) {

    }

    @Override
    public void showSixPwdOnFinishInput(String sixPwd, int event) {
        progressDialog = ProgressDialog.show(OrderDetailActivity.this, null, null, false);
        if (event == 1) {
            payOrder(mySqlOrder.getObject_Id(), mUserId, mySqlOrder.getOrder_Good().getGood_NowCoin());
        } else {
            progressDialog.dismiss();
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
        double payPoorCoin = Double.valueOf(mStrUserCoin) - Double.valueOf(mySqlOrder.getOrder_Good().getGood_NowCoin());
        double payPoorMoney = (-payPoorCoin) / 10;
        if (payPoorCoin < 0) {
            mySqlManager.pay(false, payPoorMoney + payPoorCoin * 0.05, payPoorMoney * 10 + Double.valueOf(mStrUserCoin), payPoorMoney + "");
        } else {
            mySqlManager = new MySqlManagerImpl(this, this, "支付物品订单", mySqlOrder.getOrder_Good().getGood_NowCoin() + "", "订单支付");
            mySqlManager.showSixPwdOnFinishInput(sixPwd, 1);
        }
        return false;
    }

    @Override
    public boolean updateUserSixPwdByObjectId(String sixPwd) {
        return false;
    }

    private void payOrder(String good, final String user, String userCoin) {
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
                            progressDialog.dismiss();
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
                                Good good1 = new Good();
                                good1.setObjectId(mySqlOrder.getOrder_Good().getObjectId());

                                User_Profile user_profile = new User_Profile();
                                user_profile.setObjectId(mySqlOrder.getOrder_User().getObjectId());
                                PayCoinRecord payCoinRecord = new PayCoinRecord();
                                payCoinRecord.setPC_Coin(mySqlOrder.getOrder_Good().getGood_NowCoin());
                                payCoinRecord.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Good good2 = new Good();
                                            good2.setGood_Status(2);
                                            good2.update(mySqlOrder.getOrder_Good().getObjectId(), new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null) {
                                                        Toast.makeText(OrderDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                                        pushMessage();
                                                        progressDialog.dismiss();
                                                        mBtnPay.setVisibility(View.GONE);
                                                        mRvPayType.setVisibility(View.GONE);
                                                    } else {
                                                        ErrorCodeUtil.switchErrorCode(OrderDetailActivity.this, e.getErrorCode() + "");
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            ErrorCodeUtil.switchErrorCode(OrderDetailActivity.this, e.getErrorCode() + "");
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(OrderDetailActivity.this, "支付失败" + result, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "服务地址获取错误！", Toast.LENGTH_SHORT).show();
        }
    }

    private void pushMessage() {
    }


    private List checkDefaultAddress(List<String> address) {
        List<String> dutAddress = new ArrayList<>();
        int defaultAdrsPos = (3 * Integer.parseInt(address.get(0))) + 1;
        dutAddress.add(address.get(defaultAdrsPos));
        dutAddress.add(address.get(defaultAdrsPos + 1));
        dutAddress.add(address.get(defaultAdrsPos + 2));
        return dutAddress;
    }
}
