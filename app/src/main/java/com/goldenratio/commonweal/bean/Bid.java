package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/9.
 */

public class Bid extends BmobObject {
    private User_Profile Bid_User;
    private Good Bid_Good;
    private String Bid_Coin;

    public User_Profile getBid_User() {
        return Bid_User;
    }

    public void setBid_User(User_Profile bid_User) {
        Bid_User = bid_User;
    }

    public Good getBid_Good() {
        return Bid_Good;
    }

    public void setBid_Good(Good bid_Good) {
        Bid_Good = bid_Good;
    }

    public String getBid_Coin() {
        return Bid_Coin;
    }

    public void setBid_Coin(String bid_Coin) {
        Bid_Coin = bid_Coin;
    }
}
