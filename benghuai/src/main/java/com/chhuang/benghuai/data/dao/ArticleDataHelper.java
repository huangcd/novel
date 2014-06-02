package com.chhuang.benghuai.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.chhuang.benghuai.data.Article;

import java.text.MessageFormat;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.chhuang.benghuai.data.dao.DatabaseHelper.ArticleInfo.*;

/**
 * Created by chhuang on 2014/5/28.
 */
public class ArticleDataHelper extends BaseDataHelper<Article> {

    public static final String ARTICLE_CONTENT_URL_STRING = MessageFormat.format("content://{0}/{1}",
                                                                                 DataProvider.AUTHORITY,
                                                                                 TABLE_NAME);
    public static final Uri    ARTICLE_CONTENT_URI        = Uri.parse(ARTICLE_CONTENT_URL_STRING);

    private static ArticleDataHelper instance;

    public synchronized static ArticleDataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ArticleDataHelper(context.getApplicationContext());
        }
        return instance;
    }

    private ArticleDataHelper(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return ARTICLE_CONTENT_URI;
    }

    public static Article fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(_ID));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String url = cursor.getString(cursor.getColumnIndex(URL));
        byte[] blob = cursor.getBlob(cursor.getColumnIndex(CONTENT));
        String content = blob == null ? null : new String(blob);
        Article article = new Article(id, title, url);
        article.setContent(content);
        return article;
    }

    public void bulkInsert(List<Article> articles) {
        int size = articles.size();
        ContentValues[] contentValues = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            contentValues[i] = getContentValue(articles.get(i));
        }
        bulkInsert(contentValues);
    }

    public Uri insert(Article article) {
        ContentValues values = getContentValue(article);
        return insert(values);
    }
}
