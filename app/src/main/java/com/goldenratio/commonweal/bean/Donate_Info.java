package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/24.
 * Email:jxfengmtx@163.com
 */
public class Donate_Info extends BmobObject {
    String User_ID;
    User_Profile User_Info;
    String Help_ID;
    Double Donate_Coin;

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public User_Profile getUser_Info() {
        return User_Info;
    }

    public void setUser_Info(User_Profile user_Info) {
        User_Info = user_Info;
    }

    public String getHelp_ID() {
        return Help_ID;
    }

    public void setHelp_ID(String help_ID) {
        Help_ID = help_ID;
    }

    public Double getDonate_Coin() {
        return Donate_Coin;
    }

    public void setDonate_Coin(Double donate_Coin) {
        Donate_Coin = donate_Coin;
    }
}
