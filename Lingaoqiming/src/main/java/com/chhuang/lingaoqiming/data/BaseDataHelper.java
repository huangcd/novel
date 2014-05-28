package com.chhuang.lingaoqiming.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by chhuang on 2014/5/28.
 */
public abstract class BaseDataHelper {
    private Context context;

    public BaseDataHelper(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected abstract Uri getContentUri();

    public void notifyChange() {
        context.getContentResolver().notifyChange(getContentUri(), null);
    }

    protected final Cursor query(Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    protected final Cursor query(String[] projection, String selection, String[] selectionArgs,
                                 String sortOrder) {
        return context.getContentResolver().query(getContentUri(), projection, selection,
                selectionArgs, sortOrder);
    }

    protected final Uri insert(ContentValues values) {
        return context.getContentResolver().insert(getContentUri(), values);
    }

    protected final int bulkInsert(ContentValues[] values) {
        return context.getContentResolver().bulkInsert(getContentUri(), values);
    }

    protected final int update(ContentValues values, String where, String[] whereArgs) {
        return context.getContentResolver().update(getContentUri(), values, where, whereArgs);
    }

    protected final int delete(Uri uri, String selection, String[] selectionArgs) {
        return context.getContentResolver().delete(getContentUri(), selection, selectionArgs);
    }

    protected final Cursor getList(String[] projection, String selection, String[] selectionArgs,
                                   String sortOrder) {
        return context.getContentResolver().query(getContentUri(), projection, selection,
                selectionArgs, sortOrder);
    }

    public CursorLoader getCursorLoader(Context context) {
        return getCursorLoader(context, null, null, null, null);
    }

    protected final CursorLoader getCursorLoader(Context context, String[] projection,
                                                 String selection, String[] selectionArgs, String sortOrder) {
        return new CursorLoader(context, getContentUri(), projection, selection, selectionArgs,
                sortOrder);
    }

}
