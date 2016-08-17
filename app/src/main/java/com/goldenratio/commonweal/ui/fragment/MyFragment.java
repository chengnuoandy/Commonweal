package com.goldenratio.commonweal.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.LoginActivity;
import com.goldenratio.commonweal.ui.activity.my.LogisticsInformation;
import com.goldenratio.commonweal.ui.activity.my.MessageActivity;
import com.goldenratio.commonweal.ui.activity.my.MySetActivity;
import com.goldenratio.commonweal.ui.activity.my.UserSettingsActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者：Created by 龙啸天 on 2016/6/29 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 */
public class MyFragment extends Fragment {
    @BindView(R.id.civ_avatar)
    CircleImageView mAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.iv_my_message)
    ImageView mIvMessage;
    @BindView(R.id.iv_settings)
    ImageView mIvSetting;

    private boolean isLogin = false;

    private String userSex;
    private String userNickname;//用户昵称
    private String autograph; //个性签名
    private String userName;  //用户名
    private String avaUrl;  //用户高清头像
    private String avaMinUrl;//用户小头像
    public static String mUserID; //用户objectid

    private TextView mTextView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);

        ButterKnife.bind(this, view);

        if (isUserTableExist()) {
            getUserData();
            isLogin = true;
        }

        mTextView = (TextView) view.findViewById(R.id.ceshi);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LogisticsInformation.class);
                startActivity(intent);
            }
        });

        return view;

    }


    @OnClick({R.id.civ_avatar, R.id.tv_name, R.id.iv_my_message, R.id.iv_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_avatar:
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), UserSettingsActivity.class);
                    intent.putExtra("user_sex", userSex);
                    intent.putExtra("user_nickname", userNickname);
                    intent.putExtra("user_name", userName);
                    intent.putExtra("autograph", autograph);
                    intent.putExtra("avaUrl", avaUrl);
                    intent.putExtra("avaMinUrl", avaMinUrl);
                    startActivityForResult(intent, 3);
                    break;
                }
            case R.id.tv_name:
                if (!isUserTableExist()) {
                    if (!isLogin) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    Toast.makeText(getActivity(), "用户已经登陆", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_my_message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_settings:
                Intent intent1 = new Intent(getActivity(), MySetActivity.class);
                intent1.putExtra("islogin", isLogin);
                startActivityForResult(intent1, 2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //登陆界面返回数据
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    mUserID = data.getStringExtra("objectId");
                    isLogin = true;
                    getUserData();
                    ((MyApplication) getActivity().getApplication()).setObjectID(mUserID);
                    Log.i("lxc", "onActivityResult: " + mUserID);
                }
                break;
            //应用设置界面返回数据
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    isLogin = false;
                    Log.i("settings", "设置返回");
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
                }
                break;
            case 3:
                getUserData();
                break;
        }
    }

    /**
     * 判断本地用户表是否存在
     *
     * @return
     */
    private boolean isUserTableExist() {
        boolean isTableExist = true;
        String sqlCmd = "SELECT count(User_Avatar) FROM User_Profile ";
        UserDao ud = new UserDao(getActivity());
        Cursor c = ud.query(sqlCmd);
        if (c.moveToNext()) {
            if (c.getInt(0) == 0) {
                isTableExist = false;
            }
        }
        c.close();
        return isTableExist;
    }

    /**
     * 读取本地数据库数据 （用户默认头像和签名）
     * <p/>
     * 用户唯一id（objectid）
     */
    private void getUserData() {
        String sqlCmd = "SELECT * FROM User_Profile ";
        UserDao ud = new UserDao(getActivity());
        Cursor cursor = ud.query(sqlCmd);
        userName = "";
        userNickname = "";
        avaUrl = "";
        autograph = "";
        if (cursor.moveToFirst()) {
            mUserID = cursor.getString(cursor.getColumnIndex("objectId"));
            userSex = cursor.getString(cursor.getColumnIndex("User_sex"));
            userName = cursor.getString(cursor.getColumnIndex("User_Name"));
            userNickname = cursor.getString(cursor.getColumnIndex("User_Nickname"));
            autograph = cursor.getString(cursor.getColumnIndex("User_Autograph"));
            avaUrl = cursor.getString(cursor.getColumnIndex("User_Avatar"));
            avaMinUrl = cursor.getString(cursor.getColumnIndex("User_image_min"));
            //   Log.i("ud", avaUrl);
        }
        cursor.close();
        mTvName.setBackgroundResource(R.color.color_FMy_Context);
        mTvName.setTextColor(getResources().getColor(R.color.colorPrimary));
        mTvName.setText(userNickname);
        Picasso.with(getActivity()).load(avaUrl).into(mAvatar);
    }
}