package com.chhuang.lingaoqiming.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTableLockedException;
import android.provider.BaseColumns;

/**
 * Date: 2014/5/27
 * Time: 13:24
 *
 * @author chhuang@microsoft.com
 */
public class ArticleDatabaseHelper extends SQLiteOpenHelper {
    public static final  String DB_NAME = "article.db";
    public static final  int    VERSION = 1;
    private final static String TAG     = ArticleDatabaseHelper.class.getName();

    public ArticleDatabaseHelper(
            Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ArticleInfo.TABLE.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static class ArticleInfo implements BaseColumns {
        private ArticleInfo() {
        }

        public static final String TABLE_NAME = "articles";
        public static final String ID         = "id";
        public static final String TITLE      = "title";
        public static final String CONTENT    = "content";
        public static final String TIME       = "time";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME).addColumn(ID, Column.DataType.INTEGER)
                                                                           .addColumn(TITLE, Column.DataType.TEXT)
                                                                           .addColumn(CONTENT, Column.DataType.BLOB)
                                                                           .addColumn(TIME, Column.DataType.INTEGER);

    }
}
