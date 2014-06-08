package com.chhuang.novel.data.articles;

import com.android.volley.Request;
import com.android.volley.Response;
import com.chhuang.novel.data.Article;

import java.util.ArrayList;

/**
 * Date: 2014/6/5
 * Time: 22:25
 *
 * @author chhuang@microsoft.com
 */
public interface INovel {
    ArrayList<Article> parseHomePageToArticles(String response);

    String parseArticle(String response);

    String getBaseUrl();

    String getBookName();

    INovelRequestFactory getFactory();

    public interface INovelRequestFactory {
        Request<String> create(
                String url,
                Response.Listener<String> responseListener,
                Response.ErrorListener errorListener);
    }
}
