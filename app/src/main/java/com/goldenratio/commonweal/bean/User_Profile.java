package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kiuber on 2016/8/8.
 */

public class User_Profile extends BmobObject {

    private String User_Name;
    private String User_Nickname;
    private String User_Autograph;
    private String User_Phone;
    private String User_Password;
    private String User_Address;
    private String User_WbID;
    private String User_Sex;
    private String User_Coin;
    private String User_DeviceInfo;
    private List<String> User_Attention;
    private boolean User_VerStatus;
    private String User_image_min;
    private String User_image_max;
    private String User_image_hd;
    private String User_VerifiedReason;
    private boolean User_IsV;
    private List<String> User_Receive_Address;


    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Nickname() {
        return User_Nickname;
    }

    public void setUser_Nickname(String user_Nickname) {
        User_Nickname = user_Nickname;
    }

    public String getUser_Autograph() {
        return User_Autograph;
    }

    public void setUser_Autograph(String user_Autograph) {
        User_Autograph = user_Autograph;
    }

    public String getUser_Phone() {
        return User_Phone;
    }

    public void setUser_Phone(String user_Phone) {
        User_Phone = user_Phone;
    }

    public String getUser_Password() {
        return User_Password;
    }

    public void setUser_Password(String user_Password) {
        User_Password = user_Password;
    }

    public String getUser_Address() {
        return User_Address;
    }

    public void setUser_Address(String user_Address) {
        User_Address = user_Address;
    }

    public String getUser_WbID() {
        return User_WbID;
    }

    public void setUser_WbID(String user_WbID) {
        User_WbID = user_WbID;
    }

    public String getUser_Sex() {
        return User_Sex;
    }

    public void setUser_Sex(String user_Sex) {
        User_Sex = user_Sex;
    }

    public String getUser_Coin() {
        return User_Coin;
    }

    public void setUser_Coin(String user_Coin) {
        User_Coin = user_Coin;
    }

    public String getUser_DeviceInfo() {
        return User_DeviceInfo;
    }

    public void setUser_DeviceInfo(String user_DeviceInfo) {
        User_DeviceInfo = user_DeviceInfo;
    }

    public List<String> getUser_Attention() {
        return User_Attention;
    }

    public void setUser_Attention(List<String> user_Attention) {
        User_Attention = user_Attention;
    }

    public boolean isUser_VerStatus() {
        return User_VerStatus;
    }

    public void setUser_VerStatus(boolean user_VerStatus) {
        User_VerStatus = user_VerStatus;
    }

    public String getUser_image_min() {
        return User_image_min;
    }

    public void setUser_image_min(String user_image_min) {
        User_image_min = user_image_min;
    }

    public String getUser_image_max() {
        return User_image_max;
    }

    public void setUser_image_max(String user_image_max) {
        User_image_max = user_image_max;
    }

    public String getUser_image_hd() {
        return User_image_hd;
    }

    public void setUser_image_hd(String user_image_hd) {
        User_image_hd = user_image_hd;
    }

    public String getUser_VerifiedReason() {
        return User_VerifiedReason;
    }

    public void setUser_VerifiedReason(String user_VerifiedReason) {
        User_VerifiedReason = user_VerifiedReason;
    }

    public boolean isUser_IsV() {
        return User_IsV;
    }

    public void setUser_IsV(boolean user_IsV) {
        User_IsV = user_IsV;
    }

    public List<String> getUser_Receive_Address() {
        return User_Receive_Address;
    }

    public void setUser_Receive_Address(List<String> user_Receive_Address) {
        User_Receive_Address = user_Receive_Address;
    }
}
