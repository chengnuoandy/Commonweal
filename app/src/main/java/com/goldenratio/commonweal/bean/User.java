package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lvxue on 2016/6/7 0007.
 */
public class User extends BmobObject {
    private String User_NO;     //用户注册顺序
    private String User_ID;//用户ID随机产生，唯一（User+随机码）
    private String User_Name;//用户昵称
    private String User_Phone;//用户手机号
    private String User_Password;   //用户密码
    private String User_Address;//用户地址
    private String User_Receive_Address;//用户收货地址
    private String User_Integral;//用户积分

    public void setUser_Address(String user_Address) {
        User_Address = user_Address;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public void setUser_Integral(String user_Integral) {
        User_Integral = user_Integral;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public void setUser_NO(String user_NO) {
        User_NO = user_NO;
    }

    public void setUser_Password(String user_Password) {
        User_Password = user_Password;
    }

    public void setUser_Phone(String user_Phone) {
        User_Phone = user_Phone;
    }

    public void setUser_Receive_Address(String user_Receive_Address) {
        User_Receive_Address = user_Receive_Address;
    }

    public String getUser_Address() {
        return User_Address;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public String getUser_Integral() {
        return User_Integral;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public String getUser_NO() {
        return User_NO;
    }

    public String getUser_Password() {
        return User_Password;
    }

    public String getUser_Phone() {
        return User_Phone;
    }

    public String getUser_Receive_Address() {
        return User_Receive_Address;
    }
}
