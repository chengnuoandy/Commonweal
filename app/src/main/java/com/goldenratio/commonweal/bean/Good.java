package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * good information table
 * Created by Kiuber on 2016/6/10.
 */

public class Good extends BmobObject {
    private BmobDate Goods_UpDate; //截至日期
    private Long Goods_UpDateM; //截止日期 毫秒值
    private String Goods_NO; //物品发布顺序
    private String Goods_ID; //物品ID（Good+用户ID+随机码）
    private String Goods_Type; //物品类型
    private String Goods_Name; //物品名称
    private String Goods_Video; //物品介绍视频
    private String Goods_Photos; //物品介绍照片
    private String Goods_Description; //物品描述
    private int Goods_Donation_Rate; //物品用于捐款的比例
    private int Goods_Price; //物品的起步价


    public String getGoods_Description() {
        return Goods_Description;
    }

    public void setGoods_Description(String goods_Description) {
        Goods_Description = goods_Description;
    }

    public int getGoods_Donation_Rate() {
        return Goods_Donation_Rate;
    }

    public void setGoods_Donation_Rate(int goods_Donation_Rate) {
        Goods_Donation_Rate = goods_Donation_Rate;
    }

    public String getGoods_ID() {
        return Goods_ID;
    }

    public void setGoods_ID(String goods_ID) {
        Goods_ID = goods_ID;
    }

    public String getGoods_Name() {
        return Goods_Name;
    }

    public void setGoods_Name(String goods_Name) {
        Goods_Name = goods_Name;
    }

    public String getGoods_NO() {
        return Goods_NO;
    }

    public void setGoods_NO(String goods_NO) {
        Goods_NO = goods_NO;
    }

    public String getGoods_Photos() {
        return Goods_Photos;
    }

    public void setGoods_Photos(String goods_Photos) {
        Goods_Photos = goods_Photos;
    }

    public int getGoods_Price() {
        return Goods_Price;
    }

    public void setGoods_Price(int goods_Price) {
        Goods_Price = goods_Price;
    }

    public String getGoods_Type() {
        return Goods_Type;
    }

    public void setGoods_Type(String goods_Type) {
        Goods_Type = goods_Type;
    }

    public BmobDate getGoods_UpDate() {
        return Goods_UpDate;
    }

    public void setGoods_UpDate(BmobDate goods_UpDate) {
        Goods_UpDate = goods_UpDate;
    }

    public Long getGoods_UpDateM() {
        return Goods_UpDateM;
    }

    public void setGoods_UpDateM(Long goods_UpDateM) {
        Goods_UpDateM = goods_UpDateM;
    }

    public String getGoods_Video() {
        return Goods_Video;
    }

    public void setGoods_Video(String goods_Video) {
        Goods_Video = goods_Video;
    }
}
