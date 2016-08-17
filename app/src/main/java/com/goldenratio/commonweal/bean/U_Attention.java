package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/17.
 * Email:jxfengmtx@163.com
 */
public class U_Attention extends BmobObject{
    User_Profile Star_ID;
    String U_ID;

    public User_Profile getStar_ID() {
        return Star_ID;
    }

    public void setStar_ID(User_Profile star_ID) {
        Star_ID = star_ID;
    }

    public String getU_ID() {
        return U_ID;
    }

    public void setU_ID(String u_ID) {
        U_ID = u_ID;
    }

}
