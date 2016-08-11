package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.SetAddressListAdapter;
import com.goldenratio.commonweal.bean.User_Profile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class SetAddressActivity extends Activity implements SetAddressListAdapter.Callback, AdapterView.OnItemClickListener {

    @BindView(R.id.iv_address_back)
    ImageView mIvBack;
    @BindView(R.id.lv_address_details)
    ListView mLvAddressDetails;
    @BindView(R.id.btn_add_address)
    Button mTvAddAddress;

    private ProgressDialog mPd;
    private ArrayList<String> address;
    private List<List<String>> mAddressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);
        ButterKnife.bind(this);

        showProgressDialog();

        address = new ArrayList<String>();
        mAddressList = new ArrayList<List<String>>();
        mLvAddressDetails.setOnItemClickListener(this);
        getAddressFromBmob();
    }


    @OnClick({R.id.iv_address_back, R.id.btn_add_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_back:
                finish();
                break;
            case R.id.btn_add_address:
                mAddressList.clear();
                Intent intent = new Intent(SetAddressActivity.this, EditAddressActivity.class);
                intent.putStringArrayListExtra("address", address);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public int click(View v, int temp) {
        Log.i("callback", "click: 执行");
        switch (v.getId()) {
            case R.id.rl_address:
                Intent intent = new Intent(SetAddressActivity.this, EditAddressActivity.class);
                intent.putStringArrayListExtra("address", address);
                startActivityForResult(intent, 1);
                break;
            case R.id.cb_default_address:
                if (((CheckBox) v).isChecked()) {        //如果是选中状态
                    if (temp != -1) {           //temp不为-1，说明已经进行过点击事件
                        CheckBox tempButton = (CheckBox) findViewById(temp);
                        if (tempButton != null) {
                            tempButton.setChecked(false);   //取到上一次点击的RadioButton，并设置为未选中状态
                            tempButton.setClickable(true);
                        }
                    }
                    temp = v.getId();  //将temp重新赋值，记录下本次点击的RadioButton
                    showProgressDialog();
                    updateAddressToBmob();
                    Log.i("check1", "onCheckedChanged: " + temp);
                }
                Log.i("check2", "onCheckedChanged: " + temp);
                return temp;
            case R.id.tv_delete_address:
                Log.i("deleteAddress", "click: address点击");
                break;
        }
        return -1;
    }

    @Override
    public int onCheckedChanged(CompoundButton buttonView, boolean isChecked, int temp) {
        if (isChecked) {        //如果是选中状态
            if (temp != -1) {   //temp不为-1，说明已经进行过点击事件
                CheckBox tempButton = (CheckBox) findViewById(temp);
                if (tempButton != null) {
                    tempButton.setChecked(false);   //取到上一次点击的RadioButton，并设置为未选中状态
                    tempButton.setClickable(true);
                }
            }
            temp = buttonView.getId();  //将temp重新赋值，记录下本次点击的RadioButton
            showProgressDialog();
            updateAddressToBmob();
            Log.i("check1", "onCheckedChanged: " + temp);
        }
        Log.i("check2", "onCheckedChanged: " + temp);
        return temp;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    address = data.getStringArrayListExtra("address");
                    splitAddress();
                    Toast.makeText(SetAddressActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    mLvAddressDetails.setAdapter(new SetAddressListAdapter(SetAddressActivity.this,
                            mAddressList, SetAddressActivity.this));
                }
                break;
        }
    }


    private void getAddressFromBmob() {
        BmobQuery<User_Profile> bmobQuery = new BmobQuery<User_Profile>();
        // bmobQuery.addQueryKeys("User_Receive_Address");
        String objectID = ((MyApplication) getApplication()).getObjectID();
        bmobQuery.getObject(objectID, new QueryListener<User_Profile>() {
            @Override
            public void done(User_Profile user_profile, BmobException e) {
                if (e == null) {
                    address = (ArrayList<String>) user_profile.getUser_Receive_Address();
                    if (user_profile.getUser_Receive_Address().size() >= 1) {
                        //  address.clear();
                        splitAddress();
                        mLvAddressDetails.setAdapter(new SetAddressListAdapter(SetAddressActivity.this,
                                mAddressList, SetAddressActivity.this));
                        closeProgressDialog();
                    } else
                        Toast.makeText(SetAddressActivity.this, "收货地址无数据", Toast.LENGTH_SHORT).show();
                } else {
                    closeProgressDialog();
                    Toast.makeText(SetAddressActivity.this, "获取地址失败" + e.getErrorCode() + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    private void updateAddressToBmob() {
        String objectID = ((MyApplication) getApplication()).getObjectID();
        User_Profile u = new User_Profile();
        Log.i("list", "updateDataToBmob: " + address);
        u.setValue("User_Receive_Address.0", "1");
        u.update(objectID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    closeProgressDialog();
                    Toast.makeText(SetAddressActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    closeProgressDialog();
                    Log.i("why", e.getMessage());
                    Toast.makeText(SetAddressActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void splitAddress() {
        ArrayList<String> splitAddress = new ArrayList<String>();
        for (int i = 0; i < address.size(); i++) {
            splitAddress.add(address.get(i));
            Log.i("address1", "splitAddress: " + address.get(i) + "----" + address.size());
            if (i % 3 == 0) {
                Log.i("address2", "splitAddress: " + address.get(i));
                mAddressList.add(splitAddress);
                splitAddress = new ArrayList<String>();
            }
        }
    }

    private void showProgressDialog() {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setMessage("加载中");
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("itemposition", "onItemClick: " + position + "----" + id);

    }
}
