package com.goldenratio.commonweal.receiver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.activity.MainActivity;
import com.goldenratio.commonweal.ui.activity.my.MessageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/16.
 * Email:jxfengmtx@163.com
 */
public class PushMessageReceiver extends BroadcastReceiver {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
       /* d------MessageActivity-- Dynamic
        p------MessageActivity--Privite
        w------WebView1


        p-Title-Data*/
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            String jsonPushData = intent.getStringExtra("msg");
            JSONObject jsonObject = null;
            String pushData = null;
            try {
                jsonObject = new JSONObject(jsonPushData);
                pushData = jsonObject.getString("alert");
            } catch (JSONException e) {
                Log.i("JSONException", "推送异常: " + e);
            }
            assert pushData != null;
            String[] sPushData = pushData.split("-");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(context, (sPushData[0].equals("p") || sPushData[0].equals("d")) ?
                    MessageActivity.class : MainActivity.class);
            intent1.putExtra("dyc", sPushData[0]);
            Log.i("拆分后的推送数据", "onReceive: " + sPushData + sPushData.length);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            Notification notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(sPushData[1])
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pi)
                    .setContentText(sPushData[2])
                    .build();
            notificationManager.notify(1, notification);
            Log.d("pushcontent", "客户端收到推送内容：" + intent.getStringExtra("msg"));
        }
    }
}
