package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/14.
 * Email:jxfengmtx@163.com
 */
public class Dynamic_Help extends BmobObject {
    private String initiator_Name;
    private String initiator_Image;
    private String Help_Content;
    private List<String> Help_Pic;
    private Help_Initiator mHelp_initiator;

    public Help_Initiator getHelp_initiator() {
        return mHelp_initiator;
    }

    public void setHelp_initiator(Help_Initiator help_initiator) {
        mHelp_initiator = help_initiator;
    }

    public List<String> getHelp_Pic() {
        return Help_Pic;
    }

    public void setHelp_Pic(List<String> help_Pic) {
        Help_Pic = help_Pic;
    }


    public String getInitiator_Name() {
        return initiator_Name;
    }

    public void setInitiator_Name(String initiator_Name) {
        this.initiator_Name = initiator_Name;
    }

    public String getInitiator_Image() {
        return initiator_Image;
    }

    public void setInitiator_Image(String initiator_Image) {
        this.initiator_Image = initiator_Image;
    }

    public String getHelp_Content() {
        return Help_Content;
    }

    public void setHelp_Content(String help_Content) {
        Help_Content = help_Content;
    }


}
