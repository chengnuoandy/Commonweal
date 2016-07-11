package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.fragment.MyFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.UpdateListener;

public class UserSettingsActivity extends Activity {


    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_user_nickname)
    TextView mTvUserNickname;
    @BindView(R.id.tv_user_autograph)
    TextView mTvUserAutograph;
    @BindView(R.id.tv_user_sex)
    TextView mTvUserSex;
    @BindView(R.id.iv_set_avatar)
    ImageView mMinAvatar;

    private String userName;
    private String userNickname;
    private String autograph;
    private String userSex;
    private String avaMinUrl;

    private int whichSex;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        ButterKnife.bind(this);

        getMyData();
        Picasso.with(this).load(avaMinUrl).into(mMinAvatar);
        mTvUserSex.setText(userSex);
        mTvUserName.setText(userName);
        mTvUserNickname.setText(userNickname);
        mTvUserAutograph.setText(autograph);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    String userName = data.getStringExtra("user_Name");
                    mTvUserName.setText(userName);
                }
                break;
            case 3:
                break;
        }
    }

    @OnClick({R.id.iv_us_back, R.id.rl_set_avatar, R.id.rl_set_userName, R.id.rl_set_userNickName, R.id.rl_set_userSex, R.id.rl_set_autograph, R.id.tv_set_address})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_us_back:
                finish();
                break;
            case R.id.rl_set_avatar:
                intent = new Intent(this, SetAvatarActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_set_userName:
                intent = new Intent(this, SetUserNameActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.rl_set_userNickName:
                showInputDialog(mTvUserNickname);
                break;
            case R.id.rl_set_userSex:
                showChoiceDialog();
                break;
            //设置用户个性签名
            case R.id.rl_set_autograph:
                showInputDialog(mTvUserAutograph);
                break;
            case R.id.tv_set_address:
                intent = new Intent(this, SetAddressActivity.class);
                startActivityForResult(intent, 3);
                break;
        }
    }

    private void showChoiceDialog() {
        if (mTvUserSex.getText().equals("男"))
            whichSex = 0;
        else
            whichSex = 1;
        new AlertDialog.Builder(this).setTitle("单选框").setIcon(
                android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                new String[]{"男", "女"}, whichSex,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgressDialog();
                        String sex;
                        whichSex = which;
                        if (which == 0) {
                            sex = "男";
                        } else {
                            sex = "女";
                        }
                        mTvUserSex.setText(sex);
                        updateDataToSqlite(sex, "User_sex");
                        updateDataToBmob(sex, 0);
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void updateDataToSqlite(String userData, String userRow) {
        String sqlCmd = "UPDATE User SET " + userRow + "='" + userData + "'";
        UserDao ud = new UserDao(this);
        ud.execSQL(sqlCmd);
    }

    private void updateDataToBmob(String userData, int i) {
        String userID = MyFragment.mUserID;
        User u = new User();
        if (i == 0) {
            u.setUser_Sex(userData);
        } else if (i == 1)
            u.setUser_Nickname(userData);
        else u.setUser_Autograph(userData);
        u.update(this, userID, new UpdateListener() {
            @Override
            public void onSuccess() {
                closeProgressDialog();
                Toast.makeText(UserSettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("why", s);
                closeProgressDialog();
                Toast.makeText(getApplication(), "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showInputDialog(final TextView TV) {
        final EditText TVUSER = new EditText(this);
        final int X;
        final String USERROW;
        if (TV == mTvUserNickname) {
            TVUSER.setMaxLines(1);
            USERROW = "User_Nickname";
            X = 1;
        } else {
            TVUSER.setMaxLines(3);
            USERROW = "User_Autograph";
            X = 2;
        }
        new AlertDialog.Builder(this).setTitle("请输入").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                TVUSER).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog();
                String userData = TVUSER.getText().toString();
                TV.setText(userData);
                updateDataToSqlite(userData, USERROW);
                updateDataToBmob(userData, X);
            }
        }).setNegativeButton("取消", null).show();
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

    private void getMyData() {
        Intent intent = getIntent();
        userSex = intent.getStringExtra("user_sex");
        userName = intent.getStringExtra("user_name");
        userNickname = intent.getStringExtra("user_nickname");
        autograph = intent.getStringExtra("autograph");
        avaMinUrl = intent.getStringExtra("avaMinUrl");
    }
}
