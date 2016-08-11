package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.ui.fragment.MyFragment;
import com.lljjcoder.citypickerview.widget.CityPickerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 龙啸天 on 2016/7/20 0020.
 * <p/>
 * 编辑收货地址
 */

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


    private ArrayList<String> address;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.bind(this);

        address = getIntent().getStringArrayListExtra("address");

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
                showProgressDialog();
                updateDataToBmob();
                break;
        }
    }

    private void rtnDataToSetAddressAct() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("address", address);
        setResult(RESULT_OK, intent);
        //address.clear();
        closeProgressDialog();
        finish();
    }

    private void configCityPickerView() {
        CityPickerView cityPickerView = new CityPickerView(EditAddressActivity.this);
        cityPickerView.setTextColor(Color.BLACK);
        cityPickerView.setTextSize(20);
        cityPickerView.setVisibleItems(5);  //新增滚轮内容可见数量
        cityPickerView.setIsCyclic(false);   //滚轮是否循环滚动
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

                String addreDetail = province + " " + city + " " + district + " ";
                mTvEditAddress.setText(addreDetail);
            }
        });
    }

    private void updateDataToBmob() {
        String consignee = mEtConsignee.getText().toString();
        String consigneePhone = mEtEditPhone.getText().toString();
        String consigneeAddress = mTvEditAddress.getText() + " "
                + mEtDetailAddress.getText();

        address.add(consignee);
        address.add(consigneePhone);
        address.add(consigneeAddress);

        String userID = MyFragment.mUserID;
        User_Profile u = new User_Profile();
        Log.i("list", "updateDataToBmob: " + address);
        u.setUser_Receive_Address(address);
        u.update(userID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    rtnDataToSetAddressAct();
                } else {
                    closeProgressDialog();
                    Log.i("why", e.getMessage());
                    Toast.makeText(EditAddressActivity.this, "修改失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void showProgressDialog() {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setMessage("保存中");
            mPd.setCancelable(true);
            mPd.show();
        }
    }

    private void closeProgressDialog() {
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
            mPd = null;
        }
    }
}
