package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodPicAdapter;
import com.goldenratio.commonweal.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.util.List;

public class DynamicReleaseActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mIvBack;
    private LinearLayout mLlAddPhoto;
    private TextView mTvLocation;
    private Button mBtnRelease;
    private List<String> pathList;
    private GridView mGvShowPhoto;
    private MyGoodPicAdapter mPicAdapter;
    private ImageConfig mImageConfig;


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
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_add_photo:
                ImageSelector.open(this, mImageConfig);   // 开启图片选择器
                break;
            case R.id.tv_location:
                Intent intent = new Intent(this,DynamicLocationActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.btn_release:
                break;
        }
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
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    mTvLocation.setText(data.getStringExtra(DynamicLocationActivity.KEY_S)+"-"+data.getStringExtra(DynamicLocationActivity.KEY_T));
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
