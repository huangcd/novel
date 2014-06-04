package com.chhuang.novel.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Date: 2014/5/28
 * Time: 15:39
 *
 * @author chhuang
 */
public abstract class BaseDataHelper<T> {
    public static final  String                              TAG  = BaseDataHelper.class.getName();
    public static final  Gson                                GSON = new GsonBuilder().create();
    private static final HashMap<Class<?>, ArrayList<Field>> annotatedFieldMap
                                                                  = new HashMap<Class<?>, ArrayList<Field>>();
    private Context context;

    public BaseDataHelper(Context context) {
        this.context = context;
    }

    public static <T> T fromContentValues(Cursor cursor, Class<T> klass) {
        T t;
        try {
            t = klass.newInstance();
        } catch (Exception e) {
            Log.w(TAG, Log.getStackTraceString(e));
            return null;
        }
        ArrayList<Field> annotatedFields = getAnnotatedFields(klass);
        for (Field field : annotatedFields) {
            Class fieldClass = field.getType();
            String key = field.getAnnotation(ContentKey.class).key();
            try {
                if (fieldClass.equals(String.class)) {
                    field.set(t, cursor.getString(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(Long.class)) {
                    field.setLong(t, cursor.getLong(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(Short.class)) {
                    field.setShort(t, cursor.getShort(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(Integer.class)) {
                    field.setInt(t, cursor.getInt(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(Byte.class)) {
                    field.setByte(t, (byte) cursor.getInt(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(Float.class)) {
                    field.setFloat(t, cursor.getFloat(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(Double.class)) {
                    field.setDouble(t, cursor.getDouble(cursor.getColumnIndex(key)));
                } else if (fieldClass.equals(byte[].class)) {
                    field.set(t, cursor.getString(cursor.getColumnIndex(key)));
                } else {
                    field.set(t, GSON.fromJson(cursor.getString(cursor.getColumnIndex(key)), fieldClass));
                }
            } catch (IllegalAccessException e) {
                Log.w(TAG, Log.getStackTraceString(e));
            }
        }
        return t;
    }

    protected static ArrayList<Field> getAnnotatedFields(Class<?> klass) {
        ArrayList<Field> fields;
        if (!annotatedFieldMap.containsKey(klass)) {
            fields = new ArrayList<Field>();
            for (Field field : klass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ContentKey.class)) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            annotatedFieldMap.put(klass, fields);
            return fields;
        }
        return annotatedFieldMap.get(klass);
    }

    public Context getContext() {
        return context;
    }

    public ContentValues getContentValue(T t) {
        Class<?> klass = t.getClass();
        ArrayList<Field> annotatedFields = getAnnotatedFields(klass);
        ContentValues values = new ContentValues(annotatedFields.size());
        for (Field field : annotatedFields) {
            String key = field.getAnnotation(ContentKey.class).key();
            try {
                Object value = field.get(t);
                if (value instanceof String) {
                    values.put(key, (String) value);
                } else if (value instanceof Long) {
                    values.put(key, (Long) value);
                } else if (value instanceof Integer) {
                    values.put(key, (Integer) value);
                } else if (value instanceof Byte) {
                    values.put(key, (Byte) value);
                } else if (value instanceof Short) {
                    values.put(key, (Short) value);
                } else if (value instanceof Float) {
                    values.put(key, (Float) value);
                } else if (value instanceof Double) {
                    values.put(key, (Double) value);
                } else if (value instanceof byte[]) {
                    values.put(key, (byte[]) value);
                } else {
                    values.put(key, GSON.toJson(value));
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return values;
    }

    public void notifyChange() {
        context.getContentResolver().notifyChange(getContentUri(), null);
    }

    protected abstract Uri getContentUri();

    public final Cursor query(
            Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(uri, projection, selection, selectionArgs,
                                                  sortOrder);
    }

    public final Cursor query(
            String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        return context.getContentResolver().query(getContentUri(), projection, selection,
                                                  selectionArgs, sortOrder);
    }

    public final Uri insert(ContentValues values) {
        return context.getContentResolver().insert(getContentUri(), values);
    }

    public final int bulkInsert(ContentValues[] values) {
        return context.getContentResolver().bulkInsert(getContentUri(), values);
    }

    public final int update(ContentValues values, String where, String[] whereArgs) {
        return context.getContentResolver().update(getContentUri(), values, where, whereArgs);
    }

    public final int delete(String selection, String[] selectionArgs) {
        return context.getContentResolver().delete(getContentUri(), selection, selectionArgs);
    }

    public final Cursor getList(
            String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        return context.getContentResolver().query(getContentUri(), projection, selection,
                                                  selectionArgs, sortOrder);
    }

    public CursorLoader getCursorLoader(Context context) {
        return getCursorLoader(context, null, null, null, null);
    }

    protected final CursorLoader getCursorLoader(
            Context context, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        return new CursorLoader(context, getContentUri(), projection, selection, selectionArgs,
                                sortOrder);
    }

}
