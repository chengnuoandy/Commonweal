package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/6/11.
 *
 */

public class GoodActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.iv_return)
    ImageView mIvReturn;
    @BindView(R.id.title_text_tv)
    TextView mTitleTextTv;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.btn_release)
    Button mBtnRelease;
    private Button mBtnPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good);
        ButterKnife.bind(this);
        setOnClickListener();
    }

    private void setOnClickListener() {
        mTvType.setOnClickListener(this);
        mTvPrice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type:
                break;
            case R.id.tv_price:
                break;
        }
    }
}