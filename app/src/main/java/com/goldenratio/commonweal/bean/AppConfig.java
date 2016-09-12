package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/11.
 */

public class AppConfig extends BmobObject {
    private String WebServiceIp;

    public String getWebServiceIp() {
        return WebServiceIp;
    }

    public void setWebServiceIp(String webServiceIp) {
        WebServiceIp = webServiceIp;
    }
}
