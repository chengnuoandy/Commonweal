package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/20.
 */

public class Good_Comment extends BmobObject {
    private User_Profile comment_user;//用户
    private String comment;//内容
    private String objcetid;//标识
    private String reply;//回复对象

    public User_Profile getComment_user() {
        return comment_user;
    }

    public void setComment_user(User_Profile comment_user) {
        this.comment_user = comment_user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getObjcetid() {
        return objcetid;
    }

    public void setObjcetid(String objcetid) {
        this.objcetid = objcetid;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

}
