package com.goldenratio.commonweal.dao.entity;

/**
 * Created by Administrator on 2016/6/11.
 */

public class User {

    private String ObjectId;
    private String User_ID;
    private String User_Name;
    private String User_Autograph;
    private String User_Avatar;

    public User(String objectId,String user_ID, String user_Name, String user_Autograph,String user_Avatar) {
        ObjectId=objectId;
        User_ID = user_ID;
        User_Name = user_Name;
        User_Autograph = user_Autograph;
        User_Avatar = user_Avatar;
    }

    public String getObjectId() {

        return ObjectId;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public String getUser_Autograph() {
        return User_Autograph;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public void setUser_Autograph(String user_Autograph) {
        User_Autograph = user_Autograph;
    }

    public void setObjectId(String objectId) {
        ObjectId = objectId;
    }
}
