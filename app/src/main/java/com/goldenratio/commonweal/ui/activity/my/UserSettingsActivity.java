package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.DynamicPhotoShow;
import com.goldenratio.commonweal.ui.fragment.MyFragment;
import com.goldenratio.commonweal.util.GlideLoader;
import com.squareup.picasso.Picasso;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 作者：Created by 龙啸天 on 2016/7/01 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 */
public class UserSettingsActivity extends Activity {


    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_user_nickname)
    TextView mTvUserNickname;
    @BindView(R.id.tv_user_autograph)
    TextView mTvUserAutograph;
    @BindView(R.id.tv_user_sex)
    TextView mTvUserSex;
    @BindView(R.id.civ_set_avatar)
    ImageView mMinAvatar;

    private String userName;
    private String userNickname;
    private String autograph;
    private String userSex;
    private String avaUrl;

    private int whichSex;
    private ProgressDialog mPd;
    private ImageConfig mImageConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        ButterKnife.bind(this);

        getMyData();
        Picasso.with(this).load(avaUrl).into(mMinAvatar);
        mTvUserSex.setText(userSex);
        mTvUserName.setText(userName);
        mTvUserNickname.setText(userNickname);
        mTvUserAutograph.setText(autograph);
    }

    /**
     * 各界面返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
        //图片选择器返回数据
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            String pathString = pathList.get(0);
            Bitmap bitmap = null;
            try {
                File file = new File(pathString);
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(pathString);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            mMinAvatar.setImageBitmap(bitmap);
            showProgressDialog();
            uploadAvatarFile(pathString);
        }
    }

    @OnClick({R.id.iv_us_back, R.id.rl_set_avatar, R.id.civ_set_avatar, R.id.rl_set_userName, R.id.rl_set_userNickName, R.id.rl_set_userSex, R.id.rl_set_autograph, R.id.rl_set_address})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_us_back:
                finish();
                break;
            case R.id.rl_set_avatar:
                imageSecectorConfig();
                ImageSelector.open(UserSettingsActivity.this, mImageConfig);   // 开启图片选择器
                break;
            case R.id.civ_set_avatar:
                Intent intent0 = new Intent(UserSettingsActivity.this, DynamicPhotoShow.class);
                ArrayList<String> list = new ArrayList<String>();
                list.add(avaUrl);
                intent0.putStringArrayListExtra("list", list);
                startActivity(intent0);
                break;
            case R.id.rl_set_userName:
                if (TextUtils.isEmpty(mTvUserName.getText())) {
                    intent = new Intent(this, SetUserNameActivity.class);
                    startActivityForResult(intent, 2);
                } else
                    Toast.makeText(UserSettingsActivity.this, "您已经设置了用户名，不允许重复设置", Toast.LENGTH_SHORT).show();
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
            case R.id.rl_set_address:
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
        new AlertDialog.Builder(this).setTitle("性别").setSingleChoiceItems(
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
                        updateDataToBmob(sex, 0, "User_sex");
                    }
                }).setNegativeButton("取消", null).show();
    }


    private void imageSecectorConfig() {
        mImageConfig
                = new ImageConfig.Builder(new GlideLoader())
                .steepToolBarColor(getResources().getColor(R.color.black))
                .titleBgColor(getResources().getColor(R.color.white))
                .titleTextColor(getResources().getColor(R.color.black))
                // (截图默认配置：关闭    比例 1：1    输出分辨率  500*500)
                .crop(1, 1, 1000, 1000)
                // 开启单选   （默认为多选）
                .singleSelect()
                // 开启拍照功能 （默认关闭）
                .showCamera()
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
                .filePath("/ImageSelector/Pictures")
                .build();
    }

    /**
     * 更新本地数据库
     *
     * @param userData
     * @param userRow
     */
    private void updateDataToSqlite(String userData, String userRow) {
        String sqlCmd = "UPDATE User_Profile SET " + userRow + "='" + userData + "'";
        UserDao ud = new UserDao(this);
        ud.execSQL(sqlCmd);
    }

    /**
     * 更新Bmob数据库
     *
     * @param userData
     * @param i        用来判断更新的字段  0:Sex,1:昵称,2:个签,3:头像;
     */
    private void updateDataToBmob(final String userData, int i, final String userRow) {
        String userID = MyFragment.mUserID;
        User_Profile u = new User_Profile();
        if (i == 0) {
            u.setUser_Sex(userData);
        } else if (i == 1)
            u.setUser_Nickname(userData);
        else if (i == 2) {
            u.setUser_Autograph(userData);
        } else u.setUser_image_hd(userData);
        u.update(userID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    closeProgressDialog();
                    updateDataToSqlite(userData, userRow);
                    Toast.makeText(UserSettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("why", e.getMessage());
                    closeProgressDialog();
                    Toast.makeText(getApplication(), "修改失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 上传头像文件到Bmob
     */
    private void uploadAvatarFile(String avaPath) {
        final BmobFile bmobFile = new BmobFile(new File(avaPath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    String avatarURL = bmobFile.getFileUrl();    //返回的上传文件的完整地址
                    updateDataToBmob(avatarURL, 3, "User_Avatar");
                } else {
                    Toast.makeText(UserSettingsActivity.this, "上传头像失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();

                }
            }

        });
    }

    /**
     * 显示输入对话框（输入昵称、个签）
     *
     * @param TV
     */
    private void showInputDialog(final TextView TV) {
        final EditText ETUSER = new EditText(this);
        final int X;
        final String USERROW;
        if (TV == mTvUserNickname) {
            ETUSER.setSingleLine(true);
            ETUSER.setText(mTvUserNickname.getText());
            USERROW = "User_Nickname";
            X = 1;
        } else {
            ETUSER.setMaxLines(3);
            ETUSER.setText(mTvUserAutograph.getText());
            USERROW = "User_Autograph";
            X = 2;
        }
        new AlertDialog.Builder(this).setTitle("编辑").setView(
                ETUSER).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog();
                String userData = ETUSER.getText().toString();
                TV.setText(userData);
                updateDataToBmob(userData, X, USERROW);
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

    /**
     * 获取我的界面传过来的数据
     */
    private void getMyData() {
        Intent intent = getIntent();
        userSex = intent.getStringExtra("user_sex");
        userName = intent.getStringExtra("user_name");
        userNickname = intent.getStringExtra("user_nickname");
        autograph = intent.getStringExtra("autograph");
        avaUrl = intent.getStringExtra("avaUrl");
    }
}
