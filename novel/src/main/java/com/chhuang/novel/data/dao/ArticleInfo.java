package com.chhuang.novel.data.dao;

import android.provider.BaseColumns;
import com.chhuang.novel.data.sql.SQLiteTable;

import static com.chhuang.novel.data.sql.Column.DataType.*;

/**
 * Date: 2014/6/4
 * Time: 15:29
 *
 * @author chhuang
 */
public class ArticleInfo implements BaseColumns {
    public static final String      TABLE_NAME  = "articles";
    public static final String      NOVEL_NAME  = "novel_name";
    public static final String      TITLE       = "title";
    public static final String      CONTENT     = "content";
    public static final String      PERCENTAGE  = "percentage";
    public static final String      URL         = "url";
    public static final String      ID          = "id";
    public static final String      TIME        = "time";
    public static final SQLiteTable TABLE       = new SQLiteTable(TABLE_NAME)
            .addColumn(NOVEL_NAME, TEXT)
            .addColumn(ID, INTEGER)
            .addColumn(PERCENTAGE, REAL)
            .addColumn(TITLE, TEXT)
            .addColumn(CONTENT, BLOB)
            .addColumn(URL, TEXT)
            .addColumn(TIME, INTEGER)
            .addUniqueColumns(NOVEL_NAME, ID);
    public static final String[] PROJECTIONS = new String[]{
            _ID, NOVEL_NAME, ID, PERCENTAGE, TITLE, CONTENT, URL, TIME};

    ArticleInfo() {
    }
}
