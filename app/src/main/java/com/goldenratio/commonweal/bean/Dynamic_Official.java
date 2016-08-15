package com.goldenratio.commonweal.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/14.
 * Email:jxfengmtx@163.com
 */
public class Dynamic_Official extends BmobObject {
    private String Dyc_Title;
    private String Dyc_Image;
    private String Dyc_Content;
    private List<String> Dyc_Pic;

    public String getDyc_Title() {
        return Dyc_Title;
    }

    public void setDyc_Title(String dyc_Title) {
        Dyc_Title = dyc_Title;
    }

    public String getDyc_Image() {
        return Dyc_Image;
    }

    public void setDyc_Image(String dyc_Image) {
        Dyc_Image = dyc_Image;
    }

    public String getDyc_Content() {
        return Dyc_Content;
    }

    public void setDyc_Content(String dyc_Content) {
        Dyc_Content = dyc_Content;
    }

    public List<String> getDyc_Pic() {
        return Dyc_Pic;
    }

    public void setDyc_Pic(List<String> dyc_Pic) {
        Dyc_Pic = dyc_Pic;
    }
}
