package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kiuber on 2016/8/16.
 */

public class Deposit extends BmobObject {
    private String D_Coin;
    private User_Profile D_User;
    private String D_GoodId;
    private String OrderId;

    public String getD_Coin() {
        return D_Coin;
    }

    public void setD_Coin(String d_Coin) {
        D_Coin = d_Coin;
    }

    public User_Profile getD_User() {
        return D_User;
    }

    public void setD_User(User_Profile d_User) {
        D_User = d_User;
    }

    public String getD_GoodId() {
        return D_GoodId;
    }

    public void setD_GoodId(String d_GoodId) {
        D_GoodId = d_GoodId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }
}
