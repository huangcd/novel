package com.chhuang.novel.data.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import com.chhuang.novel.AppContext;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Date: 2014/6/2
 * Time: 13:56
 *
 * @author chhuang@microsoft.com
 */
public class DataProvider extends ContentProvider {
    public static final String AUTHORITY                 = "com.chhuang.novel.provider";
    public static final String ARTICLE_CONTENT_TYPE      = "vnd.android.cursor.dir/" + AUTHORITY + ".article";
    public static final String ARTICLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + ".article";
    public static final    int           ARTICLES                  = 0;
    public static final    int           ARTICLE                   = 1;
    public static final    UriMatcher    URI_MATCHER               = new UriMatcher(UriMatcher.NO_MATCH);
    protected final static ReentrantLock DBLock                    = new ReentrantLock();
    private final static   String        TAG                       = DataProvider.class.getName();
    public static DatabaseHelper DBHelper;

    static {
        URI_MATCHER.addURI(AUTHORITY, ArticleInfo.TABLE_NAME, ARTICLES);
        URI_MATCHER.addURI(AUTHORITY, ArticleInfo.TABLE_NAME + "/#", ARTICLE);
    }

    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(
            Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        DBLock.lock();
        try {
            SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
            String table = matchTable(uri);
            builder.setTables(table);

            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } finally {
            DBLock.unlock();
        }
    }

    public synchronized static DatabaseHelper getDBHelper() {
        if (DBHelper == null) {
            DBHelper = new DatabaseHelper(AppContext.getContext());
        }
        return DBHelper;
    }

    private String matchTable(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ARTICLES:
            case ARTICLE:
                return ArticleInfo.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ARTICLES:
                return ARTICLE_CONTENT_TYPE;
            case ARTICLE:
                return ARTICLE_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        DBLock.lock();
        try {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for (ContentValues value : values) {
                    db.insertWithOnConflict(table, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (Exception ex) {
                Log.e(TAG, Log.getStackTraceString(ex));
            } finally {
                db.endTransaction();
            }
            return values.length;
        } finally {
            DBLock.unlock();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        DBLock.lock();
        try {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, Log.getStackTraceString(ex));
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        } finally {
            DBLock.unlock();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        DBLock.lock();
        try {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count = 0;
            db.beginTransaction();
            try {
                count = db.delete(table, selection, selectionArgs);
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, Log.getStackTraceString(ex));
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } finally {
            DBLock.unlock();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        DBLock.lock();
        try {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count = 0;
            db.beginTransaction();
            try {
                count = db.update(table, values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, Log.getStackTraceString(ex));
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } finally {
            DBLock.unlock();
        }
    }
}
