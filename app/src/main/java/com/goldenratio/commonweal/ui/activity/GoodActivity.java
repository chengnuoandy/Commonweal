package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.PhotoGridViewAdapter;
import com.goldenratio.commonweal.ui.lib.GoodEditText;
import com.goldenratio.commonweal.ui.lib.GoodKeypad;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class GoodActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "lxc";
    private LayoutInflater mLi;
    private ArrayList<String> mSelectPath;
    private static final int REQUEST_IMAGE = 2;
    private GridView mGvShowPhoto;
    private DialogPlus mDialogPlus;

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
        mDialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.view_good_price))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {

                        GoodEditText GET1 = ((GoodEditText) findViewById(R.id.release1));
                        GoodEditText GET2 = ((GoodEditText) findViewById(R.id.release2));
                       // GET1.requestFocus();

                        View.OnFocusChangeListener pubET = new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                switch (v.getId()) {
                                    case R.id.release1:
                                        if (hasFocus)
                                            new GoodKeypad(getWindow().getDecorView(), ((GoodEditText) findViewById(R.id.release1)));
                                        break;
                                    case R.id.release2:
                                        if (hasFocus)
                                            new GoodKeypad(getWindow().getDecorView(), ((GoodEditText) findViewById(R.id.release2)));
                                        break;
                                }
                            }
                        };
                        GET1.setOnFocusChangeListener(pubET);
                        GET2.setOnFocusChangeListener(pubET);
                        GET1.setInputType(InputType.TYPE_NULL);
                        GET2.setInputType(InputType.TYPE_NULL);

                    }
                })
                .create();
        mDialogPlus.show();

    }

}