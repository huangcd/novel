package com.chhuang.novel.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.chhuang.novel.data.Article;

import java.text.MessageFormat;
import java.util.List;

import static com.chhuang.novel.data.dao.ArticleInfo.TABLE_NAME;

/**
 * Created by chhuang on 2014/5/28.
 */
public class ArticleDataHelper extends BaseDataHelper<Article> {

    public static final String ARTICLE_CONTENT_URL_STRING = MessageFormat.format("content://{0}/{1}",
                                                                                 DataProvider.AUTHORITY,
                                                                                 TABLE_NAME);
    public static final Uri    ARTICLE_CONTENT_URI        = Uri.parse(ARTICLE_CONTENT_URL_STRING);

    private static ArticleDataHelper instance;

    private ArticleDataHelper(Context context) {
        super(context);
    }

    public synchronized static ArticleDataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ArticleDataHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static Article fromCursor(Cursor cursor) {
        return fromContentValues(cursor, Article.class);
    }

    @Override
    protected Uri getContentUri() {
        return ARTICLE_CONTENT_URI;
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
