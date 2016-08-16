package com.goldenratio.commonweal;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.Process;
import android.util.Log;

import com.goldenratio.commonweal.dao.UserDao;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.x;

import java.util.List;

/**
 * Created by Kiuber on 2016/6/20.
 */

public class MyApplication extends Application {

//             MiPushClient.getRegId(this)

    public static final String TAG = "lxccs";

    private boolean DynamicRefresh;

    public static final String APP_ID = "2882303761517501484";
    public static final String APP_KEY = "5521750163484";

    private String ObjectID;   //bmob objectID

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils
        x.Ext.init(this);

        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        /*Logcat调试记录  小米推送
        SDCard/Android/data/apppkgname/files/MiPushLog目录下的文件。
        如果app需要关闭写日志文件功能（不建议关闭），
        只需要调用Logger.disablePushFileLog(context)即可。*/
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }
            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
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

    /**
     * 小米推送初始化
     */
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}