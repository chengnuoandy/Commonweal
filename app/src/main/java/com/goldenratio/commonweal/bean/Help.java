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
    private String Help_Sponsor;    //项目发起人
    private String Help_Title;      //项目标题
    private String Help_Content;    //项目内容
    private Integer Help_Progress;      //项目进度
    private Integer Help_CountDown;    //项目的时间倒计时
    private String Help_Result;     //项目结果

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

    public String getHelp_Sponsor() {
        return Help_Sponsor;
    }

    public void setHelp_Sponsor(String help_Sponsor) {
        Help_Sponsor = help_Sponsor;
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
