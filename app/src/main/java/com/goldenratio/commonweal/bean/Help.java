package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by CharmNight on 2016/6/10.
 */

public class Help extends BmobObject {
    private List Help_pic;       //相关图片
    private Integer Help_Id;            //项目ID（Help+用户ID/公益组织ID+随机码）
    private String Help_Initiator;    //项目发起人&执行者
    private String Help_Sponsor;     //赞助方
    private String Help_Title;      //项目标题
    private String Help_Content;    //项目内容(显示在首页上的)
    private Integer Help_Progress;      //项目进度
    private Integer Help_CountDown;    //项目的时间倒计时
    private String Help_Result;     //项目结果
    private String Help_Content_content;//项目的内容简介

    public String getHelp_Content_content() {
        return Help_Content_content;
    }

    public void setHelp_Content_content(String help_Content_content) {
        Help_Content_content = help_Content_content;
    }

    public String getHelp_Initiator() {
        return Help_Initiator;
    }

    public void setHelp_Initiator(String help_Initiator) {
        Help_Initiator = help_Initiator;
    }

    public void setHelp_Progress(Integer help_Progress) {
        Help_Progress = help_Progress;
    }

    public String getHelp_Sponsor() {
        return Help_Sponsor;
    }

    public void setHelp_Sponsor(String help_Sponsor) {
        Help_Sponsor = help_Sponsor;
    }





    private String Help_Top_pic;

    public String getHelp_Top_pic(){return Help_Top_pic;}
    public void setHelp_Top_pic(String help_top_pic){
        Help_Top_pic = help_top_pic;
    }

    public List getHelp_pic() {
        return Help_pic;
    }

    public void setHelp_pic(List help_pic) {
        Help_pic = help_pic;
    }

    public Integer getHelp_Id() {
        return Help_Id;
    }

    public void setHelp_Id(int help_Id) {
        Help_Id = help_Id;
    }



    public String getHelp_Title() {
        return Help_Title;
    }

    public void setHelp_Title(String help_Title) {
        Help_Title = help_Title;
    }

    public String getHelp_Content() {
        return Help_Content;
    }

    public void setHelp_Content(String help_Content) {
        Help_Content = help_Content;
    }

    public Integer getHelp_Progress() {
        return Help_Progress;
    }

    public void setHelp_Progress(int help_Progress) {
        Help_Progress = help_Progress;
    }

    public Integer getHelp_CountDown() {
        return Help_CountDown;
    }

    public void setHelp_CountDown(int help_CountDown) {
        Help_CountDown = help_CountDown;
    }

    public String getHelp_Result() {
        return Help_Result;
    }

    public void setHelp_Result(String help_Result) {
        Help_Result = help_Result;
    }
}
