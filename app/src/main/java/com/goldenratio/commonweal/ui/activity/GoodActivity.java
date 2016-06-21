package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.PhotoGridViewAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class GoodActivity extends Activity implements View.OnClickListener {

    private LayoutInflater mLi;
    private ArrayList<String> mSelectPath;
    private static final int REQUEST_IMAGE = 2;
    private GridView mGvShowPhoto;

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
        DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.view_good_price))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        //
                    }
                })
                .create().show();
    }
}