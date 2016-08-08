package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Address;
import com.lljjcoder.citypickerview.widget.CityPickerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAddressActivity extends Activity {

    @BindView(R.id.et_edit_consignee)
    EditText mEtConsignee;
    @BindView(R.id.et_edit_phone)
    EditText mEtEditPhone;
    @BindView(R.id.tv_place_address)
    TextView mTvPlaceAddress;
    @BindView(R.id.tv_edit_address)
    TextView mTvEditAddress;
    @BindView(R.id.rl_edit_address)
    RelativeLayout mRlEditAddress;
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

    @OnClick({R.id.iv_edit_address_back, R.id.rl_edit_address, R.id.et_detail_address, R.id.btn_save_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit_address_back:
                finish();
                break;
            case R.id.rl_edit_address:
                configCityPickerView();
                break;
            case R.id.et_detail_address:
                break;
            case R.id.btn_save_address:
                rtnDataToSetAddressAct();
                finish();
                break;
        }
    }

    private void rtnDataToSetAddressAct() {
        String consignee = mEtConsignee.getText().toString();
        String consigneePhone = mEtEditPhone.getText().toString();
        String consigneeAddress = mTvEditAddress.getText() + " "
                + mEtDetailAddress.getText();
        Address address = new Address();
        address.setConsignee(consignee);
        address.setConsigneePhone(consigneePhone);
        address.setConsigneeAddress(consigneeAddress);
        Intent intent = new Intent();
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
    }

    private void configCityPickerView() {
        CityPickerView cityPickerView = new CityPickerView(EditAddressActivity.this);
        cityPickerView.setTextColor(Color.BLUE);
        cityPickerView.setTextSize(20);
        cityPickerView.setVisibleItems(8);  //新增滚轮内容可见数量
        cityPickerView.setIsCyclic(true);   //滚轮是否循环滚动
        cityPickerView.show();
        cityPickerView.setOnCityItemClickListener(new CityPickerView.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];

                String addre = province + " " + city + " " + district + " ";
                mTvEditAddress.setText(addre);
            }
        });
    }
}
