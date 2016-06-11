package com.goldenratio.commonweal.util;

import android.content.Context;

/**
 * Created by Administrator on 2016/6/11.
 */

public class SharedUtils {

    private static String FILE_NAME = "AppConfig";

    /**
     * 将String类型键值对保存到AppConfig文件
     * @param context
     * @param key
     * @param value
     */
    public static void putKeyValueString(Context context, String key, String value) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    /**
     * 获取AppConfig文件内String类型某键对应的值
     * @param context
     * @param key
     * @return
     */
    public static String getKeyValueString(Context context, String key) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(key, "");
    }
}
