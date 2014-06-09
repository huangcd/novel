package com.chhuang.novel.data.sql;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2014/5/27
 * Time: 13:56
 *
 * @author chhuang@microsoft.com
 */
public class SQLiteTable {
    private static final String TAG = SQLiteTable.class.getName();
    private String         tableName;
    private List<Column>   columnDefinitions;
    private List<String[]> uniqueColumns;

    public SQLiteTable(String tableName) {
        this.tableName = tableName;
        columnDefinitions = new ArrayList<Column>();
        columnDefinitions.add(new Column(Column._ID, "PRIMARY KEY", Column.DataType.INTEGER));
        uniqueColumns = new ArrayList<String[]>();
    }

    public SQLiteTable addUniqueColumns(String... columns) {
        uniqueColumns.add(columns);
        return this;
    }

    public SQLiteTable addColumn(String name, Column.DataType type) {
        return addColumn(name, null, type);
    }

    public SQLiteTable addColumn(String name, String constraint, Column.DataType type) {
        return addColumn(new Column(name, constraint, type));
    }

    public SQLiteTable addColumn(Column column) {
        columnDefinitions.add(column);
        return this;
    }

    public void create(SQLiteDatabase db) {
        String formatter = " %s";
        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE IF NOT EXISTS ");
        buffer.append(tableName);
        buffer.append("(");
        int columnCount = columnDefinitions.size();
        int index = 0;
        for (Column columnDefinition : columnDefinitions) {
            buffer.append(columnDefinition.getName()).append(
                    String.format(formatter, columnDefinition.getType().name()));
            String constraint = columnDefinition.getConstraint();

            if (constraint != null) {
                buffer.append(String.format(formatter, constraint));
            }
            if (index < columnCount - 1) {
                buffer.append(",");
            }
            index++;
        }
        for (String[] unique : uniqueColumns) {
            buffer.append(MessageFormat.format(", UNIQUE ({0}) ON CONFLICT REPLACE",
                                                      TextUtils.join(", ", unique)));
        }
        buffer.append(");");
        Log.d(TAG, "Creation: " + buffer);
        db.execSQL(buffer.toString());
    }

    public void delete(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
