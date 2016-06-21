package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.goldenratio.commonweal.R;

import java.util.ArrayList;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class GoodActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "lxc";
    private LayoutInflater mLi;
    private ArrayList<String> mSelectPath;
    private static final int REQUEST_IMAGE = 2;
    private GridView mGvShowPhoto;

    private String price;
    private String prop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good);
        initView();
    }

    private void initView() {
        mLi = LayoutInflater.from(GoodActivity.this);
        findViewById(R.id.tv_type).setOnClickListener(this);
        findViewById(R.id.tv_price).setOnClickListener(this);
        mGvShowPhoto = (GridView) findViewById(R.id.gv_show_photos);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type:
                break;
            case R.id.tv_price:
                showPriceView();
                break;
        }
    }

    private void showTypeView() {
    }

    private void showPriceView() {
        Intent mIntent = new Intent(this,GoodKeypadActivity.class);
        mIntent.putExtra("price",price);
        mIntent.putExtra("prop",prop);
        startActivityForResult(mIntent,1);
    }

    /**
     * activity回调函数
     */
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
                    Log.d(TAG, "onActivityResult: price="+data.getStringExtra("price")+"prop="+data.getStringExtra("prop"));
                }
        }
    }
}