package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 作者：Created by 龙啸天 on 2016/7/02 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 */
public class SetUserNameActivity extends Activity implements TextWatcher {

    @BindView(R.id.et_set_username)
    EditText mEtSetUsername;
    @BindView(R.id.btn_save_username)
    Button mBtnSaveUsername;

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name);
        ButterKnife.bind(this);

        mEtSetUsername.addTextChangedListener(this);

    }

    @OnClick({R.id.iv_set_name_back, R.id.btn_save_username})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_set_name_back:
                finish();
                break;
            case R.id.btn_save_username:
                if (checkUserName(mEtSetUsername.getText().toString())) {
                    showProgressDialog();
                    isHasUserName();
                    break;
                } else {
                    Toast.makeText(SetUserNameActivity.this, "请检查您的用户名是否填写规范", Toast.LENGTH_SHORT).show();
                }
        }
    }


    //判断此用户名是否已经存在
    private void isHasUserName() {
        BmobQuery<User_Profile> bmobQuery = new BmobQuery<User_Profile>();
        bmobQuery.addWhereEqualTo("User_Name", mEtSetUsername.getText().toString());
        bmobQuery.findObjects(new FindListener<User_Profile>() {
            @Override
            public void done(List<User_Profile> list, BmobException e) {
                if (e == null) {
                    if (list.isEmpty()) {
                        updateDataToSqlite();
                        updateDataToBmob();
                    } else {
                        closeProgressDialog();
                        Log.d("query", "查询成功");
                        Log.d("info", list + "");
                        closeProgressDialog();
                        Toast.makeText(SetUserNameActivity.this, "此用户名已有人用过", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    closeProgressDialog();
                    Log.d("query", "查询失败");
                    Toast.makeText(SetUserNameActivity.this, e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void returnDataToUserSet() {
        String userName = mEtSetUsername.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("user_Name", userName);
        setResult(RESULT_OK, intent);
        closeProgressDialog();
        finish();
    }

    private void updateDataToSqlite() {
        String userName = mEtSetUsername.getText().toString();
        String sqlCmd = "UPDATE User_Profile SET User_Name='" + userName + "'";
        UserDao ud = new UserDao(this);
        ud.execSQL(sqlCmd);
    }

    private void updateDataToBmob() {
        String userID = MyFragment.mUserID;
        String userName = mEtSetUsername.getText().toString();
        User_Profile u = new User_Profile();
        u.setUser_Name(userName);
        u.update(userID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(SetUserNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    returnDataToUserSet();
                } else {
                    Log.i("why", e.getMessage());
                    Toast.makeText(SetUserNameActivity.this, "修改失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private boolean checkUserName(String uName) {
        //用户名由3-15个字符组成（不能为中文）
//        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{3,15}$";
        String regex = "^\\w{3,15}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(uName);
        return m.matches();
    }

    private void closeProgressDialog() {
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
            mPd = null;
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!TextUtils.isEmpty(mEtSetUsername.getText().toString())) {
            mBtnSaveUsername.setEnabled(true);
        } else mBtnSaveUsername.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
