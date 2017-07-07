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

    private static final String BASE_URL = "http://192.168.0.15:5000";


    private static SyncHttpClient client = new SyncHttpClient();


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setResponseTimeout(30000);
        client.setTimeout(30000);
        client.setConnectTimeout(30000);
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