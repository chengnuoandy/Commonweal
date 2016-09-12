package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/11.
 */

public class VerifyRecord extends BmobObject {
    private User_Profile Ver_User;
    private String WbID;
    private String WBReson;

    public User_Profile getVer_User() {
        return Ver_User;
    }

    public void setVer_User(User_Profile ver_User) {
        Ver_User = ver_User;
    }

    public String getWbID() {
        return WbID;
    }

    public void setWbID(String wbID) {
        WbID = wbID;
    }

    public String getWBReson() {
        return WBReson;
    }

    public void setWBReson(String WBReson) {
        this.WBReson = WBReson;
    }
}
