package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/20.
 */

public class Business extends BmobObject {
    private String Business_Name;//交易名称
    private String Business_Use;//交易对象  ----商户对象
    private int Business_Type;  //交易类型   公益币 或 Rmb
    private String Business_time; //交易时间
    private String Business_state;//交易状态 是否支付成功 ---当前状态
    private String Business_payment;   //支付方式
    private String Business_Coin;  //交易金额
    private String Business_Number;//交易单号
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

    public int getBusiness_Type() {
        return Business_Type;
    }

    public void setBusiness_Type(int business_Type) {
        Business_Type = business_Type;
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

    public String getBusiness_Coin() {
        return Business_Coin;
    }

    public void setBusiness_Coin(String business_Coin) {
        Business_Coin = business_Coin;
    }

    public String getBusiness_Number() {
        return Business_Number;
    }

    public void setBusiness_Number(String business_Number) {
        Business_Number = business_Number;
    }

    public String getCustomer_numbers() {
        return Customer_numbers;
    }

    public void setCustomer_numbers(String customer_numbers) {
        Customer_numbers = customer_numbers;
    }
}
