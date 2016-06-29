package com.goldenratio.commonweal.bean;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by CharmNight on 2016/6/10.
 */

public class Help extends BmobObject {

    private String Help_ID; //项目ID（Help+用户ID/公益组织ID+随机码）
    private String Help_Type; // 项目类型
    private String Help_Money;//项目申请金额
    private String Help_MoneyUse;//项目申请金额用途
    private BmobDate Help_StartDate; //项目开始时间
    private BmobDate Help_EndDate; // 项目结束时间
    private String Help_Pic;       //相关图片
    private String Help_Title;//项目标题
    private String Help_OneSentence;
    private String Help_Org;    //项目发起组织
    private String Help_Initiator;    //项目发起人&执行者
    private String Help_Smile;    //项目受助者
    private String Help_SmilePro;    //项目受助者省份
    private String Help_SmileCity;    //项目受助者城市
    private String Help_Content;    //项目内容(显示在首页上的)
    private String Help_Html; //项目全部信息
    private String Help_Result; //项目结果
    private String Help_DonateSum;

    public String getHelp_ID() {
        return Help_ID;
    }

    public void setHelp_ID(String help_ID) {
        Help_ID = help_ID;
    }

    public String getHelp_Type() {
        return Help_Type;
    }

    public void setHelp_Type(String help_Type) {
        Help_Type = help_Type;
    }

    public String getHelp_Money() {
        return Help_Money;
    }

    public void setHelp_Money(String help_Money) {
        Help_Money = help_Money;
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

    public String getHelp_Org() {
        return Help_Org;
    }

    public void setHelp_Org(String help_Org) {
        Help_Org = help_Org;
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

    public String getHelp_Html() {
        return Help_Html;
    }

    public void setHelp_Html(String help_Html) {
        Help_Html = help_Html;
    }

    public String getHelp_Result() {
        return Help_Result;
    }

    public void setHelp_Result(String help_Result) {
        Help_Result = help_Result;
    }

    public String getHelp_DonateSum() {
        return Help_DonateSum;
    }

    public void setHelp_DonateSum(String help_DonateSum) {
        Help_DonateSum = help_DonateSum;
    }
}