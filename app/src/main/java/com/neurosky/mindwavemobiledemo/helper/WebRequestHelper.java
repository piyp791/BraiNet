package com.neurosky.mindwavemobiledemo.helper;

import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by peps on 6/18/17.
 */

public class WebRequestHelper {

    private static WebRequestHelper webRequestHelper = null;

    JSONObject idValidationResponse = null;

    public static WebRequestHelper getInstance(){
        if (webRequestHelper ==null){
            webRequestHelper = new WebRequestHelper();
        }
        return webRequestHelper;
    }

    private WebRequestHelper(){

    }

    private static final String BASE_URL = Constants.HOST;


    private static SyncHttpClient client = new SyncHttpClient();


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setResponseTimeout(Constants.TIMEOUT_TIME);
        client.setTimeout(Constants.TIMEOUT_TIME);
        client.setConnectTimeout(Constants.TIMEOUT_TIME);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d(Constants.CUSTOM_LOG_TYPE, BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }
}