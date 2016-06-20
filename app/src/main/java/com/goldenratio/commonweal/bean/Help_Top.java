package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 两个人 on 2016-06-20.
 */
public class Help_Top extends BmobObject {

    private String Title;
    private String Help_Top_Pic;

    public void setTitle(String title){
        Title=title;

    }
    public String getTitle(){return Title;}
    public void setHelp_Top_Pic(String pic){
        Help_Top_Pic=pic;

    }
    public String getHelp_Top_Pic(){return Title;}

}
