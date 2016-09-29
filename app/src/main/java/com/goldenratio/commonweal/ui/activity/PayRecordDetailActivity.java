package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.PayRecord;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.io.Serializable;

/**
 * Created by Kiuber on 2016/8/23.
 */
public class PayRecordDetailActivity extends BaseActivity {

    private TextView mTvMoney;
    private TextView mTvCoin;
    private TextView mTvName;
    private TextView mTvType;
    private TextView mTvPayWay;
    private TextView mTvStatus;
    private TextView mTvNumber;
    private TextView mTvTime;
    private PayRecord payRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record_detailed);
        initView();
        initData();
        setTextViewText();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initView() {
        mTvMoney = (TextView) findViewById(R.id.tv_pr_money);
        mTvCoin = (TextView) findViewById(R.id.tv_pr_coin);
        mTvName = (TextView) findViewById(R.id.tv_pr_name);
        mTvType = (TextView) findViewById(R.id.tv_pr_type);
        mTvPayWay = (TextView) findViewById(R.id.tv_pr_pay_way);
        mTvStatus = (TextView) findViewById(R.id.tv_pr_status);
        mTvNumber = (TextView) findViewById(R.id.tv_pr_number);
        mTvTime = (TextView) findViewById(R.id.tv_pr_time);
    }

    private void initData() {
        payRecord = (PayRecord) getIntent().getSerializableExtra("PayRecord");
    }

    private void setTextViewText() {
        mTvName.setText(payRecord.getPR_Name());
        String type = payRecord.getPR_Type();
        mTvType.setText(type);
        if (type.equals("公益币支出")) {
            mTvCoin.setText(payRecord.getPR_Coin());
        } else if (type.equals("充值公益币")) {
            mTvCoin.setVisibility(View.GONE);
            mTvMoney.setText("¥ " + payRecord.getPR_Money());
            mTvMoney.setVisibility(View.VISIBLE);
        }
        switch (payRecord.getPR_PayWay()) {
            case 1:
                mTvPayWay.setText("支付宝支付");
                break;
            case 2:
                mTvPayWay.setText("微信支付");
                break;
            case 3:
                mTvPayWay.setText("账号公益币支付");
                break;
        }
        if (payRecord.isPR_Status()) {
            mTvStatus.setText("支付成功");
        } else {
            mTvStatus.setText("支付失败");
        }
        mTvNumber.setText(payRecord.getPR_Number());
        mTvTime.setText(payRecord.getCreatedAt());
    }
}
