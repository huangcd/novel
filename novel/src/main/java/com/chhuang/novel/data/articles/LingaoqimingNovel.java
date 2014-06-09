package com.chhuang.novel.data.articles;

import com.android.volley.Request;
import com.android.volley.Response;
import com.chhuang.novel.data.Article;
import com.chhuang.novel.data.GBKRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 2014/6/9
 * Time: 22:35
 *
 * @author chhuang@microsoft.com
 */
public class LingaoqimingNovel implements INovel {
    public final static  Pattern              ARTICLE_HREF_PATTERN = Pattern.compile("article/(\\d+).html",
                                                                                     Pattern.CASE_INSENSITIVE);
    public final static  String               BASE_URL             = "http://www.lingaoqiming.com/";
    private final static String               TAG                  = LingaoqimingNovel.class.getName();
    private final static INovelRequestFactory factory              = new INovelRequestFactory() {
        @Override
        public Request<String> create(
                String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
            return new GBKRequest(url, responseListener, errorListener);
        }
    };

    @Override
    public ArrayList<Article> parseHomePageToArticles(String response) {
        ArrayList<Article> articles = new ArrayList<Article>();
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByAttributeValueMatching("href", ARTICLE_HREF_PATTERN);
        for (Element element : elements) {
            String href = element.attr("href");
            Matcher matcher = ARTICLE_HREF_PATTERN.matcher(href);
            if (matcher.matches()) {
                int articleIndex = Integer.parseInt(matcher.group(1));
                String title = element.text();
                String url = BASE_URL + matcher.group();
                Article article = new Article(articleIndex, title, url);
                article.setBookName(getBookName());
                articles.add(article);
            }
        }
        return articles;
    }

    @Override
    public String parseArticle(String response) {
        Document document = Jsoup.parse(response);
        Elements ps = document.select("div.gray14").select("p");
        StringBuilder buffer = new StringBuilder();
        for (Element p : ps) {
            buffer.append(p.text()).append("\r\n");
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        return getBookName();
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getBookName() {
        return "临高启明";
    }

    @Override
    public INovelRequestFactory getFactory() {
        return factory;
    }
}
