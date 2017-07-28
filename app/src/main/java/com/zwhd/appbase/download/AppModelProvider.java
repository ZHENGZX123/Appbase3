package com.zwhd.appbase.download;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * APP下载提供者
 * <p/>
 * Created by YI on 2015/12/9.
 */
public class AppModelProvider extends ContentProvider {
    /**
     * 数据库对象
     */
    private DBHelper dbHelper;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int APP_ITEMS = 1;
    private static final int APP_ITEM = 2;
    private static final int UPDATE_COUNT = 3;
    private static final String AUTHOR = "com.zwhd.app";
    /**
     * 多个处理URI
     */
    public static String APPS_URI = "content://" + AUTHOR + "/app";
    /**
     * 单个处理URI
     */
    public static String APP_URI = "content://" + AUTHOR + "/app/#";
    /**
     * 更新数量URI
     */
    public static String UPDATE_COUNT_URI = "content://" + AUTHOR + "/downloadcount";

    static {
        MATCHER.addURI(AUTHOR, "app", APP_ITEMS);
        MATCHER.addURI(AUTHOR, "app/#", APP_ITEM);
        MATCHER.addURI(AUTHOR, "downloadcount", UPDATE_COUNT);
    }

    public AppModelProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {

        switch (MATCHER.match(uri)) {
            case APP_ITEMS:
                return "vnd.android.cursor.dir/app";
            case APP_ITEM:
                return "vnd.android.cursor.item/app";
            case UPDATE_COUNT:
                return "vnd.android.cursor.dir/downloadcount";
        }
        return null;
    }


    /**
     * 查
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (MATCHER.match(uri)) {
            case APP_ITEMS:
                return db.query(DBHelper.APPS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case APP_ITEM:
                long id = ContentUris.parseId(uri);
                String where = "_id=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                return db.query(DBHelper.APPS_TABLE, projection, where, selectionArgs, null, null, sortOrder);
        }
        return null;
    }


    /**
     * 增
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case APP_ITEMS:
                long rowid = db.insert(DBHelper.APPS_TABLE, null, values);
                Uri insertUri = ContentUris.withAppendedId(uri, rowid);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return insertUri;
        }
        return null;
    }

    /**
     * 删
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (MATCHER.match(uri)) {
            case APP_ITEMS:
                count = db.delete(DBHelper.APPS_TABLE, selection, selectionArgs);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case APP_ITEM:
                long id = ContentUris.parseId(uri);
                String where = "_id=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.delete(DBHelper.APPS_TABLE, where, selectionArgs);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return count;
        }
        return 0;
    }

    /**
     * 更
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (MATCHER.match(uri)) {
            case APP_ITEMS:
                count = db.update(DBHelper.APPS_TABLE, values, selection, selectionArgs);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case APP_ITEM:
                long id = ContentUris.parseId(uri);
                String where = "_id=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.update(DBHelper.APPS_TABLE, values, where, selectionArgs);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return count;

        }
        return 0;
    }
}
