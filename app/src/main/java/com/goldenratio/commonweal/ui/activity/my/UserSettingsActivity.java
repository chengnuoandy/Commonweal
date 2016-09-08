package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.api.Constants;
import com.goldenratio.commonweal.api.ErrorInfo;
import com.goldenratio.commonweal.api.User;
import com.goldenratio.commonweal.api.UsersAPI;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.ui.activity.DynamicPhotoShow;
import com.goldenratio.commonweal.ui.activity.RegisterActivity;
import com.goldenratio.commonweal.ui.fragment.MyFragment;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.GlideLoader;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.goldenratio.commonweal.util.MD5Util;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
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
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 作者：Created by 龙啸天 on 2016/7/01 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 */
public class UserSettingsActivity extends Activity implements IMySqlManager {


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
    @BindView(R.id.tv_setweibo)
    TextView mTvSetweibo;
    @BindView(R.id.rl_set_weibo)
    RelativeLayout mRlSetWeibo;

    private String userName;
    private String userNickname;
    private String autograph;
    private String userSex;
    private String avaUrl;

    private SsoHandler mSsoHandler;
    private UsersAPI mUsersAPI;
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken = null;
    private User upUser = null;

    private int whichSex;
    private ProgressDialog mPd;
    private ImageConfig mImageConfig;
    private String mUserID;
    private MySqlManagerImpl mySqlManager;
    private MySqlManagerImpl mManager2;
    private boolean flag;
    private String wbID;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        ButterKnife.bind(this);
        mUserID = MyFragment.mUserID;

        // 创建微博实例
        // 快速授权时，不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, null);
        mSsoHandler = new SsoHandler(this, mAuthInfo);

        getMyData();
        Picasso.with(this).load(avaUrl).into(mMinAvatar);
        mTvUserSex.setText(userSex);
        mTvUserName.setText(userName);
        mTvUserNickname.setText(userNickname);
        mTvUserAutograph.setText(autograph);
        if (!TextUtils.isEmpty(wbID)) {
            mTvSetweibo.setText("已绑定");
        }
        new ImmersiveUtil(this, R.color.white, true);
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
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

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
                if (resultCode == RESULT_OK) {
                    int flagR = data.getIntExtra("type", -1);
                    if (flagR == 1) {
                        //隐藏键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        flag = true;
                        mManager2 = new MySqlManagerImpl(this, this, "设置新密码", "", "请输入新的支付密码");
                        mManager2.queryUserCoinAndSixPwdByObjectId(null, null);
                    }
                }
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

