package com.zwhd.appbase.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 下载数据库
 * <p/>
 * Created by YI on 2015/12/7.
 */
public class DBHelper extends SQLiteOpenHelper {
    /**
     * 数据库名称
     */
    final static String DB_NAME = "download_db";
    /**
     * 数据库版本
     */
    final static int DB_VERSION = 2;
    /**
     * 下载数据表
     */
    final static String DOWNLOAD_TABLE = "download_table";
    /**
     * 应用数据表
     */
    public static final String APPS_TABLE = "apps_table";
    /**
     * 分类数据表
     */
    public static final String CATE_TABLE = "cate_table";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "
                + DOWNLOAD_TABLE +
                " (_id integer primary key autoincrement,_name text,_url text,_path text,_dsize integer,_size integer,_status integer,_description text)");
        db.execSQL("create table if not exists "
                + APPS_TABLE +
                " (_id integer primary key autoincrement,_aId integer,_aCateId integer,_aVersion integer,_aVersionName text,_aUpdateTime text,_aSize text," +
                "_aContainSystem text,_aLogo text,_aGrade text,_aRecomment text,_aName text,_aComments text,_aVisitors text,_aDown text,_aDesc text," +
                "_aDwonloadUrl text,_packageName text,_savePath text,_active integer,_downloadSize integer,_appSize integer,_downloadStatus integer,_openway integer,_aTitle text,_advurl text)");
        db.execSQL("create table if not exists "
                + CATE_TABLE +
                "(_id integer primary key autoincrement,_cId integer,_cName text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DOWNLOAD_TABLE);
        db.execSQL("drop table if exists " + APPS_TABLE);
        onCreate(db);
    }
}
