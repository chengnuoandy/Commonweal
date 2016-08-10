package com.goldenratio.commonweal;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Kiuber on 2016/6/20.
 */

public class MyApplication extends Application {

    private boolean DynamicRefresh;

    private String ObjectID;   //bmob objectID

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils
        x.Ext.init(this);
    }

    public boolean isDynamicRefresh() {
        return DynamicRefresh;
    }

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public void setDynamicRefresh(boolean dynamicRefresh) {
        DynamicRefresh = dynamicRefresh;
    }
}