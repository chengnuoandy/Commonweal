package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodPicAdapter;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.bean.U_FamousP;
import com.goldenratio.commonweal.bean.U_NormalP;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class DynamicReleaseActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mIvBack;
    private LinearLayout mLlAddPhoto;
    private TextView mTvLocation;
    private Button mBtnRelease;
    private EditText mEtText;
    private List<String> pathList;
    private GridView mGvShowPhoto;
    private MyGoodPicAdapter mPicAdapter;
    private ImageConfig mImageConfig;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_release);

        initView();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mLlAddPhoto = (LinearLayout) findViewById(R.id.ll_add_photo);
        mTvLocation = (TextView) findViewById(R.id.tv_location);
        mBtnRelease = (Button) findViewById(R.id.btn_release);
        mEtText = (EditText) findViewById(R.id.et_text);
        mGvShowPhoto = (GridView) findViewById(R.id.gv_show_photos);

        mIvBack.setOnClickListener(this);
        mLlAddPhoto.setOnClickListener(this);
        mTvLocation.setOnClickListener(this);
        mBtnRelease.setOnClickListener(this);

        //初始化图片选择器
        imageSelectConfig();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_add_photo:
                ImageSelector.open(this, mImageConfig);   // 开启图片选择器
                break;
            case R.id.tv_location:
                Intent intent = new Intent(this, DynamicLocationActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_release:
                UploadData();
                SendM();
                break;
        }
    }

    /**
     * 上传数据
     */
    private void UploadData() {
        if (TextUtils.isEmpty(mEtText.getText().toString())) {
            Toast.makeText(DynamicReleaseActivity.this, "请填写内容后再提交！", Toast.LENGTH_SHORT).show();
        } else {
            Dynamic dy = new Dynamic();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            dy.setDynamics_title(mEtText.getText().toString());
            dy.setDynamics_time(str);
            if (!mTvLocation.getText().toString().equals("地点(不添加)")) {
                dy.setDynamics_location(mTvLocation.getText().toString());
            }
            getUserData(dy);
            if (pathList != null){
                final String[] filePaths = new String[pathList.size()];
                for (int i = 0; i < pathList.size(); i++) {
                    filePaths[i] = pathList.get(i);
                }
                save2Bmob(filePaths,dy);
            }else {
                SaveData(dy);
            }

        }
    }

    private void save2Bmob(final String[] filePaths, final Dynamic dy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobFile.uploadBatch(DynamicReleaseActivity.this, filePaths, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> list, List<String> list1) {
                        if (filePaths.length == list1.size()){
                            dy.setDynamics_pic(list1);
                            Log.d("lxc", "onSuccess: "+list1);
                            SaveData(dy);
                        }
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        }).start();
    }

    /**
     * 上传文本数据
     * @param dy 数据实体
     */
    private void SaveData(Dynamic dy) {
        dy.save(DynamicReleaseActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(DynamicReleaseActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
                Completed();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 获取用户数据
     *
     * @param dy 保存的实体
     */
    private void getUserData(Dynamic dy) {
        String sqlCmd = "SELECT * FROM User ";
        UserDao ud = new UserDao(this);
        Cursor cursor = ud.query(sqlCmd);
        if (cursor.moveToFirst()) {
//            判断用户的身份，进行关联
            if (false){
                U_FamousP user = new U_FamousP();
                user.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
                dy.setDynamics_u_f_id(user);
                dy.setDynamics_isv(true);
            }else {
                U_NormalP user = new U_NormalP();
                user.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
                dy.setDynamics_uid(user);
                dy.setDynamics_isv(false);
            }
        }
        cursor.close();
    }

    /**
     * 初始化图片选择器
     */
    private void imageSelectConfig() {
        mImageConfig = new ImageConfig.Builder(new GlideLoader())
                .steepToolBarColor(getResources().getColor(R.color.black))
                .titleBgColor(getResources().getColor(R.color.white))
                .titleSubmitTextColor(getResources().getColor(R.color.colorPrimary))
                .titleTextColor(getResources().getColor(R.color.black))
                // 开启多选   （默认为多选）
                .mutiSelect()
                // 多选时的最大数量   （默认 9 张）
                .mutiSelectMaxSize(8)
                // 开启拍照功能 （默认关闭）
                .showCamera()
                // 已选择的图片路径
                // .pathList(path)
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
                .filePath("/ImageSelector/Pictures")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get Image Path List
            pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            mGvShowPhoto.setVisibility(View.VISIBLE);
            mPicAdapter = new MyGoodPicAdapter(this, pathList, mGvShowPhoto);
            mGvShowPhoto.setAdapter(mPicAdapter);
            mGvShowPhoto.setOnItemClickListener(this);
        }
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    mTvLocation.setText(data.getStringExtra(DynamicLocationActivity.KEY_S) + "-" + data.getStringExtra(DynamicLocationActivity.KEY_T));
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 进度条相关--显示进度条
     */
    private void SendM() {
            pd = new ProgressDialog(this);
            pd.setTitle("发送中...");
            pd.setCancelable(false);
            pd.show();
    }
    /**
     * 进度条相关--取消进度条显示
     */
    private void Completed() {
        if (pd != null && pd.isShowing()) {
            //关闭对话框
            pd.dismiss();
            pd = null;
        }
    }
}
