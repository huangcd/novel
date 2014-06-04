package com.chhuang.novel.data.dao;

import android.provider.BaseColumns;
import com.chhuang.novel.data.sql.Column;
import com.chhuang.novel.data.sql.SQLiteTable;

/**
 * Date: 2014/6/4
 * Time: 15:29
 *
 * @author chhuang
 */
public class ArticleInfo implements BaseColumns {
    public static final String      TABLE_NAME  = "articles";
    public static final String      TITLE       = "title";
    public static final String      CONTENT     = "content";
    public static final String      PERCENTAGE  = "percentage";
    public static final String      URL         = "url";
    public static final String      TIME        = "time";
    public static final String      HAS_READ    = "has_read";
    public static final SQLiteTable TABLE       = new SQLiteTable(TABLE_NAME)
            .addColumn(PERCENTAGE, Column.DataType.REAL)
            .addColumn(HAS_READ, Column.DataType.INTEGER)
            .addColumn(TITLE, Column.DataType.TEXT)
            .addColumn(CONTENT, Column.DataType.BLOB)
            .addColumn(URL, Column.DataType.TEXT)
            .addColumn(TIME, Column.DataType.INTEGER);
    public static final String[]    PROJECTIONS = new String[]{
            _ID, PERCENTAGE, HAS_READ, TITLE, CONTENT, URL, TIME};

    ArticleInfo() {
    }
}
