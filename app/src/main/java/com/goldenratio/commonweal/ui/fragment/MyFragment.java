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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGridAdapter;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.LoginActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyFragment extends Fragment {
    @BindView(R.id.civ_avatar)
    CircleImageView mAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.gridview)
    GridView gridView;

    private boolean isLogin = false;
    private String mUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);

        ButterKnife.bind(this, view);

        gridView.setAdapter(new MyGridAdapter(getContext()));

        if (isUserTableExist()) {
            getUserData(mUserID);
            isLogin = true;
        }

        return view;
    }


    @OnClick({R.id.civ_avatar, R.id.tv_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_avatar:
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
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    mUserID = data.getStringExtra("objectId");
                    isLogin = true;
                    getUserData(mUserID);
                    Log.i("lxc", "onActivityResult: " + mUserID);

                }
        }
    }

    /**
     * 判断本地用户表是否存在
     *
     * @return
     */
    private boolean isUserTableExist() {
        boolean isTableExist = true;
        String sqlCmd = "SELECT count(User_Avatar) FROM User ";
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
     *
     * @param ID 用户唯一id（objectid）
     */
    private void getUserData(String ID) {
        String sqlCmd = "SELECT User_Autograph,User_Avatar FROM User ";
        UserDao ud = new UserDao(getActivity());
        Cursor cursor = ud.query(sqlCmd);
        String avaUrl = "";
        String autograph = "";
        if (cursor.moveToFirst()) {
            autograph = cursor.getString(cursor.getColumnIndex("User_Autograph"));
            avaUrl = cursor.getString(cursor.getColumnIndex("User_Avatar"));
            Log.i("ud", avaUrl);
        }
        cursor.close();
        mTvName.setText(autograph);
        Picasso.with(getActivity()).load(avaUrl).into(mAvatar);
    }


}