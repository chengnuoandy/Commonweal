package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/20.
 */

public class Business extends BmobObject {
    private String Business_Name;//交易名称
    private String Business_Use;//交易对象  ----商户对象
    private int Business_type;  //交易类型   公益币 或 Rmb
    private String Business_time; //交易时间
    private String Business_state;//交易状态 是否支付成功 ---当前状态
    private String Business_payment;   //支付方式
    private String Business_money;  //交易金额
    private String Business_numbers;//交易单号
    private String Customer_numbers;//客户单号

    public String getBusiness_Name() {
        return Business_Name;
    }

    public void setBusiness_Name(String business_Name) {
        Business_Name = business_Name;
    }

    public String getBusiness_Use() {
        return Business_Use;
    }

    public void setBusiness_Use(String business_Use) {
        Business_Use = business_Use;
    }

    public int getBusiness_type() {
        return Business_type;
    }

    public void setBusiness_type(int business_type) {
        Business_type = business_type;
    }

    public String getBusiness_time() {
        return Business_time;
    }

    public void setBusiness_time(String business_time) {
        Business_time = business_time;
    }

    public String getBusiness_state() {
        return Business_state;
    }

    public void setBusiness_state(String business_state) {
        Business_state = business_state;
    }

    public String getBusiness_payment() {
        return Business_payment;
    }

    public void setBusiness_payment(String business_payment) {
        Business_payment = business_payment;
    }

    public String getBusiness_money() {
        return Business_money;
    }

    public void setBusiness_money(String business_money) {
        Business_money = business_money;
    }

    public String getBusiness_numbers() {
        return Business_numbers;
    }

    public void setBusiness_numbers(String business_numbers) {
        Business_numbers = business_numbers;
    }

    public String getCustomer_numbers() {
        return Customer_numbers;
    }

    public void setCustomer_numbers(String customer_numbers) {
        Customer_numbers = customer_numbers;
    }
}
