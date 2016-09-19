package com.goldenratio.commonweal.util;

import android.content.Context;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.onekeyshare.OnekeyShare;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Administrator on 2016/8/15.
 * 分享工具类
 */

public class ShareUtils {


    /**
     * @param mContext
     * @param title    title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
     * @param titleUrl titleUrl是标题的网络链接，仅在人人网和QQ空间使用
     * @param text     text是分享文本，所有平台都需要这个字段
     * @param url      url仅在微信（包括好友和朋友圈）中使用
     * @param comment  comment是我对这条分享的评论，仅在人人网和QQ空间使用
     */
    public static void showGUI(Context mContext, String title, String titleUrl, String text, String url, String comment) {
        MyApplication applicationContext = (MyApplication) mContext.getApplicationContext();
        String siteUrl = applicationContext.getSiteUrl();
        ShareSDK.initSDK(mContext);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        try {
            copyBigDataToSD(mContext);
            oks.setImagePath("/sdcard/commonweal/app_coin.png");//确保SDcard下面存在此张图片
        } catch (IOException e) {
            e.printStackTrace();
        }

        oks.setTitle(title);

        oks.setTitleUrl(titleUrl);
        oks.setText(text);
//        分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setUrl(url);
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        if (!(siteUrl == null)) {
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl("http://sharesdk.cn");
        }
        // 启动分享GUI
        oks.show(mContext);
    }


    private static void copyBigDataToSD(Context mContext) throws IOException {
        String fileName = "/commonweal/app_coin.png";
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(fileName);
        myInput = mContext.getAssets().open("yphone.zip");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
}
