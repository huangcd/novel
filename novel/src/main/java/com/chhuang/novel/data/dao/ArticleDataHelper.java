package com.chhuang.novel.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.chhuang.novel.data.Article;

import java.text.MessageFormat;
import java.util.List;

import static com.chhuang.novel.data.dao.ArticleInfo.*;

/**
 * Date: 2014/5/28
 * Time: 18:32
 * @author chhuang@microsoft.com
 */
public class ArticleDataHelper extends BaseModelHelper<Article> {

    public static final String ARTICLE_CONTENT_URL_STRING = MessageFormat.format("content://{0}/{1}",
                                                                                 DataContentProvider.AUTHORITY,
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
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String url = cursor.getString(cursor.getColumnIndex(URL));
        final byte[] blob = cursor.getBlob(cursor.getColumnIndex(CONTENT));
        String content = blob == null ? null : new String(blob);
        double percentage = cursor.getDouble(cursor.getColumnIndex(PERCENTAGE));
        String bookName = cursor.getString(cursor.getColumnIndex(NOVEL_NAME));
        Article article = new Article(id, title, url);
        article.setContent(content);
        article.setPercentage(percentage);
        article.setBookName(bookName);
        return article;
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
