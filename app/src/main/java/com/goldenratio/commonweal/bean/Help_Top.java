package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 两个人 on 2016-06-20.
 */
public class Help_Top extends BmobObject {

    private String Title;
    private List Help_Top_Pic;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public List getHelp_Top_Pic() {
        return Help_Top_Pic;
    }

    public void setHelp_Top_Pic(List help_Top_Pic) {
        Help_Top_Pic = help_Top_Pic;
    }
}
