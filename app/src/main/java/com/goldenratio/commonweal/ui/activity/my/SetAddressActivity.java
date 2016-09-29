package com.goldenratio.commonweal.ui.activity.my;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.SetAddressListAdapter;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class SetAddressActivity extends BaseActivity implements SetAddressListAdapter.Callback {

    @BindView(R.id.iv_address_back)
    ImageView mIvBack;
    @BindView(R.id.lv_address_details)
    ListView mLvAddressDetails;
    @BindView(R.id.btn_add_address)
    Button mTvAddAddress;

    private ProgressDialog mPd;
    private ArrayList<String> address;  //存储收货地址
    private List<List<String>> mAddressList;  //ListView适配源
    private int clickPosition = -1;    //用来存储点击的位置----默认地址的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);
        ButterKnife.bind(this);

        address = new ArrayList<String>();
        mAddressList = new ArrayList<List<String>>();
        showProgressDialog();
        getAddressFromBmob();
        new ImmersiveUtil(this, R.color.white, true);
    }


    @OnClick({R.id.iv_address_back, R.id.btn_add_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_back:
                finish();
                break;
            case R.id.btn_add_address:
                if ((address.size() - 1) % 3 == 0) {
                    Intent intent = new Intent(SetAddressActivity.this, EditAddressActivity.class);
                    intent.putStringArrayListExtra("address", address);
                    startActivityForResult(intent, 1);
                } else
                    Toast.makeText(SetAddressActivity.this, "未知错误，请重新设置地址", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public int click(final View v) {
        Log.i("callback", "click: 执行");
        switch (v.getId()) {
            case R.id.rl_address:
                Intent intent = new Intent(SetAddressActivity.this, EditAddressActivity.class);
                intent.putStringArrayListExtra("address", address);
                intent.putExtra("position", (int) v.getTag());
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_delete_address:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("提示");
                dialog.setMessage("您确定要删除此地址？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = (int) v.getTag();
                        showProgressDialog();
                        removeAddressToBmob(position);
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
                Log.i("deleteAddress", "click: address点击");
                break;
            default:
                CheckBox mCbSelectDefaultAddress = ((CheckBox) v);
                if (((CheckBox) v).isChecked()) {        //如果是选中状态
                    if (clickPosition != -1) {            //temp不为-1，说明已经进行过点击事件(已经设置过默认地址)
                        CheckBox tempButton = (CheckBox) findViewById(clickPosition);
                        if (tempButton != null) {
                            tempButton.setChecked(false);   //将上一次的checkbox设置为未选中状态
                            tempButton.setClickable(true);  //上次的checkbox为可以点击
                        }
                    }
                    updateAddressToBmob(v.getId() + "");
                    clickPosition = v.getId();               //将temp重新赋值，记录下本次点击的RadioButton
                    showProgressDialog();
                    Log.i("check1", "onCheckedChanged: " + clickPosition);
                }
                Log.i("check2", "onCheckedChanged: " + clickPosition);
                if (v.getId() == clickPosition) {
                    mCbSelectDefaultAddress.setChecked(true);        //将本次点击的checkbox设置为选中状态
                    mCbSelectDefaultAddress.setClickable(false);     //本次checkbox不能点击
                } else {
                    mCbSelectDefaultAddress.setChecked(false);
                }
                return v.getId();
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    address = data.getStringArrayListExtra("address");
                    clickPosition = Integer.parseInt(address.get(0));
                    mAddressList.clear();
                    splitAddress();
                    if (address.size() != 1 && (address.size() - 1) % 3 == 0) {
                        Toast.makeText(SetAddressActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        if (mLvAddressDetails.getAdapter() == null)
                            mLvAddressDetails.setAdapter(new SetAddressListAdapter(SetAddressActivity.this,
                                    mAddressList, SetAddressActivity.this));
                        else ((BaseAdapter) mLvAddressDetails.getAdapter()).notifyDataSetChanged();
                    } else
                        Toast.makeText(SetAddressActivity.this, "请刷新地址界面", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void getAddressFromBmob() {
        BmobQuery<User_Profile> bmobQuery = new BmobQuery<User_Profile>();
        String objectID = ((MyApplication) getApplication()).getObjectID();
        bmobQuery.getObject(objectID, new QueryListener<User_Profile>() {
            @Override
            public void done(User_Profile user_profile, BmobException e) {
                if (e == null) {
                    address = (ArrayList<String>) user_profile.getUser_Receive_Address();
                    if (address.size() != 1 && (address.size() - 1) % 3 == 0) {
                        clickPosition = Integer.parseInt(address.get(0));
                        splitAddress();
                        mLvAddressDetails.setAdapter(new SetAddressListAdapter(SetAddressActivity.this,
                                mAddressList, SetAddressActivity.this));
                    } else {
                        address.clear();
                        address.add("0");
                        Toast.makeText(SetAddressActivity.this, "您尚未设置过收货地址", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("获取地址失败", e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
                closeProgressDialog();
            }

        });
    }

    /**
     * 更新默认地址
     *
     * @param position 默认地址的位置
     */
    private void updateAddressToBmob(final String position) {
        String objectID = ((MyApplication) getApplication()).getObjectID();
        User_Profile u = new User_Profile();
        Log.i("list", "updateDataToBmob: " + address);
        u.setValue("User_Receive_Address.0", position);
        u.update(objectID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    address.set(0, position);
                    mAddressList.clear();
                    splitAddress();
                    Toast.makeText(SetAddressActivity.this, "设置默认地址成功", Toast.LENGTH_SHORT).show();
                    closeProgressDialog();
                } else {
//                    Toast.makeText(SetAddressActivity.this, "设置失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    clickPosition = Integer.parseInt(address.get(0));
                    closeProgressDialog();
//                    Log.i("更新地址", e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
                ((BaseAdapter) mLvAddressDetails.getAdapter()).notifyDataSetChanged();
                closeProgressDialog();
            }

        });
    }

    /**
     * 删除收货地址，注意：删除后默认地址的位置会随之变化
     *
     * @param position 收货地址的位置
     */
    private void removeAddressToBmob(final int position) {
        final ArrayList<String> tempAddress = address;
        int t = (3 * position) + 1;     //此为address要删除数据的位置，确保不会删除address[0]
        Log.i("address删除前", position + "---" + t + "---" + "removeAddressToBmob: " + address);
        int defutAddress = Integer.parseInt(address.get(0));
        //如果删除的位置在默认地址之前，则默认地址位置减1
        if (defutAddress >= position && defutAddress != 0) {
            address.set(0, defutAddress - 1 + "");
            clickPosition = Integer.parseInt(defutAddress - 1 + "");
        }
        address.remove(t);  //移除此位置的数据-----后面的会往前移，移除三次
        address.remove(t);
        address.remove(t);
        String objectID = ((MyApplication) getApplication()).getObjectID();
        Log.i("address删除剩余", position + "---s" + t + "removeAddressToBmob: " + address);
        User_Profile u = new User_Profile();
        u.setUser_Receive_Address(address);
        u.update(objectID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(SetAddressActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    mAddressList.clear();
                    if (address.size() != 1) {
                        splitAddress();
                    } else
                        Toast.makeText(SetAddressActivity.this, "请设置收货地址", Toast.LENGTH_SHORT).show();
                    ((BaseAdapter) mLvAddressDetails.getAdapter()).notifyDataSetChanged();
                    Log.i("mAddressList", "done: " + mAddressList.size());
                    closeProgressDialog();
                } else {
                    address = tempAddress;
//                    Toast.makeText(SetAddressActivity.this, "删除失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
//                    closeProgressDialog();
//                    Log.i("删除失败", e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }

        });
    }

    /**
     * 拆分地址
     */
    private void splitAddress() {
        ArrayList<String> splitAddress = new ArrayList<String>();
        for (int i = 0; i < address.size(); i++) {
            splitAddress.add(address.get(i));
            Log.i("拆分地址", i + ":->" + address.get(i) + "----" + address.size());
            if (i != 0 && i % 3 == 0) {
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

}
