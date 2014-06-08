package com.chhuang.novel.data.articles;

import com.chhuang.novel.data.Article;
import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 2014/6/5
 * Time: 22:26
 *
 * @author chhuang@microsoft.com
 */
@Singleton
public class BenghuaiNovel implements INovel {
    public static final  Pattern ARTICLE_HREF_PATTERN = Pattern.compile("/5_5133/(\\d+).html",
                                                                        Pattern.CASE_INSENSITIVE);
    private static final String BASE_URL = "http://www.biquge.com/5_5133/";
    private final static String TAG      = BenghuaiNovel.class.getName();

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getBookName() {
        return "崩坏世界的传奇大冒险";
    }

    @Override
    public ArrayList<Article> parseHomePageToArticles(String response) {
        ArrayList<Article> articles = new ArrayList<Article>();
        Document document = Jsoup.parse(response);
        Elements dds = document.select("dd").select("a");
        for (Element a : dds) {
            String href = a.attr("href");
            Matcher matcher = ARTICLE_HREF_PATTERN.matcher(href);
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                String title = a.text();
                String url = BASE_URL + id + ".html";
                Article article = new Article(id, title, url);
                articles.add(article);
            }
        }
        return articles;
    }

    @Override
    public String parseArticle(String response) {
        Document document = Jsoup.parse(response);
        Element content = document.select("div#content").first();
        StringBuilder buffer = new StringBuilder();
        for (TextNode p : content.textNodes()) {
            buffer.append(p.getWholeText()).append("\r\n");
        }
        return buffer.toString();
    }
}
