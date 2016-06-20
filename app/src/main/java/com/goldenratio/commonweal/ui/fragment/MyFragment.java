package com.goldenratio.commonweal.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.goldenratio.commonweal.dao.DBHelper;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.LoginActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFragment extends Fragment {
    private GridView gridView;
    private TextView mAutograph;
    private boolean isLogin = false;
    private CircleImageView mAvatar;
    private String mUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);
        mAvatar = (CircleImageView) view.findViewById(R.id.civ_avatar);
        mAutograph = (TextView) view.findViewById(R.id.tv_name);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new MyGridAdapter(getContext()));

        if (isUserTableExist()) {
            getUserData(mUserID);
            isLogin = true;
        }

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserTableExist()) {
                    if (!isLogin) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    Toast.makeText(getActivity(), "用户已经登陆", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
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
        mAutograph.setText(autograph);
        Picasso.with(getActivity()).load(avaUrl).into(mAvatar);
    }
}