package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.goldenratio.commonweal.util.ShareUtils;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;


/**
 * 作者：Created by 龙啸天 on 2016/6/27 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 * <p/>
 * 整个app的设置---
 */
public class MySetActivity extends BaseActivity {
    @BindView(R.id.tv_exit)
    TextView mTvExit;
    @BindView(R.id.cache_size)
    TextView mCacheSize;
    private boolean isLogin = false;

    private String cachePath;

    private TextView mTvNowVer;
    private TextView mTvShare;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "无缓存", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mCacheSize.setText("0B");
                    Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_set);
        ButterKnife.bind(this);

        isLogin = getIntent().getExtras().getBoolean("islogin");

        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            File skRoot = Environment.getExternalStorageDirectory();
            cachePath = skRoot.getPath() + "/GR_commonweal/avatarCache";
            String fileSize = getFileOrFilesSize(cachePath);
            mCacheSize.setText(String.valueOf(fileSize));

        }

        mTvNowVer = (TextView) findViewById(R.id.tv_now_ver);
        mTvShare = (TextView) findViewById(R.id.tv_share);
        mTvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://123.206.89.67/loadpage/index.html";
                ShareUtils shareUtils = new ShareUtils();
                shareUtils.showShare(MySetActivity.this, url, "爱点App", "内容", "评论");
            }
        });
        try {
            mTvNowVer.setText("当前版本：v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new ImmersiveUtil(this, R.color.white, true);
    }


    /**
     * 删除本地user表
     */
    private void deleteTable() {
        String sqlCmd = "DELETE FROM User_Profile";
        UserDao ud = new UserDao(this);
        ud.execSQL(sqlCmd);
    }


    /**
     * 注解 按钮点击事件
     *
     * @param view
     */
    @OnClick({R.id.tv_exit, R.id.iv_back, R.id.tv_feedback, R.id.tv_about, R.id.tv_update, R.id.clear_cache})
    public void onClick(View view) {
        switch (view.getId()) {
            //退出登陆
            case R.id.tv_exit:
                if (!isLogin) {
                    Toast.makeText(MySetActivity.this, "您尚未登陆", Toast.LENGTH_SHORT).show();
                } else {
                    deleteTable();
                    ((MyApplication) getApplication()).setObjectID("");
                    setResult(RESULT_OK, null);
                    finish();
                }
                break;
            case R.id.clear_cache:
                File file = new File(cachePath);
                DeleteFile(file);
                break;
            //返回主界面
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_feedback:
                Intent intent = new Intent(this, UserFeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_about:
                showAbout();
                break;
            case R.id.tv_update:
                updateApp();
        }
    }


    private String getFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return 文件大小
     * @throws Exception
     */
    private long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param
     * @return
     * @throws Exception
     */
    private long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * @param fileS
     * @return
     */
    private String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void DeleteFile(File file) {
        if (file.exists() == false) {
            mHandler.sendEmptyMessage(0);
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
        mHandler.sendEmptyMessage(1);
    }

    private void updateApp() {
//        BmobUpdateAgent.initAppVersion();
        Toast.makeText(MySetActivity.this, "正在检测更新...", Toast.LENGTH_SHORT).show();
        BmobUpdateAgent.forceUpdate(MySetActivity.this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                if (updateStatus == UpdateStatus.Yes) {//版本有更新
                    Toast.makeText(MySetActivity.this, "发现新版本！", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.No) {
                    Toast.makeText(MySetActivity.this, "版本无更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.TimeOut) {
                    Toast.makeText(MySetActivity.this, "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                }
                /*else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                    Toast.makeText(MySetActivity.this, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                }else if(updateStatus==UpdateStatus.IGNORED){
                    Toast.makeText(MySetActivity.this, "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                    Toast.makeText(MySetActivity.this, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("关于");
        try {
            builder.setMessage("XXX\n" +
                    "版本：" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + "\n" +
                    "这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍" +
                    "这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍" +
                    "这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍" +
                    "这里写详细介绍这里写详细介绍这里写详细介绍这里写详细介绍");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        builder.setPositiveButton("确定", null);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
