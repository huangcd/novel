package com.chhuang.novel;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chhuang.novel.data.articles.BenghuaiNovel;
import com.chhuang.novel.data.articles.INovel;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2014/6/2
 * Time: 14:38
 *
 * @author chhuang@microsoft.com
 */
public class AppContext extends Application {
    public static final List<INovel> registerNovels = new ArrayList<INovel>() {
        {
            add(new BenghuaiNovel());
        }
    };
    private static AppContext   context;
    private        RequestQueue queue;

    public static void showToast(final Activity activity, final String content, final int length) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, content, length).show();
            }
        });
    }

    public static AppContext getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = (AppContext) getApplicationContext();
        queue = Volley.newRequestQueue(this);
    }

    public RequestQueue getQueue() {
        return queue;
    }
}
