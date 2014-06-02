package com.chhuang.benghuai.data.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Date: 2014/6/2
 * Time: 15:16
 *
 * @author chhuang@microsoft.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentKey {
    String key();
}
