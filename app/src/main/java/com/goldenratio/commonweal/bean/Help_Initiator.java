package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 冰封承諾Andy on 2016/8/15 0015.
 * 发起方介绍页
 */
public class Help_Initiator extends BmobObject {

    private String Initiator_desc;
    private String User_img;

    public String getInitiator_desc() {
        return Initiator_desc;
    }

    public void setInitiator_desc(String initiator_desc) {
        Initiator_desc = initiator_desc;
    }

    public String getUser_img() {
        return User_img;
    }

    public void setUser_img(String user_img) {
        User_img = user_img;
    }
}
