package com.goldenratio.commonweal.util;

/**
 * Created by Administrator on 2016/8/9.
 */

public class Comment {

    private String userID;
    public String UserName;
    public String times;
    public String icom;
    public String comment;
    public String reply;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getIcom() {
        return icom;
    }

    public void setIcom(String icom) {
        this.icom = icom;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
