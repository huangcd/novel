package com.chhuang.benhuai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.chhuang.benhuai.data.Article;
import com.chhuang.benhuai.data.GBKRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DirectoryActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String  TAG                  = DirectoryActivity.class.getName();
    public static final Pattern ARTICLE_HREF_PATTERN = Pattern.compile("/5_5133/(\\d+).html", Pattern.CASE_INSENSITIVE);
    public static final String  BASE_URL             = "http://www.biquge.com/5_5133/";
    @InjectView(R.id.layout_titles)
    SwipeRefreshLayout layoutTitles;
    @InjectView(R.id.list_titles)
    ListView           listViewTitles;
    private RequestQueue          requestQueue;
    private ArrayAdapter<Article> articleArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_directory);

        init();
        onRefresh();
    }

    private void init() {
        ButterKnife.inject(this);

        layoutTitles.setOnRefreshListener(this);
        layoutTitles.setColorScheme(android.R.color.holo_blue_bright,
                                    android.R.color.holo_green_light,
                                    android.R.color.holo_orange_light,
                                    android.R.color.holo_red_light);
        requestQueue = Volley.newRequestQueue(this);
        articleArrayAdapter = new ArticleAdapter(this, R.layout.title_item);
        listViewTitles.setAdapter(articleArrayAdapter);
        listViewTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = articleArrayAdapter.getItem(position);
                Intent intent = new Intent(DirectoryActivity.this, ArticleActivity.class).putExtra("article", article);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        layoutTitles.setRefreshing(true);

        requestQueue.add(new GBKRequest(BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                articleArrayAdapter.clear();
                articleArrayAdapter.notifyDataSetChanged();

                new AsyncTask<String, Article, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        if (params.length == 0) {
                            return null;
                        }
                        String response = params[0];
                        Document document = Jsoup.parse(response);
                        Elements dds = document.select("dd").select("a");
                        for (Element a : dds) {
                            String href = a.attr("href");
                            Matcher matcher = ARTICLE_HREF_PATTERN.matcher(href);
                            if (matcher.matches()) {
                                int articleIndex = Integer.parseInt(matcher.group(1));
                                String title = a.text();
                                String url = BASE_URL + matcher.group(1) + ".html";
                                Article article = new Article(articleIndex, title, url);
                                publishProgress(article);
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        layoutTitles.setRefreshing(false);
                    }

                    @Override
                    protected void onProgressUpdate(Article... articles) {
                        if (articles.length == 0) {
                            return;
                        }
                        Article article = articles[0];
                        articleArrayAdapter.add(article);
                        articleArrayAdapter.notifyDataSetChanged();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
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
            chapterNumber.setText(String.format("%04d", article.getChapterNumber()));
            chapterTitle.setText(article.getTitle());
            return convertView;
        }
    }
}