    @OnClick({R.id.iv_us_back, R.id.rl_set_avatar, R.id.civ_set_avatar, R.id.rl_set_userName,
            R.id.rl_set_userNickName, R.id.rl_set_userSex, R.id.rl_set_autograph,
            R.id.rl_set_address, R.id.alter_login_pwd, R.id.alter_pay_pwd, R.id.rl_set_weibo})
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
            case R.id.alter_login_pwd:
                showPwdDialog();
                break;
            case R.id.alter_pay_pwd:
                showPayPwdDialog();
                break;
            case R.id.rl_set_weibo:
                String WbID = ((MyApplication) (getApplicationContext())).getWbId();
                if (TextUtils.isEmpty(WbID)) {
                    wbAuthorize();
                } else {
                    Toast.makeText(UserSettingsActivity.this, "你已经绑定了！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 微博绑定
     */
    private void wbAuthorize() {
        new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("微博只能绑定一次，由于特殊原因，解绑需要联系官方，绑定之前请慎重考虑，你确定要绑定吗？")
                .setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSsoHandler.authorize(new AuthListener());
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * 修改支付密码
     */
    private void showPayPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"我记得支付密码", "我忘记了支付密码"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mySqlManager = new MySqlManagerImpl(UserSettingsActivity.this, UserSettingsActivity.this, "设置新密码", "", "请输入旧支付密码");
                    mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null);
                } else {
                    Intent intent = new Intent(UserSettingsActivity.this, RegisterActivity.class);
                    intent.putExtra("type", 3);
                    intent.putExtra("phone", phone);
                    startActivityForResult(intent, 3);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 修改登陆密码的提示框
     */
    private void showPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.view_pwd_dialog, null);
        builder.setTitle("修改密码");
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText editText = (EditText) view.findViewById(R.id.et_old);
        final EditText editText1 = (EditText) view.findViewById(R.id.et_new1);
        final EditText editText2 = (EditText) view.findViewById(R.id.et_new2);

        view.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText()) || TextUtils.isEmpty(editText1.getText()) || TextUtils.isEmpty(editText2.getText())) {
                    Toast.makeText(UserSettingsActivity.this, "请填写所有数据！", Toast.LENGTH_SHORT).show();
                } else {

                    if (editText1.getText().toString().equals(editText2.getText().toString())) {
                        BmobQuery<User_Profile> query = new BmobQuery<User_Profile>();
                        query.getObject(mUserID, new QueryListener<User_Profile>() {
                            @Override
                            public void done(User_Profile user_profile, BmobException e) {
                                if (e == null) {
                                    if (user_profile.getUser_Password().equals(MD5Util.createMD5(editText.getText().toString()))) {
                                        showProgressDialog();
                                        updateDataToBmob(MD5Util.createMD5(editText1.getText().toString()), 4, null);
                                        alertDialog.dismiss();
                                    } else {
                                        Toast.makeText(UserSettingsActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
//                                    Toast.makeText(UserSettingsActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserSettingsActivity.this, "两次输入的密码不同！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        view.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFp = new Intent(UserSettingsActivity.this, RegisterActivity.class);
                startActivityForResult(intentFp, 2);
                alertDialog.dismiss();
            }
        });
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
                .filePath("/GR_commonweal/avatarCache")
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
     * @param i        用来判断更新的字段  0:Sex,1:昵称,2:个签,3:头像,4:登陆密码;
     */
    private void updateDataToBmob(final String userData, int i, final String userRow) {
        User_Profile u = new User_Profile();
        if (i == 0) {
            u.setUser_Sex(userData);
        } else if (i == 1)
            u.setUser_Nickname(userData);
        else if (i == 2) {
            u.setUser_Autograph(userData);
        } else if (i == 3) {
            u.setUser_image_hd(userData);
        } else if (i == 4) {
            u.setUser_Password(userData);
        }
        u.update(mUserID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (userRow != null) {
                        updateDataToSqlite(userData, userRow);
                    }
                    closeProgressDialog();
                    Toast.makeText(UserSettingsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                } else {
//                    Log.i("why", e.getMessage());
                    closeProgressDialog();
//                    Toast.makeText(getApplication(), "修改失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
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
//                    Toast.makeText(UserSettingsActivity.this, "上传头像失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    /**
     * 显示输入对话框（输入昵称、个签）
     *
     * @param TV
     */
/*    private void showInputDialog() {

        new AlertDialog.Builder(this).setTitle("编辑").setView(
                ETUSER).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("取消", null).show();
    }*/
    private void showInputDialog(final TextView TV) {
        // 设置内容区域为自定义View
        LinearLayout SetDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_register, null);
        final EditText ETUSER = (EditText) SetDialog.findViewById(R.id.et_userName);
        TextView tvTitle = (TextView) SetDialog.findViewById(R.id.tv_title_nickname);
        final int X;
        final String USERROW;
        String dialogTitle;
        if (TV == mTvUserNickname) {
            ETUSER.setSingleLine(true);
            ETUSER.setText(mTvUserNickname.getText());
            dialogTitle = "修改昵称";
            USERROW = "User_Nickname";
            X = 1;
        } else {
            dialogTitle = "修改签名";
            ETUSER.setHint("请输入个性签名");
            ETUSER.setMaxLines(3);
            ETUSER.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            ETUSER.setText(mTvUserAutograph.getText());
            USERROW = "User_Autograph";
            X = 2;
        }
        tvTitle.setText(dialogTitle);
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        // builder.setTitle(dialogTitle);
        builder.setView(SetDialog);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog();
                String userData = ETUSER.getText().toString();
                TV.setText(userData);
                updateDataToBmob(userData, X, USERROW);
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
        wbID = intent.getStringExtra("wbid");
        phone = intent.getStringExtra("phone");
    }

    @Override
    public void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin) {

    }

    /**
     * 密码验证正确后回调
     *
     * @param sixPwd 加密后的输入的密码
     * @param event  无
     */
    @Override
    public void showSixPwdOnFinishInput(String sixPwd, int event) {
        //判断是不是第二次输入
        if (flag) {
            mManager2.updateUserSixPwdByObjectId(sixPwd);
            flag = false;
        } else {
            flag = true;
            mManager2 = new MySqlManagerImpl(this, this, "设置新密码", "", "请输入新的支付密码");
            mManager2.queryUserCoinAndSixPwdByObjectId(null, null);
        }
    }

    @Override
    public boolean updateUserCoinByObjectId(String sumCoin, String changeCoin, int flag) {
        return false;
    }

    /**
     * 输入密码后回调
     *
     * @param mStrUserCoin 用户硬币
     * @param sixPwd       用户的支付密码
     * @return 无
     */
    @Override
    public boolean queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd) {
        if (flag) {
            mManager2.showSixPwdOnFinishInput(sixPwd, 0);
        } else {
            //检测密码是否正确
            mySqlManager.showSixPwdOnFinishInput(sixPwd, 1);
        }
        return false;
    }

    @Override
    public boolean updateUserSixPwdByObjectId(String sixPwd) {
        return false;
    }


    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User_Profile#parse 将JSON串解析成User对象
                upUser = User.parse(response);
                if (upUser != null) {
                    updateDB(upUser);
                } else {
                    closeProgressDialog();
                    Toast.makeText(UserSettingsActivity.this, response,
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Log.d("lxc", "onWeiboException: openAPI访问错误 " + info);
            closeProgressDialog();
            Toast.makeText(UserSettingsActivity.this, "网络访问异常！", Toast.LENGTH_SHORT).show();
        }
    };

    private void updateDB(final User upUser) {
        User_Profile userProfile = new User_Profile();
        userProfile.setUser_WbID(upUser.id);
        userProfile.setUser_IsV(upUser.verified);
        userProfile.setUser_VerifiedReason(upUser.verified_reason); //认证原因
        final String id = mUserID;
        userProfile.update(mUserID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    mTvSetweibo.setText("已绑定");
                    wbID = upUser.id;
                    MyFragment.userWBid = upUser.id;
                    UserDao mUserDao = new UserDao(UserSettingsActivity.this);
                    mUserDao.execSQL("update User_Profile set User_weiboID = ? where objectId = ?", new String[]{upUser.id, id});
                    closeProgressDialog();
                    Toast.makeText(UserSettingsActivity.this, "绑定成功！", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(UserSettingsActivity.this, "绑定失败！", Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            showProgressDialog();
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {

                mUsersAPI = new UsersAPI(UserSettingsActivity.this, Constants.APP_KEY, mAccessToken);
                //openAPI相关
                long uid = Long.parseLong(mAccessToken.getUid());

                mUsersAPI.show(uid, mListener);
                Toast.makeText(UserSettingsActivity.this,
                        "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                closeProgressDialog();
                Toast.makeText(UserSettingsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(UserSettingsActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(UserSettingsActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
