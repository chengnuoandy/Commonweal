package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Kiuber on 2016/6/10.
 */

public class Good extends BmobObject {
    private User_Profile Good_User;
    private BmobDate Good_UpDate; //截至日期
    private Long Good_UpDateM; //截止日期 毫秒值
    private String Good_NO; //物品发布顺序
    private String Good_Type; //物品类型
    private String Good_Name; //物品名称
    private String Good_Video; //物品介绍视频
    private List Good_Photos; //物品介绍照片
    private String Good_Description; //物品描述
    private Integer Good_DonationRate; //物品用于捐款的比例
    private String Good_NowCoin; //物品的现在价
    private String Good_StartCoin; //物品的起步价
    private String Good_Praise;
    private User_Profile Good_NowBidUser;
    private Long Good_Five;//五分钟之内不可出价
    private Integer Good_Status;
    private Boolean Good_IsFirstBid;
    private Bid Good_Bid;

    public User_Profile getGood_User() {
        return Good_User;
    }

    public void setGood_User(User_Profile good_User) {
        Good_User = good_User;
    }

    public BmobDate getGood_UpDate() {
        return Good_UpDate;
    }

    public void setGood_UpDate(BmobDate good_UpDate) {
        Good_UpDate = good_UpDate;
    }

    public Long getGood_UpDateM() {
        return Good_UpDateM;
    }

    public void setGood_UpDateM(Long good_UpDateM) {
        Good_UpDateM = good_UpDateM;
    }

    public String getGood_NO() {
        return Good_NO;
    }

    public void setGood_NO(String good_NO) {
        Good_NO = good_NO;
    }

    public String getGood_Type() {
        return Good_Type;
    }

    public void setGood_Type(String good_Type) {
        Good_Type = good_Type;
    }

    public String getGood_Name() {
        return Good_Name;
    }

    public void setGood_Name(String good_Name) {
        Good_Name = good_Name;
    }

    public String getGood_Video() {
        return Good_Video;
    }

    public void setGood_Video(String good_Video) {
        Good_Video = good_Video;
    }

    public List getGood_Photos() {
        return Good_Photos;
    }

    public void setGood_Photos(List good_Photos) {
        Good_Photos = good_Photos;
    }

    public String getGood_Description() {
        return Good_Description;
    }

    public void setGood_Description(String good_Description) {
        Good_Description = good_Description;
    }

    public Integer getGood_DonationRate() {
        return Good_DonationRate;
    }

    public void setGood_DonationRate(Integer good_DonationRate) {
        Good_DonationRate = good_DonationRate;
    }

    public String getGood_NowCoin() {
        return Good_NowCoin;
    }

    public void setGood_NowCoin(String good_NowCoin) {
        Good_NowCoin = good_NowCoin;
    }

    public String getGood_StartCoin() {
        return Good_StartCoin;
    }

    public void setGood_StartCoin(String good_StartCoin) {
        Good_StartCoin = good_StartCoin;
    }

    public String getGood_Praise() {
        return Good_Praise;
    }

    public void setGood_Praise(String good_Praise) {
        Good_Praise = good_Praise;
    }

    public User_Profile getGood_NowBidUser() {
        return Good_NowBidUser;
    }

    public void setGood_NowBidUser(User_Profile good_NowBidUser) {
        Good_NowBidUser = good_NowBidUser;
    }

    public Long getGood_Five() {
        return Good_Five;
    }

    public void setGood_Five(Long good_Five) {
        Good_Five = good_Five;
    }

    public Integer getGood_Status() {
        return Good_Status;
    }

    public void setGood_Status(Integer good_Status) {
        Good_Status = good_Status;
    }

    public Boolean getGood_IsFirstBid() {
        return Good_IsFirstBid;
    }

    public void setGood_IsFirstBid(Boolean good_IsFirstBid) {
        Good_IsFirstBid = good_IsFirstBid;
    }

    public Bid getGood_Bid() {
        return Good_Bid;
    }

    public void setGood_Bid(Bid good_Bid) {
        Good_Bid = good_Bid;
    }
}