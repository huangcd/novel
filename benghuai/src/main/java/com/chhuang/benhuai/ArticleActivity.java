package com.chhuang.benhuai;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
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
import org.jsoup.nodes.TextNode;

public class ArticleActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.layout_article)
    SwipeRefreshLayout layoutArticle;
    @InjectView(R.id.content)
    TextView           contentView;
    private Article      article;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_article);

        init();
    }

    private void init() {
        ButterKnife.inject(this);

        layoutArticle.setColorScheme(android.R.color.holo_blue_bright,
                                     android.R.color.holo_green_light,
                                     android.R.color.holo_orange_light,
                                     android.R.color.holo_red_light);
        layoutArticle.setOnRefreshListener(this);
        Intent intent = getIntent();
        article = intent.getParcelableExtra("article");
        queue = Volley.newRequestQueue(this);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        layoutArticle.setRefreshing(true);
        queue.add(new GBKRequest(article.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        if (params.length == 0) {
                            return "";
                        }
                        Document document = Jsoup.parse(params[0]);
                        Element content = document.select("div#content").first();
                        StringBuilder buffer = new StringBuilder();
                        for (TextNode p : content.textNodes()) {
                            buffer.append(p.getWholeText()).append("\r\n");
                        }
                        return buffer.toString();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        contentView.setText(s);
                        layoutArticle.setRefreshing(false);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                layoutArticle.setRefreshing(false);
            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
