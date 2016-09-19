package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/11.
 */

public class AppConfig extends BmobObject {
    private String WebServiceIp;
    private String SiteUrl;

    public String getWebServiceIp() {
        return WebServiceIp;
    }

    public void setWebServiceIp(String webServiceIp) {
        WebServiceIp = webServiceIp;
    }

    public String getSiteUrl() {
        return SiteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        SiteUrl = siteUrl;
    }
}
