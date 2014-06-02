package com.chhuang.novel.data.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2014/5/27
 * Time: 13:56
 *
 * @author chhuang@microsoft.com
 */
public class SQLiteTable {
    private String       tableName;
    private List<Column> columnDefinitions;

    public SQLiteTable(String tableName) {
        this.tableName = tableName;
        columnDefinitions = new ArrayList<Column>();
        columnDefinitions.add(new Column(Column._ID, "PRIMARY KEY", Column.DataType.INTEGER));
    }

    public SQLiteTable addColumn(Column column) {
        columnDefinitions.add(column);
        return this;
    }

    public SQLiteTable addColumn(String name, String constraint, Column.DataType type) {
        return addColumn(new Column(name, constraint, type));
    }

    public SQLiteTable addColumn(String name, Column.DataType type) {
        return addColumn(name, null, type);
    }

    public void create(SQLiteDatabase db) {
        String formatter = " %s";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS ");
        stringBuilder.append(tableName);
        stringBuilder.append("(");
        int columnCount = columnDefinitions.size();
        int index = 0;
        for (Column columnDefinition : columnDefinitions) {
            stringBuilder.append(columnDefinition.getName()).append(
                    String.format(formatter, columnDefinition.getType().name()));
            String constraint = columnDefinition.getConstraint();

            if (constraint != null) {
                stringBuilder.append(String.format(formatter, constraint));
            }
            if (index < columnCount - 1) {
                stringBuilder.append(",");
            }
            index++;
        }
        stringBuilder.append(");");
        db.execSQL(stringBuilder.toString());
    }

    public void delete(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
