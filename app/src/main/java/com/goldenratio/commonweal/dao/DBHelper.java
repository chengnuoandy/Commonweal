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
        super(context,DBNAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句
        String sql="create table user(User_ID int primary key(User_ID),User_Name varchar(20),User_Autograph varchar(20))";
        //执行创建数据库操作
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
