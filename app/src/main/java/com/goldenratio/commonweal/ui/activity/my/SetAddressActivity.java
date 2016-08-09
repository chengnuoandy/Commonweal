package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.SetAddressListAdapter;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

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
    public void click(View v) {
        Log.i("callback", "click: 执行");
        switch (v.getId()) {
            case R.id.rl_address:
                Intent intent = new Intent(SetAddressActivity.this, EditAddressActivity.class);
                intent.putStringArrayListExtra("address", address);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_delete_address:
                Log.i("deleteAddress", "click: address点击");
                break;
        }

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
        bmobQuery.getObject(this, MyFragment.mUserID, new GetListener<User_Profile>() {
            @Override
            public void onSuccess(User_Profile user_profile) {
                if (user_profile.getUser_Receive_Address() != null) {
                    List user_receive_address = user_profile.getUser_Receive_Address();
                    address = (ArrayList<String>) user_profile.getUser_Receive_Address();
                    splitAddress();
                    mLvAddressDetails.setAdapter(new SetAddressListAdapter(SetAddressActivity.this,
                            mAddressList, SetAddressActivity.this));
                }
                closeProgressDialog();
            }

            @Override
            public void onFailure(int i, String s) {
                closeProgressDialog();
                Toast.makeText(SetAddressActivity.this, "获取地址失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void splitAddress() {
        ArrayList<String> splitAddress = new ArrayList<String>();
        for (int i = 0; i < address.size(); i++) {
            splitAddress.add(address.get(i));
            Log.i("address1", "splitAddress: " + address.get(i) + "----" + address.size());
            if ((i + 1) % 3 == 0) {
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

    }
}
