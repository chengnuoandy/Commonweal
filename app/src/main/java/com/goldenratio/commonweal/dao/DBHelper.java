package com.goldenratio.commonweal.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kiuber on 2016/6/11.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;//数据库版本号
    private static final String DBNAME = "account.db";//数据库名

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句
        String sql = "CREATE TABLE [User_Profile] ([objectId] VARCHAR(20), [User_Name] VARCHAR(40)," +
                " [User_Autograph] VARCHAR(50) ,[User_Avatar] VARCHAR(50), [User_Nickname] VARCHAR(20)," +
                " [User_Address] VARCHAR(50) ,[User_sex] VARCHAR(10), [User_image_min] VARCHAR(50), [User_image_max] VARCHAR(50)," +
                " CONSTRAINT [] PRIMARY KEY ([objectId]))";
        //执行创建数据库操作
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
