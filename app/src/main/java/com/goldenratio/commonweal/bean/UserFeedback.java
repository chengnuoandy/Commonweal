package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 冰封承諾Andy on 2016/8/18 0018.
 * 用户反馈的实体
 */
public class UserFeedback extends BmobObject {

    //反馈内容
    private String content;
    private List pic;
    //联系方式
    private String contacts;

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List getPic() {
        return pic;
    }

    public void setPic(List pic) {
        this.pic = pic;
    }
}
