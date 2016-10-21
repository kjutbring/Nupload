package com.nupd.kittyj.nupload;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by kittyj on 10/20/16.
 */

public class NupClient {

    private static final String NUP_URL = "https://nup.pw";

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static void get(String url, RequestParams requestParams,
                                AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.get(getAbsoluteUrl(url), requestParams, asyncHttpResponseHandler);
    }

    public static void post(String url, RequestParams requestParams,
                                AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.post(getAbsoluteUrl(url), requestParams, asyncHttpResponseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return NUP_URL + relativeUrl;
    }
}
