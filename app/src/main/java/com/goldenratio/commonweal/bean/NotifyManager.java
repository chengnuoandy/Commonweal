package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 龙啸天 - Jxfen on 2016/9/11.
 * Email:jxfengmtx@163.com
 */
public class NotifyManager extends BmobObject {
    private String notifyID;
    private String userID;

    private Message mMessageID;
    public String getNotifyID() {
        return notifyID;
    }

    public void setNotifyID(String notifyID) {
        this.notifyID = notifyID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Message getMessageID() {
        return mMessageID;
    }

    public void setMessageID(Message messageID) {
        mMessageID = messageID;
    }
}
