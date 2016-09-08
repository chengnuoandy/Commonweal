package com.goldenratio.commonweal.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/9/1.
 */

public class ImmersiveUtil {

    private SystemBarTintManagerUtil mTintManager;

    public ImmersiveUtil(Activity activity, int color, boolean mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
        }
        mTintManager = new SystemBarTintManagerUtil(activity);
        if (mode) {
            //设置状态栏背景色颜色
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setStatusBarTintResource(color);
        } else {
            mTintManager.setStatusBarTintEnabled(false);
        }
        Toast.makeText(activity, mode + "", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on, Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
