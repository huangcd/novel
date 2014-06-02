package com.chhuang.novel.data.sql;

import android.provider.BaseColumns;

/**
 * Date: 2014/5/27
 * Time: 13:48
 *
 * @author chhuang@microsoft.com
 */
public class Column implements BaseColumns {
    private String name;
    private String constraint;
    private DataType type;

    public Column(String name, String constraint, DataType type) {
        this.name = name;
        this.constraint = constraint;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getConstraint() {
        return constraint;
    }

    public DataType getType() {
        return type;
    }

    public static enum DataType {
        NULL,
        INTEGER,
        REAL,
        TEXT,
        BLOB,
    }
}
