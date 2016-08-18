package com.goldenratio.commonweal.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by CharmNight on 2016/6/10.
 */

public class Help extends BmobObject implements Serializable{

    private String Help_Type; // 项目类型
    private String Help_Coin;//项目申请金额
    private String Help_MoneyUse;//项目申请金额用途
    private BmobDate Help_StartDate; //项目开始时间
    private BmobDate Help_EndDate; // 项目结束时间
    private String Help_Pic;       //相关图片
    private String Help_Title;//项目标题
    private String Help_OneSentence;
    private String Help_Initiator;    //项目发起组织
    private String Help_Execute;

    private String Help_Smile;    //项目受助者
    private String Help_SmilePro;    //项目受助者省份
    private String Help_SmileCity;    //项目受助者城市
    private String Help_Content;    //项目内容(显示在首页上的)
    private String Help_Url; //项目全部信息
    private String Help_Result; //项目结果

    private Help_Initiator InitiatorInfo; //发起方详细信息

    public com.goldenratio.commonweal.bean.Help_Initiator getInitiatorInfo() {
        return InitiatorInfo;
    }

    public void setInitiatorInfo(com.goldenratio.commonweal.bean.Help_Initiator initiatorInfo) {
        InitiatorInfo = initiatorInfo;
    }

    public String getHelp_Execute() {
        return Help_Execute;
    }

    public void setHelp_Execute(String help_Execute) {
        Help_Execute = help_Execute;
    }



    public String getHelp_Type() {
        return Help_Type;
    }

    public void setHelp_Type(String help_Type) {
        Help_Type = help_Type;
    }

    public String getHelp_Coin() {
        return Help_Coin;
    }

    public void setHelp_Coin(String help_Coin) {
        Help_Coin = help_Coin;
    }

    public String getHelp_MoneyUse() {
        return Help_MoneyUse;
    }

    public void setHelp_MoneyUse(String help_MoneyUse) {
        Help_MoneyUse = help_MoneyUse;
    }

    public BmobDate getHelp_StartDate() {
        return Help_StartDate;
    }

    public void setHelp_StartDate(BmobDate help_StartDate) {
        Help_StartDate = help_StartDate;
    }

    public BmobDate getHelp_EndDate() {
        return Help_EndDate;
    }

    public void setHelp_EndDate(BmobDate help_EndDate) {
        Help_EndDate = help_EndDate;
    }

    public String getHelp_Pic() {
        return Help_Pic;
    }

    public void setHelp_Pic(String help_Pic) {
        Help_Pic = help_Pic;
    }

    public String getHelp_Title() {
        return Help_Title;
    }

    public void setHelp_Title(String help_Title) {
        Help_Title = help_Title;
    }

    public String getHelp_OneSentence() {
        return Help_OneSentence;
    }

    public void setHelp_OneSentence(String help_OneSentence) {
        Help_OneSentence = help_OneSentence;
    }

    public String getHelp_Initiator() {
        return Help_Initiator;
    }

    public void setHelp_Initiator(String help_Initiator) {
        Help_Initiator = help_Initiator;
    }



    public String getHelp_Smile() {
        return Help_Smile;
    }

    public void setHelp_Smile(String help_Smile) {
        Help_Smile = help_Smile;
    }

    public String getHelp_SmilePro() {
        return Help_SmilePro;
    }

    public void setHelp_SmilePro(String help_SmilePro) {
        Help_SmilePro = help_SmilePro;
    }

    public String getHelp_SmileCity() {
        return Help_SmileCity;
    }

    public void setHelp_SmileCity(String help_SmileCity) {
        Help_SmileCity = help_SmileCity;
    }

    public String getHelp_Content() {
        return Help_Content;
    }

    public void setHelp_Content(String help_Content) {
        Help_Content = help_Content;
    }

    public String getHelp_Url() {
        return Help_Url;
    }

    public void setHelp_Url(String help_Url) {
        Help_Url = help_Url;
    }

    public String getHelp_Result() {
        return Help_Result;
    }

    public void setHelp_Result(String help_Result) {
        Help_Result = help_Result;
    }

}