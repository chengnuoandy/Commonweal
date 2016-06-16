package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.goldenratio.commonweal.R;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class GoodActivity extends Activity implements View.OnClickListener {

    private LayoutInflater mLi;

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
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        Toast.makeText(GoodActivity.this, width + "", Toast.LENGTH_SHORT).show();
        View view = mLi.inflate(R.layout.view_good_price, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(GoodActivity.this);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = width;
        Window window = dialog.getWindow();
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setView(view);
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }
}