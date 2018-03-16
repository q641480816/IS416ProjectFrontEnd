package com.is416.smujio.util;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.is416.smujio.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Gods on 2/24/2018.
 */

public class General {

    public static int METRIC_HEIGHT;
    public static int METRIC_WIDTH;

    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String baseUrl = "http://www.card-digi.com:8080/Jio/service";

    public static Location myLastLocation;
    public static String token;
    public static String email;
    public static JSONObject user;

    //Keys!!!!!!!!!!!!!!!!!!!!!!!
    public static final String PERSIST = "PERSIST";
    public static final int HTTP_FAIL = 0;
    public static final int HTTP_SUCCESS = 1;
    public static final int HTTP_EXCEPTION = 2;
    public static final int HTTP_AUTH_ERROR = 3;
    public static final String HTTP_STATUS_KEY = "status";
    public static final String HTTP_MESSAGE_KEY = "message";
    public static final String HTTP_EXCEPTION_KEY = "exception";
    public static final String HTTP_DATA_KEY = "data";
    //User
    public static final String USER = "user";
    public static final String ACCOUNTID = "accountId";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String SECRET = "secret";
    public static final String NICKNAME = "nickName";
    public static final String DATEOFBIRTH = "dateOfBirth";
    public static final String GENDER = "gender";
    public static final String DATE = "date";
    public static final String AVATAR = "avatar";

    //Event
    public static final String EVENT = "event";
    public static final String ID = "id";
    public static final String OWNER = "owner";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String INITTIME = "initTime";
    public static final String EVENTSTATUS = "eventStatus";
    public static final String TYPE = "type";
    public static final String PARTICIPANTS = "participants";
    public static final String SOCKETURL = "socketUrl";
    public static final String LOCATION = "location";

    public static final String EVENT_TYPE_MOVIE = "MOVIE";
    public static final String EVENT_TYPE_BAR = "BAR";
    public static final String EVENT_TYPE_DINE = "DINE";

    //Values

    public static final int HTTP_GET = 0;
    public static final int HTTP_POST = 1;
    public static final int HTTP_PUT = 2;
    public static final int EVENT_STATUS_JOINING = 0;
    public static final int EVENT_STATUS_STARTED = 1;
    public static final int EVENT_STATUS_STOPPED = -1;

    public static void makeToast(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    public static void httpRequest(Context mContext, int method, String url, JSONObject body, boolean isHeaderRequired, JsonHttpResponseHandler handler) throws Exception{
        AsyncHttpClient client = new AsyncHttpClient();
        final String f_url = baseUrl + url;
        if (isHeaderRequired){
            //TODO
        }

        ByteArrayEntity entity = null;
        switch (method){
            case HTTP_GET:
                //get
                client.get(f_url, handler);
                break;
            case HTTP_POST:
                //Post
                entity = new ByteArrayEntity(body.toString().getBytes("UTF-8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                client.post(mContext, f_url, entity,"application/json", handler);

                break;
            case HTTP_PUT:
                entity = new ByteArrayEntity(body.toString().getBytes("UTF-8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                client.put(mContext, f_url, entity, "application/json", handler);
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

    public static int convertDpToPixel(int dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = (float) dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

    public static float getMovingDistance(float startY, MotionEvent ev) {
        float dy = (ev.getY(0) - startY);
        return dy;
    }
}
