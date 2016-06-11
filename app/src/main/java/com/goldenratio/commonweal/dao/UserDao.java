package com.goldenratio.commonweal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.goldenratio.commonweal.dao.entity.User;

/**
 * Created by Administrator on 2016/6/11.
 */

public class UserDao {

    private SQLiteDatabase db;

    public void UserDao(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        this.db = dbHelper.getReadableDatabase();
    }

    public void insert(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("User_ID", user.getUser_ID());
        contentValues.put("User_Name", user.getUser_ID());
        contentValues.put("User_Autograph", user.getUser_ID());
        db.insert("User",null,contentValues);
    }
}
