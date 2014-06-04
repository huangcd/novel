package com.chhuang.novel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.chhuang.novel.data.Article;
import com.chhuang.novel.data.GBKRequest;
import com.chhuang.novel.data.dao.ArticleDataHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class ArticleActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = ArticleActivity.class.getName();
    @InjectView(R.id.layout_article)
    SwipeRefreshLayout layoutArticle;
    @InjectView(R.id.content)
    TextView           contentView;
    @InjectView(R.id.sroll_view_content)
    ScrollView scrollView;
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

        if (TextUtils.isEmpty(article.getContent())) {
            onRefresh();
        } else {
            setText();
        }
    }

    private void setText() {
        contentView.setText(article.getContent());
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                final int y = (int) (article.getPercentage() * contentView.getHeight() - scrollView.getHeight());
                scrollView.scrollTo(0, y);
            }
        });
    }

    @Override
    public void onRefresh() {
        layoutArticle.setRefreshing(true);
        queue.add(new GBKRequest(article.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Element content = document.select("div#content").first();
                StringBuilder buffer = new StringBuilder();
                for (TextNode p : content.textNodes()) {
                    buffer.append(p.getWholeText()).append("\r\n");
                }
                contentView.setText(buffer);
                layoutArticle.setRefreshing(false);
                article.setContent(buffer.toString());
                Uri uri = ArticleDataHelper.getInstance(AppContext.getContext()).insert(article);
                Log.v(TAG, "Article uri: " + uri);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppContext.showToast(ArticleActivity.this, "刷新失败，请稍后重试", Toast.LENGTH_LONG);
                layoutArticle.setRefreshing(false);
            }
        }));
    }

    @Override
    public void onBackPressed() {
        final double percentage = (scrollView.getScrollY() + scrollView.getHeight()) * 1.0 / contentView.getHeight();
        article.setPercentage(percentage);
        ArticleDataHelper.getInstance(AppContext.getContext()).insert(article);
        super.onBackPressed();
    }
}
