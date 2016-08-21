package com.goldenratio.commonweal.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kiuber on 2016-08-21.
 */
public class Help_Top extends BmobObject implements Serializable {

    private String Help_Top_Pic;
    private String Help_Top_Url;
    private String Help_Top_Title;

    public String getHelp_Top_Pic() {
        return Help_Top_Pic;
    }

    public void setHelp_Top_Pic(String help_Top_Pic) {
        Help_Top_Pic = help_Top_Pic;
    }

    public String getHelp_Top_Url() {
        return Help_Top_Url;
    }

    public void setHelp_Top_Url(String help_Top_Url) {
        Help_Top_Url = help_Top_Url;
    }

    public String getHelp_Top_Title() {
        return Help_Top_Title;
    }

    public void setHelp_Top_Title(String help_Top_Title) {
        Help_Top_Title = help_Top_Title;
    }
}
