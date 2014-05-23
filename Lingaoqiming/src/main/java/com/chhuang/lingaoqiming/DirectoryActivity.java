package com.chhuang.lingaoqiming;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chhuang.lingaoqiming.data.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DirectoryActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String  TAG                  = DirectoryActivity.class.getName();
    public static final Pattern ARTICLE_HREF_PATTERN = Pattern.compile("article/(\\d+).html", Pattern.CASE_INSENSITIVE);
    @InjectView(R.id.layout_titles)
    SwipeRefreshLayout layoutTitles;
    @InjectView(R.id.list_titles)
    ListView           listViewTitles;
    private RequestQueue          requestQueue;
    private ArrayAdapter<Article> articleArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_directory);

        init();
    }

    private void init() {
        ButterKnife.inject(this);

        layoutTitles.setOnRefreshListener(this);
        requestQueue = Volley.newRequestQueue(this);
        articleArrayAdapter = new ArticleAdapter(this, R.layout.title_item);
        listViewTitles.setAdapter(articleArrayAdapter);
    }

    @Override
    public void onRefresh() {
        layoutTitles.setRefreshing(true);

        requestQueue.add(new StringRequest("http://www.lingaoqiming.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Elements elements = document.getElementsByAttributeValueMatching("href", ARTICLE_HREF_PATTERN);
                Collections.reverse(elements);
                for (Element element : elements) {
                    String href = element.attr("href");
                    Matcher matcher = ARTICLE_HREF_PATTERN.matcher(href);
                    if (matcher.matches()) {
                        int articleIndex = Integer.parseInt(matcher.group(1));
                        String title = element.text();
                        Article article = new Article(articleIndex, title);
                        articleArrayAdapter.add(article);
                        articleArrayAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                layoutTitles.setRefreshing(false);
            }
        }));
    }

    private class ArticleAdapter extends ArrayAdapter<Article> {
        public ArticleAdapter(Context context, int resource) {
            super(context, resource);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.title_item, null);
            }
            Article article = getItem(position);
            TextView chapterNumber = (TextView) convertView.findViewById(R.id.text_chapter);
            TextView chapterTitle = (TextView) convertView.findViewById(R.id.text_title);
            chapterNumber.setText(Integer.toString(article.getChapterNumber()));
            chapterTitle.setText(article.getTitle());
            return convertView;
        }
    }
}
