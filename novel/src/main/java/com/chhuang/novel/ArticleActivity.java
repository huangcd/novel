package com.chhuang.novel;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chhuang.novel.data.Article;
import com.chhuang.novel.data.GBKRequest;
import com.chhuang.novel.data.articles.INovel;
import com.chhuang.novel.data.dao.ArticleDataHelper;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_article)
public class ArticleActivity extends RoboActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = ArticleActivity.class.getName();
    @InjectView(R.id.layout_article)
    SwipeRefreshLayout layoutArticle;
    @InjectView(R.id.content)
    TextView           contentView;
    @InjectView(R.id.sroll_view_content)
    ScrollView         scrollView;
    private Article article;
    private INovel  novel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        layoutArticle.setColorScheme(android.R.color.holo_blue_bright,
                                     android.R.color.holo_green_light,
                                     android.R.color.holo_orange_light,
                                     android.R.color.holo_red_light);
        layoutArticle.setOnRefreshListener(this);
        Intent intent = getIntent();
        article = intent.getParcelableExtra("article");
        try {
            novel = (INovel) Class.forName(intent.getStringExtra("novel")).newInstance();
        } catch (Exception e) {
            Log.w(TAG, "Failed to create INovel instance", e);
            finish();
        }

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
        final AppContext context = AppContext.getContext();
        context.getQueue().add(new GBKRequest(article.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String content = novel.parseArticle(response);
                contentView.setText(content);
                layoutArticle.setRefreshing(false);
                article.setContent(content);
                Uri uri = ArticleDataHelper.getInstance(context).insert(article);
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
