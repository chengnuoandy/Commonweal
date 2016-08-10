package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/9.
 */

public class Auction extends BmobObject {
    private Good Auc_Good;
    private User_Profile Auc_User;
    private String Auc_Coin;

    public String getAuc_Coin() {
        return Auc_Coin;
    }

    public void setAuc_Coin(String auc_Coin) {
        Auc_Coin = auc_Coin;
    }

    public Good getAuc_Good() {
        return Auc_Good;
    }

    public void setAuc_Good(Good auc_Good) {
        Auc_Good = auc_Good;
    }

    public User_Profile getAuc_User() {
        return Auc_User;
    }

    public void setAuc_User(User_Profile auc_User) {
        Auc_User = auc_User;
    }
}
