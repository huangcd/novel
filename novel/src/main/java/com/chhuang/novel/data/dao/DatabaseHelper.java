package com.chhuang.novel.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.chhuang.novel.data.sql.Column;
import com.chhuang.novel.data.sql.SQLiteTable;

/**
 * Date: 2014/5/27
 * Time: 13:24
 *
 * @author chhuang@microsoft.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final  String DB_NAME = "article.db";
    public static final  int    VERSION = 1;
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
    }

    public static class ArticleInfo implements BaseColumns {
        public static final String      TABLE_NAME  = "articles";
        public static final String      TITLE       = "title";
        public static final String      CONTENT     = "content";
        public static final String      OFFSET      = "offset";
        public static final String      URL         = "url";
        public static final String      TIME        = "time";
        public static final String      HAS_READ    = "has_read";
        public static final String[]    PROJECTIONS = new String[]{
                _ID, OFFSET, HAS_READ, TITLE, CONTENT, URL, TIME};
        public static final SQLiteTable TABLE       = new SQLiteTable(TABLE_NAME)
                .addColumn(OFFSET, Column.DataType.INTEGER)
                .addColumn(HAS_READ, Column.DataType.INTEGER)
                .addColumn(TITLE, Column.DataType.TEXT)
                .addColumn(CONTENT, Column.DataType.BLOB)
                .addColumn(URL, Column.DataType.TEXT)
                .addColumn(TIME, Column.DataType.INTEGER);

        private ArticleInfo() {
        }
    }
}
