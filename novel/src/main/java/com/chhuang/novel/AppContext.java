package com.chhuang.novel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Date: 2014/6/2
 * Time: 14:38
 *
 * @author chhuang@microsoft.com
 */
public class AppContext extends Application {
    private static Context context;

    public static void showToast(final Activity activity, final String content, final int length) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, content, length).show();
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
