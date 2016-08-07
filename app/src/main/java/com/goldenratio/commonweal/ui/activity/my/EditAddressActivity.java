package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAddressActivity extends Activity {

    @BindView(R.id.et_edit_consignee)
    EditText mEtEditConsignee;
    @BindView(R.id.et_edit_phone)
    EditText mEtEditPhone;
    @BindView(R.id.tv_place_address)
    TextView mTvPlaceAddress;
    @BindView(R.id.tv_edit_address)
    TextView mTvEditAddress;
    @BindView(R.id.rl_edit_address)
    RelativeLayout mRlEditAddress;
    @BindView(R.id.tv_edit_street)
    TextView mTvEditStreet;
    @BindView(R.id.rl_place_street)
    RelativeLayout mRlPlaceStreet;
    @BindView(R.id.et_detail_address)
    EditText mEtDetailAddress;
    @BindView(R.id.btn_save_address)
    Button mBtnSaveAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.iv_edit_address_back,  R.id.rl_edit_address, R.id.tv_edit_street, R.id.rl_place_street, R.id.et_detail_address, R.id.btn_save_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit_address_back:
                finish();
                break;
            case R.id.rl_edit_address:
                break;
            case R.id.tv_edit_street:
                break;
            case R.id.rl_place_street:
                break;
            case R.id.et_detail_address:
                break;
            case R.id.btn_save_address:
                break;
        }
    }
}
