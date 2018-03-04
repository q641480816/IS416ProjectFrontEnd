package com.is416.smujio.util;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.is416.smujio.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Gods on 2/24/2018.
 */

public class General {

    private static final String baseUrl = "http://www.card-digi.com:8080/Jio/service";

    public static Location myLastLocation;
    public static String token;
    public static String email;
    public static JSONObject user;

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PERSIST = "PERSIST";
    public static final int HTTP_FAIL = 0;
    public static final int HTTP_SUCCESS = 1;
    public static final int HTTP_EXCEPTION = 2;
    public static final int HTTP_AUTH_ERROR = 3;
    public static final String HTTP_STATUS_KEY = "status";
    public static final String HTTP_MESSAGE_KEY = "message";
    public static final String HTTP_EXCEPTION_KEY = "exception";
    public static final String HTTP_DATA_KEY = "data";
    public static final int HTTP_GET = 0;
    public static final int HTTP_POST = 1;

    public static final String EVENT_TYPE_MOVIE = "MOVIE";
    public static final String EVENT_TYPE_BAR = "BAR";
    public static final String EVENT_TYPE_DINE = "DINE";

    public static void makeToast(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    public static void httpRequest(Context mContext, int method, String url, JSONObject body, boolean isHeaderRequired, JsonHttpResponseHandler handler) throws Exception{
        AsyncHttpClient client = new AsyncHttpClient();
        final String f_url = baseUrl + url;
        if (isHeaderRequired){
            //TODO
        }

        switch (method){
            case HTTP_GET:
                //get
                break;
            case HTTP_POST:
                //Post
                ByteArrayEntity entity = null;
                entity = new ByteArrayEntity(body.toString().getBytes("UTF-8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                client.post(mContext, f_url, entity,"application/json", handler);

                break;
        }

    }

    public static int getMarker(String type){
        int id = 0;
        switch (type){
            case EVENT_TYPE_MOVIE:
                id = R.mipmap.movie;
                break;
            case EVENT_TYPE_BAR:
                id = R.mipmap.bar;
                break;
            case EVENT_TYPE_DINE:
                id = R.mipmap.dine;
                break;
        }

        return id;
    }

}
