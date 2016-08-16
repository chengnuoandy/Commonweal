package com.goldenratio.commonweal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.bmob.push.PushConstants;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/16.
 * Email:jxfengmtx@163.com
 */
public class PushMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){

            Log.d("pushcontent", "客户端收到推送内容："+intent.getStringExtra("msg"));
        }
    }
}
