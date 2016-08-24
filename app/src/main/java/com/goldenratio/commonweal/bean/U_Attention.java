package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/17.
 * Email:jxfengmtx@163.com
 */
public class U_Attention extends BmobObject {
    User_Profile Star_Info;
    User_Profile User_Info;
    String U_ID;
    String Star_ID;

    public User_Profile getStar_Info() {
        return Star_Info;
    }

    public void setStar_Info(User_Profile star_Info) {
        Star_Info = star_Info;
    }

    public User_Profile getUser_Info() {
        return User_Info;
    }

    public void setUser_Info(User_Profile user_Info) {
        User_Info = user_Info;
    }

    public String getU_ID() {
        return U_ID;
    }

    public void setU_ID(String u_ID) {
        U_ID = u_ID;
    }

    public String getStar_ID() {
        return Star_ID;
    }

    public void setStar_ID(String star_ID) {
        Star_ID = star_ID;
    }
}
