package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

/**
 * Created by Administrator on 2016/8/20.
 */

public class BusinessDetailed extends Activity {


    private String money;
    private String name;
    private String business_number;
    private String customer_numbers;
    private String state;
    private String time;
    private String use;
    private int type;
    private String payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detailed);

        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        //交易金额
        money = intent.getStringExtra("money");
        //交易名称
        name = intent.getStringExtra("name");
        //交易单号
        business_number = intent.getStringExtra("Business_number");
        //客户单号
        customer_numbers = intent.getStringExtra("Customer_numbers");
        //交易状态 是否支付成功 ---当前状态
        state = intent.getStringExtra("state");
        //交易时间
        time = intent.getStringExtra("time");
        //交易对象  ----商户对象
        use = intent.getStringExtra("use");
        //交易类型   公益币 或 Rmb
        type = intent.getIntExtra("type",0);
        //支付方式
        payment = intent.getStringExtra("payment");
    }

    /**
     * 初始化界面
     *
     */
    private void initView() {
      TextView business_money = (TextView) findViewById(R.id.business_money);
        business_money.setText("￥"+money);
        TextView business_name = (TextView) findViewById(R.id.business_name);
        business_name.setText(name);
        TextView business_use = (TextView) findViewById(R.id.business_use);
        business_use.setText(use);
        TextView business_state = (TextView) findViewById(R.id.business_state);
        business_state.setText(state);
        TextView business_time = (TextView) findViewById(R.id.business_time);
        business_time.setText(time);
        TextView business_payment = (TextView) findViewById(R.id.business_payment);
        business_payment.setText(payment);
        TextView business_numbers = (TextView) findViewById(R.id.business_numbers);
        business_numbers.setText(business_number);
        TextView Customer_numbers = (TextView) findViewById(R.id.Customer_numbers);
        Customer_numbers.setText(customer_numbers);

    }
    

}
