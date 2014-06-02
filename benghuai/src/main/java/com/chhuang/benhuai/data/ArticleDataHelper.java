package com.chhuang.benhuai.data;

import android.content.Context;
import android.net.Uri;

/**
 * Created by chhuang on 2014/5/28.
 */
public class ArticleDataHelper extends BaseDataHelper {

    public ArticleDataHelper(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return null;
    }
}
