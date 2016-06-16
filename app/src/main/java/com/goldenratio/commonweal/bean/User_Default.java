package com.goldenratio.commonweal.bean;


import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Lxt on 2016/6/15 0015.
 */


public class User_Default extends BmobObject {

    int User_Default_Res_ID;
    BmobFile User_Def_Av_Hd;
    BmobFile User_Def_Av_Max;
    BmobFile User_Def_Av_Min;
    String User_Def_Av_Hd_Url;
    String User_Def_Av_Max_Url;
    String User_Def_Av_Min_Url;
    String User_Def_Aut;

    public BmobFile getUser_Def_Av_Hd() {
        return User_Def_Av_Hd;
    }

    public void setUser_Def_Av_Hd(BmobFile user_Def_Av_Hd) {
        User_Def_Av_Hd = user_Def_Av_Hd;
    }

    public int getUser_Default_Res_ID() {
        return User_Default_Res_ID;
    }

    public void setUser_Default_Res_ID(int user_Default_Res_ID) {
        User_Default_Res_ID = user_Default_Res_ID;
    }

    public BmobFile getUser_Def_Av_Max() {
        return User_Def_Av_Max;
    }

    public void setUser_Def_Av_Max(BmobFile user_Def_Av_Max) {
        User_Def_Av_Max = user_Def_Av_Max;
    }

    public BmobFile getUser_Def_Av_Min() {
        return User_Def_Av_Min;
    }

    public void setUser_Def_Av_Min(BmobFile user_Def_Av_Min) {
        User_Def_Av_Min = user_Def_Av_Min;
    }

    public String getUser_Def_Av_Hd_Url() {
        return User_Def_Av_Hd_Url;
    }

    public void setUser_Def_Av_Hd_Url(String user_Def_Av_Hd_Url) {
        User_Def_Av_Hd_Url = user_Def_Av_Hd_Url;
    }

    public String getUser_Def_Av_Max_Url() {
        return User_Def_Av_Max_Url;
    }

    public void setUser_Def_Av_Max_Url(String user_Def_Av_Max_Url) {
        User_Def_Av_Max_Url = user_Def_Av_Max_Url;
    }

    public String getUser_Def_Av_Min_Url() {
        return User_Def_Av_Min_Url;
    }

    public void setUser_Def_Av_Min_Url(String user_Def_Av_Min_Url) {
        User_Def_Av_Min_Url = user_Def_Av_Min_Url;
    }

    public String getUser_Def_Aut() {
        return User_Def_Aut;
    }

    public void setUser_Def_Aut(String user_Def_Aut) {
        User_Def_Aut = user_Def_Aut;
    }


}
