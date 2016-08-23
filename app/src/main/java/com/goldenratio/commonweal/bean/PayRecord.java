package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/20.
 */

public class PayRecord extends BmobObject {
    private String PR_Name;
    private String PR_Money;
    private String PR_Coin;
    private String PR_Type;
    private String PR_PayType;
    private boolean PR_Status;
    private String PR_Number;
    private String User_ID;


    /*
        public String Business_Name;//交易名称
        public String Business_state;//交易状态 是否支付成功 ---当前状态
        public String Business_money;  //交易金额
        public String Business_time;//交易时间
        public String Business_Use;//交易对象  ----商户对象
        public int Business_type;  //交易类型   公益币 或 Rmb
        public String Business_payment;   //支付方式
        public String Business_numbers;//交易单号
        public String Customer_numbers;//客户单号
         */

    public String getPR_Name() {
        return PR_Name;
    }

    public void setPR_Name(String PR_Name) {
        this.PR_Name = PR_Name;
    }

    public String getPR_Money() {
        return PR_Money;
    }

    public void setPR_Money(String PR_Money) {
        this.PR_Money = PR_Money;
    }

    public String getPR_Coin() {
        return PR_Coin;
    }

    public void setPR_Coin(String PR_Coin) {
        this.PR_Coin = PR_Coin;
    }

    public String getPR_Type() {
        return PR_Type;
    }

    public void setPR_Type(String PR_Type) {
        this.PR_Type = PR_Type;
    }

    public String getPR_PayType() {
        return PR_PayType;
    }

    public void setPR_PayType(String PR_PayType) {
        this.PR_PayType = PR_PayType;
    }

    public boolean isPR_Status() {
        return PR_Status;
    }

    public void setPR_Status(boolean PR_Status) {
        this.PR_Status = PR_Status;
    }

    public String getPR_Number() {
        return PR_Number;
    }

    public void setPR_Number(String PR_Number) {
        this.PR_Number = PR_Number;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }
}
