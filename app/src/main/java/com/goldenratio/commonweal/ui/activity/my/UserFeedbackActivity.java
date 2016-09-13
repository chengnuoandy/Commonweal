package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodPicAdapter;
import com.goldenratio.commonweal.bean.UserFeedback;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class UserFeedbackActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ImageConfig imageConfig;
    private ArrayList<String> pathList;
    private MyGoodPicAdapter mPicAdapter;
    private ProgressDialog pd;

    @BindView(R.id.et_contacts)
    EditText mEtContacts;
    @BindView(R.id.et_feedback)
    EditText mEtFeedback;
    @BindView(R.id.gv_show_photos)
    GridView mGvShowPhoto;
    @BindView(R.id.iv_set_back)
    ImageView mIvSetBack;
    @BindView(R.id.btn_send)
    Button mBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);

        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        imageConfig = new ImageConfig.Builder(new GlideLoader())
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


    @OnClick({R.id.iv_set_back, R.id.ll_add_photo, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_set_back:
                finish();
                break;
            case R.id.ll_add_photo:
                ImageSelector.open(this, imageConfig);   // 开启图片选择器
                break;
            case R.id.btn_send:
                if (mEtFeedback.getText().toString().isEmpty()){
                    Toast.makeText(UserFeedbackActivity.this, "请填写反馈内容", Toast.LENGTH_SHORT).show();
                }else {
                    SendM();
                    up2Bmob();
                }
                break;
        }
    }

    private void up2Bmob() {
        UserFeedback userFeedback = new UserFeedback();
        userFeedback.setContacts(mEtContacts.getText().toString());
        userFeedback.setContent(mEtFeedback.getText().toString());
        if (pathList != null) {
            final String[] filePaths = new String[pathList.size()];
            for (int i = 0; i < pathList.size(); i++) {
                filePaths[i] = pathList.get(i);
            }
            saveFile2Bmob(filePaths, userFeedback);
        } else {
            userFeedback.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {

                    if (e == null) {
                        Toast.makeText(UserFeedbackActivity.this, "发送成功！", Toast.LENGTH_SHORT).show();
                        Completed();
                        finish();
                    } else {
                        Completed();
//                        Toast.makeText(UserFeedbackActivity.this, "未知错误" + e, Toast.LENGTH_SHORT).show();
                        ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                    }
                }
            });
        }
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void saveFile2Bmob(final String[] filePaths, final UserFeedback uf) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> list, List<String> list1) {
                        if (filePaths.length == list1.size()) {
                            uf.setPic(list1);
                            uf.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null){
                                        Completed();
                                        Toast.makeText(UserFeedbackActivity.this, "发送成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Completed();
//                                        Toast.makeText(UserFeedbackActivity.this, "未知错误-" + e, Toast.LENGTH_SHORT).show();
                                        ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {

                    }

                    @Override
                    public void onError(int i, String s) {
                        Completed();
                    }
                });
            }
        }).start();
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
