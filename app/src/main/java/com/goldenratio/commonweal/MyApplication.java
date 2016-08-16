package com.goldenratio.commonweal;

import android.app.Application;
import android.database.Cursor;

import com.goldenratio.commonweal.dao.UserDao;

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

    /**
     * 判断本地用户表是否存在
     *
     * @return
     */
    private boolean isUserTableExist() {
        boolean isTableExist = true;
        String sqlCmd = "SELECT count(User_Avatar) FROM User_Profile ";
        UserDao ud = new UserDao(this);
        Cursor c = ud.query(sqlCmd);
        if (c.moveToNext()) {
            if (c.getInt(0) == 0) {
                isTableExist = false;
            }
        }
        c.close();
        return isTableExist;
    }
}