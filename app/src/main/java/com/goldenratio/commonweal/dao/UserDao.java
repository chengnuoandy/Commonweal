package com.goldenratio.commonweal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goldenratio.commonweal.dao.entity.User;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class UserDao {

    private SQLiteDatabase db;

    public UserDao(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        this.db = dbHelper.getReadableDatabase();
    }

    public void execSQL(String sql) {
        db.execSQL(sql);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        db.execSQL(sql, bindArgs);
    }

    public Cursor query(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
}
