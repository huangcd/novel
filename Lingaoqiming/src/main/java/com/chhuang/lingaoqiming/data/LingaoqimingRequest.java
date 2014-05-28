package com.chhuang.lingaoqiming.data;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
* Date: 2014/5/26
* Time: 11:10
*
* @author chhuang@microsoft.com
*/
public class LingaoqimingRequest extends StringRequest {
    public LingaoqimingRequest(
            String url,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, "GB2312");
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
