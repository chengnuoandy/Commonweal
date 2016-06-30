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
import android.widget.ImageView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.fragment.MyFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.UpdateListener;

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
                    updateDataToSqlite();
                    updateDataToBmob();
                    break;
                } else {
                    Toast.makeText(SetUserNameActivity.this, "请检查您的用户名是否填写规范", Toast.LENGTH_SHORT).show();
                }
        }
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
        String sqlCmd = "UPDATE User SET User_Name='" + userName + "'";
        UserDao ud = new UserDao(this);
        ud.execSQL(sqlCmd);
    }

    private void updateDataToBmob() {
        String userID = MyFragment.mUserID;
        String userName = mEtSetUsername.getText().toString();
        User u = new User();
        u.setUser_Name(userName);
        u.update(SetUserNameActivity.this, userID, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(SetUserNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                returnDataToUserSet();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("why", s);
                Toast.makeText(SetUserNameActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkUserName(String uName) {
        //用户名由3-15个字符组成（不能为中文）
        String regex = "^[a-zA-Z0-9_]{3,15}$";
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
