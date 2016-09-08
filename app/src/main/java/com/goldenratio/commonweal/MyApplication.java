package com.goldenratio.commonweal;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.x;

import java.util.List;

import c.b.BP;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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
    private String WbId;    //微博ID
    private String WbVerReason;    //微博认证理由

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils
        x.Ext.init(this);
        initBmob();

// TODO: 2016/9/4 微博认证
        if (isUserTableExist()) {
            getUserData();
//            getWbIdFromBmob();
        } else ObjectID = "";

        //初始化push推送服务
        if (shouldInit()) {
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

    public String getWbId() {
        return WbId;
    }

    public void setWbId(String wbId) {
        WbId = wbId;
    }

    public String getWbVerReason() {
        return WbVerReason;
    }

    public void setWbVerReason(String wbVerReason) {
        WbVerReason = wbVerReason;
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

    private void getUserData() {
        String sqlCmd = "SELECT objectId FROM User_Profile ";
        UserDao ud = new UserDao(this);
        Cursor cursor = ud.query(sqlCmd);
        if (cursor.moveToFirst()) {
            ObjectID = cursor.getString(cursor.getColumnIndex("objectId"));
        }
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


   private void initBmob(){
        String libName = "bmob"; // 库名, 注意没有前缀lib和后缀.so
        System.loadLibrary(libName);

        //初始化Bmob
        Bmob.initialize(this, "727a409235aab18ae7b1e1f3933c9a64");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save1();
        // 启动推送服务
        BmobPush.startWork(this);
        BP.init(this, "727a409235aab18ae7b1e1f3933c9a64");
    }

    private void getWbIdFromBmob() {
        BmobQuery<User_Profile> user_profileBmobQuery = new BmobQuery<>();
        user_profileBmobQuery.addWhereEqualTo("objectId", ObjectID);
        user_profileBmobQuery.addQueryKeys("User_WbID,User_VerifiedReason");
        user_profileBmobQuery.findObjects(new FindListener<User_Profile>() {
            @Override
            public void done(List<User_Profile> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 1) {
                        WbId = list.get(0).getUser_WbID();
                        WbVerReason = list.get(0).getUser_VerifiedReason();
                    } else {
                        Toast.makeText(MyApplication.this, "账号信息错误，请联系开发团队", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }
}