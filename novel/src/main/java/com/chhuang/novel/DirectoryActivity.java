package com.chhuang.novel;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chhuang.novel.data.Article;
import com.chhuang.novel.data.articles.BenghuaiNovel;
import com.chhuang.novel.data.articles.INovel;
import com.chhuang.novel.data.dao.ArticleDataHelper;
import com.chhuang.novel.data.dao.ArticleInfo;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@ContentView(R.layout.activity_directory)
public class DirectoryActivity extends RoboActivity
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG            = DirectoryActivity.class.getName();
    @InjectView(R.id.layout_drawer)
    DrawerLayout drawerLayout;
    @InjectView(R.id.layout_titles)
    SwipeRefreshLayout layoutTitles;
    @InjectView(R.id.list_titles)
    ListView           listViewTitles;
    @InjectView(R.id.sidebar_list_view)
    ListView     listViewSidebar;
    private SimpleCursorAdapter articleAdapter;
    private INovel novel = new BenghuaiNovel();
    private int lastVisitPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,                       /* host Activity */
                drawerLayout,               /* DrawerLayout object */
                R.drawable.ic_launcher,     /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,       /* "open drawer" description for accessibility */
                R.string.drawer_close       /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        listViewSidebar.setAdapter(new ArrayAdapter<INovel>(this,
                                                            android.R.layout.simple_list_item_1,
                                                            AppContext.registerNovels));
        listViewSidebar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                novel = (INovel) listViewSidebar.getAdapter().getItem(position);
                getLoaderManager().initLoader(novel.hashCode(), null, DirectoryActivity.this);
            }
        });

        getLoaderManager().initLoader(AppContext.registerNovels.get(0).hashCode(), null, this);

        layoutTitles.setOnRefreshListener(this);
        layoutTitles.setColorScheme(android.R.color.holo_blue_bright,
                                    android.R.color.holo_green_light,
                                    android.R.color.holo_orange_light,
                                    android.R.color.holo_red_light);
        articleAdapter = new ArticleCursorAdapter(this,
                                                  R.layout.title_item,
                                                  null,
                                                  new String[0],
                                                  new int[0],
                                                  0);
        listViewTitles.setAdapter(articleAdapter);
        listViewTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter) listViewTitles.getAdapter()).getCursor();
                if (cursor == null) {
                    return;
                }
                cursor.moveToPosition(position);
                lastVisitPosition = position;
                Article article = ArticleDataHelper.fromCursor(cursor);
                Intent intent = new Intent(DirectoryActivity.this, ArticleActivity.class)
                        .putExtra("article", article)
                        .putExtra("novel", novel.getClass().getCanonicalName());
                startActivity(intent);
            }
        });
        registerForContextMenu(listViewTitles);
    }

    @Override
    protected void onPause() {
        getSharedPreferences(TAG, MODE_PRIVATE)
                .edit()
                .putInt("list_selection", lastVisitPosition)
                .commit();
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_titles) {
            menu.setHeaderTitle("下载");
            menu.add(Menu.NONE, 0, 0, "下载本章");
            menu.add(Menu.NONE, 1, 1, "下载之后所有章节");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo contextMenuInfo = item.getMenuInfo();
        if (contextMenuInfo instanceof AdapterView.AdapterContextMenuInfo) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
            Cursor cursor = ((SimpleCursorAdapter) listViewTitles.getAdapter()).getCursor();
            cursor.moveToPosition(info.position);
            switch (item.getItemId()) {
                case 0:
                    singleDownload(cursor);
                    break;
                case 1:
                    do {
                        singleDownload(cursor);
                    } while (cursor.moveToNext());
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void singleDownload(Cursor cursor) {
        final Article article = ArticleDataHelper.fromCursor(cursor);
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String content = novel.parseArticle(response);
                article.setContent(content);
                Uri uri = ArticleDataHelper.getInstance(
                        AppContext.getContext()).insert(article);
                Log.v(TAG, "Article uri: " + uri);
            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppContext.showToast(DirectoryActivity.this, article.getTitle() + " 下载失败，请稍后重试", Toast.LENGTH_LONG);
            }
        };
        AppContext.getContext().getQueue().add(
                novel.getFactory().create(article.getUrl(), responseListener, errorListener));
    }

    private void multipleDownload(Cursor cursor) {
        final List<Article> synchronizedArticleList = Collections.synchronizedList(new ArrayList<Article>());
        AtomicInteger taskCount = new AtomicInteger(0);
        final AtomicInteger finishCount = new AtomicInteger(0);
        do {
            final Article article = ArticleDataHelper.fromCursor(cursor);
            taskCount.getAndIncrement();
            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String content = novel.parseArticle(response);
                    article.setContent(content);
                    synchronizedArticleList.add(article);
                    finishCount.getAndIncrement();
                }
            };
            final Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AppContext.showToast(DirectoryActivity.this, article.getTitle() + " 下载失败，请稍后重试", Toast.LENGTH_LONG);
                    finishCount.getAndIncrement();
                }
            };
            AppContext.getContext().getQueue().add(
                    novel.getFactory().create(article.getUrl(), responseListener, errorListener));
        }
        while (cursor.moveToNext());
        // finishCount.compareAndSet()
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        for (INovel novel : AppContext.registerNovels) {
            if (novel.hashCode() == id) {
                return new CursorLoader(this,
                                        ArticleDataHelper.ARTICLE_CONTENT_URI,
                                        ArticleInfo.PROJECTIONS,
                                        ArticleInfo.NOVEL_NAME + " = ?",
                                        new String[]{novel.getBookName()},
                                        ArticleInfo.ID);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        articleAdapter.changeCursor(data);
        if (data == null || data.getCount() == 0) {
            onRefresh();
        } else {
            final int lastVisitPosition = getSharedPreferences(TAG, MODE_PRIVATE).getInt("list_selection", 0);
            listViewTitles.post(new Runnable() {
                @Override
                public void run() {
                    listViewTitles.setSelection(lastVisitPosition);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        layoutTitles.setRefreshing(true);

        AppContext.getContext().getQueue().add(novel.getFactory().create(novel.getBaseUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Article> articles = novel.parseHomePageToArticles(response);

                ArticleDataHelper.getInstance(AppContext.getContext()).bulkInsert(articles);

                layoutTitles.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppContext.showToast(DirectoryActivity.this, "刷新失败，请稍后重试", Toast.LENGTH_LONG);
                layoutTitles.setRefreshing(false);
            }
        }));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articleAdapter.changeCursor(null);
    }


    private static class ViewHolder {
        private TextView    chapterNumber;
        private TextView    chapterTitle;
        private ImageView   star;
        private ProgressBar progressBar;
    }

    private class ArticleCursorAdapter extends SimpleCursorAdapter {
        private ArticleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.title_item, null);
                holder = new ViewHolder();
                holder.chapterNumber = (TextView) view.findViewById(R.id.text_chapter);
                holder.chapterTitle = (TextView) view.findViewById(R.id.text_title);
                holder.star = (ImageView) view.findViewById(R.id.image_status);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress_article);
                view.setTag(holder);
                Log.v(TAG, String.format("Init view holder (%s, %s, %s)",
                                         holder.chapterNumber,
                                         holder.chapterTitle,
                                         holder.star));
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (holder == null) {
                holder = new ViewHolder();
                holder.chapterNumber = (TextView) view.findViewById(R.id.text_chapter);
                holder.chapterTitle = (TextView) view.findViewById(R.id.text_title);
                holder.star = (ImageView) view.findViewById(R.id.image_status);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress_article);
                Log.v(TAG, String.format("Init view holder (%s, %s, %s), backup",
                                         holder.chapterNumber,
                                         holder.chapterTitle,
                                         holder.star));
                view.setTag(holder);
            }
            Article article = ArticleDataHelper.fromCursor(cursor);
            if (TextUtils.isEmpty(article.getContent())) {
                holder.star.setImageState(new int[]{android.R.attr.state_pressed}, false);
            } else {
                holder.star.setImageState(new int[]{android.R.attr.state_checked, android.R.attr.state_pressed}, false);
            }
            final int progress = (int) (100 * article.getPercentage());
            holder.chapterNumber.setText(String.format("%04d", article.getId()));
            holder.chapterTitle.setText(article.getTitle());
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(progress);
        }
    }
}
