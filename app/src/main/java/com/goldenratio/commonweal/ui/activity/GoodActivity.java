package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.PhotoGridViewAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class GoodActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "lxc";
    private LayoutInflater mLi;
    private ArrayList<String> mSelectPath;
    private static final int REQUEST_IMAGE = 2;
    private GridView mGvShowPhoto;
    private TextView TVprice;

    private String price;
    private String prop;

    private LinearLayout mLlAddPhoto;
    private ImageConfig imageConfig;
    private PhotoGridViewAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good);
        initView();
        imageSelectConfig();
        UploadData(1,1);
    }


    private void initView() {
        mLi = LayoutInflater.from(GoodActivity.this);
        findViewById(R.id.tv_type).setOnClickListener(this);
        TVprice = (TextView) findViewById(R.id.tv_price);
        mGvShowPhoto = (GridView) findViewById(R.id.gv_show_photos);
        mLlAddPhoto = (LinearLayout) findViewById(R.id.ll_add_photo);
        mLlAddPhoto.setOnClickListener(this);
        TVprice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type:
                break;
            case R.id.tv_price:
                showPriceView();
                break;
            case R.id.ll_add_photo:
                ImageSelector.open(GoodActivity.this, imageConfig);   // 开启图片选择器
                break;
        }
    }

    private void showTypeView() {
    }

    private void showPriceView() {
        Intent mIntent = new Intent(this, GoodKeypadActivity.class);
        mIntent.putExtra("price", price);
        mIntent.putExtra("prop", prop);
        startActivityForResult(mIntent, 1);
    }

    /**
     * 把拍卖信息上传至服务器
     * 包含 拍卖截至时间、
     * @param day 时长-天
     * @param hour 时长-小时
     */
    private void UploadData(int day,int hour) {
        Good mGood = new Good();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long date = System.currentTimeMillis() + day * 24 * 60 * 60 * 1000 + hour * 60 * 60 * 1000;
        Date curDate = new Date(date);//转换时间
        String str = formatter.format(curDate);
        Log.d(TAG, "testDate: now time--->"+str);
        mGood.setGoods_UpDate(BmobDate.createBmobDate("yyyy-MM-dd HH:mm:ss",str));
        mGood.setGoods_UpDateM(date);
        mGood.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(GoodActivity.this, "上传数据成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(GoodActivity.this, "上传数据失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imageSelectConfig() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //发布--->价格 捐款相关
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    //price 价格
                    //prop 捐款比例
                    price = data.getStringExtra("price");
                    prop = data.getStringExtra("prop");
                    if (!prop.equals("") && !price.equals(""))
                        TVprice.setText("已设置 底价：" + price + "元 捐款比例：" + prop + "%");

                    Log.d(TAG, "onActivityResult: price=" + data.getStringExtra("price") + "prop=" + data.getStringExtra("prop"));
                }
                break;
        }
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            mGvShowPhoto.setVisibility(View.VISIBLE);
            mPhotoAdapter = new PhotoGridViewAdapter(this, pathList);
            mGvShowPhoto.setAdapter(mPhotoAdapter);
            mGvShowPhoto.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        ImageView mDeletePhoto = (ImageView) view.findViewById(R.id.iv_delete_selected_photo);
        mDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GoodActivity.this, "delete", Toast.LENGTH_SHORT).show();
                mGvShowPhoto.removeViewAt(position);
                mPhotoAdapter.notifyDataSetChanged();
            }
        });
    }
}