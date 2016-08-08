package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodPicAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.U_FamousP;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

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
    private MyGoodPicAdapter mPicAdapter;
    private TextView mTvType;
    private List<String> pathList;
    private static final int GOOD_TYPE = 2;
    private Button mBtnRelease;
    private EditText mEtName, mEtDescription;
    private String mStrType = "";
    private String mStrTime = "";
    private String mStrObjectId;
    private Spinner mSrTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good);
        initView();
        imageSelectConfig();
//        UploadData(1, 1);
    }


    private void initView() {
        mLi = LayoutInflater.from(GoodActivity.this);
        mTvType = (TextView) findViewById(R.id.tv_type);
        mTvType.setOnClickListener(this);
        TVprice = (TextView) findViewById(R.id.tv_price);
        mGvShowPhoto = (GridView) findViewById(R.id.gv_show_photos);
        mLlAddPhoto = (LinearLayout) findViewById(R.id.ll_add_photo);
        mLlAddPhoto.setOnClickListener(this);
        TVprice.setOnClickListener(this);
        mBtnRelease = (Button) findViewById(R.id.btn_release);
        mBtnRelease.setOnClickListener(this);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtDescription = (EditText) findViewById(R.id.et_description);
        mSrTime = (Spinner) findViewById(R.id.sr_time);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type:
                Intent intent = new Intent(this, GoodTypeActivity.class);
                startActivityForResult(intent, GOOD_TYPE);
                break;
            case R.id.tv_price:
                showPriceView();
                break;
            case R.id.ll_add_photo:
                ImageSelector.open(GoodActivity.this, imageConfig);   // 开启图片选择器
                break;
            case R.id.btn_release:
                saveGoodInfo2Bmob();
                break;
            case R.id.iv_back:
                finish();
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
     *
     * @param day  时长-天
     * @param hour 时长-小时
     */
    private void UploadData(int day, int hour, int minute) {
        Good mGood = new Good();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long date = System.currentTimeMillis() + day * 24 * 60 * 60 * 1000 + hour * 60 * 60 * 1000 + minute * 60 * 1000;
        Date curDate = new Date(date);//转换时间
        String str = formatter.format(curDate);
        Log.d(TAG, "testDate: now time--->" + str);
        mGood.setGood_UpDate(BmobDate.createBmobDate("yyyy-MM-dd HH:mm:ss", str));
        mGood.setGood_UpDateM(date);
        mGood.setGood_ID(5);
        mGood.setGood_Name("张杰");
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
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    mStrType = data.getStringExtra("type");
                    mTvType.setText("类目：" + mStrType);
                }
                break;
        }
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
//        Toast.makeText(this, "" + parent.getId(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "" + view.getId(), Toast.LENGTH_SHORT).show();
//        pathList.remove(position);
//        mPicAdapter.notifyDataSetChanged();
//        if (pathList.size() == 0) {
//            mGvShowPhoto.setVisibility(View.GONE);
//        }
    }

    private void saveGoodInfo2Bmob() {

        long[] mLgTimes = {21600000, 32400000, 43200000, 64800000, 86400000};
        final String mStrName = mEtName.getText().toString();
        final String mStrDescription = mEtName.getText().toString();
        final long mLgTime = mLgTimes[mSrTime.getSelectedItemPosition()] + System.currentTimeMillis();
        if (TextUtils.isEmpty(mStrName) || TextUtils.isEmpty(mStrDescription)
                || TextUtils.isEmpty(pathList.toString())
                || TextUtils.isEmpty(mStrType) || TextUtils.isEmpty(price)
                || TextUtils.isEmpty(prop)) {
            Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
        } else {
            UserDao userDao = new UserDao(GoodActivity.this);
            Cursor cursor = userDao.query("select * from User_Profile");
            while (cursor.moveToNext()) {
                int nameColumnIndex = cursor.getColumnIndex("objectId");
                mStrObjectId = cursor.getString(nameColumnIndex);
            }
            cursor.close();
            final String[] filePaths = new String[pathList.size()];
            for (int i = 0; i < pathList.size(); i++) {
                filePaths[i] = pathList.get(i).toString();
            }

            doInBackground(filePaths, mStrName, mStrDescription, mLgTime);
            finish();
        }
    }

    private void doInBackground(final String[] filePaths, final String mStrName, final String mStrDescription, final long mLgTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobFile.uploadBatch(GoodActivity.this, filePaths, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> list, List<String> list1) {
                        if (filePaths.length == list1.size()) {
                            U_FamousP u_famousP = new U_FamousP();
                            u_famousP.setObjectId(mStrObjectId);
                            Good good = new Good();
                            Toast.makeText(GoodActivity.this, mStrObjectId, Toast.LENGTH_SHORT).show();
                            good.setGood_User_ID(u_famousP);
                            good.setGood_Name(mStrName);
                            good.setGood_Description(mStrDescription);
                            good.setGood_Photos(list1);
                            good.setGood_Type(mStrType);
                            good.setGood_Price(Integer.parseInt(price));
                            good.setGood_NowPrice(Integer.parseInt(price));
                            good.setGood_DonationRate(Integer.parseInt(prop));
                            good.setGood_UpDateM(mLgTime);
                            good.save(GoodActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(500);
                                    Toast.makeText(GoodActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(GoodActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {

                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(GoodActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}