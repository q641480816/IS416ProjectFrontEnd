package com.is416.smujio.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.is416.smujio.R;
import com.is416.smujio.model.Event;
import com.is416.smujio.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public static User user;
    public static Event currentEvent;
    public static boolean isForeground = false;
    public static Handler UIHandler = new Handler();

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

    //action
    public static final String LEAVE = "leave";
    public static final String SHAKE = "shake";

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
    public static final String USERSTATUS = "userStatus";

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
    public static final String SIZELIMIT = "sizeLimit";
    public static final String LASTCALL = "lastCall";

    public static final String EVENT_TYPE_MOVIE = "MOVIE";
    public static final String EVENT_TYPE_DRINK = "DRINK";
    public static final String EVENT_TYPE_DINE = "DINE";
    public static final String EVENT_TYPE_DATE = "DATE";
    public static final String EVENT_TYPE_PROJECT = "PROJECT";
    public static final String EVENT_TYPE_STUDY = "STUDY";
    //Values

    public static final int HTTP_GET = 0;
    public static final int HTTP_POST = 1;
    public static final int HTTP_PUT = 2;
    public static final int EVENT_STATUS_JOINING = 0;
    public static final int EVENT_STATUS_STARTED = 1;
    public static final int EVENT_STATUS_STOPPED = -1;
    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int USER_NOT_IN_EVENT = -1;

    //socket
    public static final String SOCKETSTATUS = "status";
    public static final String SOCKETREGISTER = "register";
    public static final String SOCKETUNREGISTER = "unregister";
    public static final String SOCKETUPDATE = "update";
    public static final String SOCKETCLOSE = "close";
    public static final String SOCKETADDNEWEVENT = "add";

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


    public static int getIcon(String type){
        int id = 0;
        switch (type){
            case EVENT_TYPE_MOVIE:
                id = R.mipmap.movie;
                break;
            case EVENT_TYPE_DRINK:
                id = R.mipmap.drink;
                break;
            case EVENT_TYPE_DINE:
                id = R.mipmap.dine;
                break;
            case EVENT_TYPE_DATE:
                id = R.mipmap.date;
                break;
            case EVENT_TYPE_PROJECT:
                id = R.mipmap.project;
                break;
            case EVENT_TYPE_STUDY:
                id = R.mipmap.study;
                break;
        }

        return id;
    }

    public static int getMarker(String type){
        int id = 0;
        switch (type){
            case EVENT_TYPE_MOVIE:
                id = R.mipmap.movie_pic;
                break;
            case EVENT_TYPE_DRINK:
                id = R.mipmap.drink_pic;
                break;
            case EVENT_TYPE_DINE:
                id = R.mipmap.dine_pic;
                break;
            case EVENT_TYPE_DATE:
                id = R.mipmap.date_pic;
                break;
            case EVENT_TYPE_PROJECT:
                id = R.mipmap.project_pic;
                break;
            case EVENT_TYPE_STUDY:
                id = R.mipmap.study_pic;
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

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
