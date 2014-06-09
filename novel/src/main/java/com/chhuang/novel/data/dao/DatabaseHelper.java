package com.chhuang.novel.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Date: 2014/5/27
 * Time: 13:24
 *
 * @author chhuang@microsoft.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final  String DB_NAME = "article.db";
    public static final int VERSION = 3;
    private final static String TAG     = DatabaseHelper.class.getName();

    public DatabaseHelper(
            Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ArticleInfo.TABLE.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ArticleInfo.TABLE.delete(db);
        ArticleInfo.TABLE.create(db);
    }
}
