package com.is416.smujio.component.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.is416.smujio.EventActivity;
import com.is416.smujio.JioActivity;
import com.is416.smujio.R;
import com.is416.smujio.component.dialog.InitEventDialog;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Gods on 2/26/2018.
 */

public class PairFragment extends Fragment implements SensorEventListener {

    private Context mContext;
    private LayoutInflater inflater;

    private View mainView;
    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;
    private boolean isPaired = false;
    private long id;

    public SensorManager mSensorManager;
    public Sensor mAccelerometerSensor;
    public Vibrator mVibrator;// vibrate the phone
    public SoundPool mSoundPool;// vibrate SoundPool

    //record shake state
    private boolean isShake = false;

    //count down timer
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private static final long START_TIME_IN_MILLIS = 11000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private ImageView mTopPart;
    private ImageView mTopLine;
    private ImageView mBottomLine;
    private TextView mConnectionMsg;
    private TextView mCountdown;
    private MyHandler mHandler;
    private int mShakeAudio;
    private AVLoadingIndicatorView mAVI;

    //
    private String location = "";

    private Geocoder geocoder;
    private boolean lastCall = false;
    private boolean eventStatus = false;
    private String master = "SHAKE";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.pair_fragment, container, false);
        this.inflater = inflater;

        init();
        bindView();
        addListener();

        return mainView;
    }

    private void bindView() {
        mTopPart = mainView.findViewById(R.id.main_shake_top);
        mTopLayout = mainView.findViewById(R.id.main_linear_top);
        mBottomLayout = mainView.findViewById(R.id.main_linear_bottom);
        mTopLine = mainView.findViewById(R.id.main_shake_top_line);
        mBottomLine = mainView.findViewById(R.id.main_shake_bottom_line);
        mAVI = mainView.findViewById(R.id.avi);
        mCountdown = mainView.findViewById(R.id.text_view_countdown);
        mConnectionMsg = mainView.findViewById(R.id.text_view_shakeinfo);

        //default
        mAVI.setVisibility(View.GONE);
        mConnectionMsg.setVisibility(View.GONE);
        mCountdown.setVisibility(View.GONE);
        mTopLine.setVisibility(View.GONE);
        mBottomLine.setVisibility(View.GONE);
    }

    private void addListener() {

    }

    private void init() {
        this.mContext = ((JioActivity) ActivityManager.getAc("MAIN")).getContext();
        mSensorManager = ((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE));
        geocoder = new Geocoder(mContext.getApplicationContext());
        mHandler = new MyHandler(this);
        // initiate SoundPool
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mShakeAudio = mSoundPool.load(mContext, R.raw.shake_audio, 1);
        // get Vibrate Service
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void startAnimation(boolean isBack) {
        //Initiate animation's coordinates position to relative to yourself
        int type = Animation.RELATIVE_TO_SELF;

        float topFromY;
        float topToY;
        float bottomFromY;
        float bottomToY;
        if (isBack) {
            topFromY = -1f;
            topToY = 0;
            bottomFromY = 1f;
            bottomToY = 0;
        } else {
            topFromY = 0;
            topToY = -1f;
            bottomFromY = 0;
            bottomToY = 1f;
        }

        //Animation effect for the top image
        TranslateAnimation topAnim = new TranslateAnimation(
                type, 0, type, 0, type, topFromY, type, topToY
        );
        topAnim.setDuration(200);
        // Animation stop at the last frame
        topAnim.setFillAfter(true);

        // Animation effect for the bottom image
        TranslateAnimation bottomAnim = new TranslateAnimation(
                type, 0, type, 0, type, bottomFromY, type, bottomToY
        );
        bottomAnim.setDuration(200);
        bottomAnim.setFillAfter(true);

        // check if the animation return to origin, let the top and bottom line "GONE"
        if (isBack) {
            bottomAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //after the animation, hind the 2 lines in the center (let them "GONE")
                    mAVI.setVisibility(View.GONE);
                    mConnectionMsg.setVisibility(View.GONE);
                    mCountdown.setVisibility(View.GONE);
                    mTopLine.setVisibility(View.GONE);
                    mBottomLine.setVisibility(View.GONE);
                }
            });
        }
        // set animation
        mTopLayout.startAnimation(topAnim);
        mBottomLayout.startAnimation(bottomAnim);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {

            //Obtain x,y,z value
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math
                    .abs(z) > 17) && !isShake) {
                isShake = true;
                // Shake and vibrate
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            //start vibrate, open shake sound track and show animation effect
                            mHandler.obtainMessage(START_SHAKE).sendToTarget();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void toggleShakeListener(boolean isOpen) {
        if (isOpen) {
            if (mSensorManager != null) {
                //Obtain AccelerometerSensor
                mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (mAccelerometerSensor != null) {
                    mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                }
            }
        } else {
            mSensorManager.unregisterListener(this);
        }
    }

    private void updateCountDownText(long mTimeLeftInMillis) {
        mCountdown.setText("" + mTimeLeftInMillis);
    }

    private class MyHandler extends Handler {
        private WeakReference<PairFragment> mReference;
        private PairFragment pairFragment;

        public MyHandler(PairFragment fragment) {
            mReference = new WeakReference<>(fragment);
            if (mReference != null) {
                pairFragment = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    pairFragment.mVibrator.vibrate(300);
                    mAVI.setVisibility(View.VISIBLE);
                    mConnectionMsg.setVisibility(View.VISIBLE);
                    mCountdown.setVisibility(View.VISIBLE);
                    mTimerRunning = true;
                    //This method requires the caller to hold the permission VIBRATE.
                    // open shake sound track
                    pairFragment.mSoundPool.play(mShakeAudio, 1, 1, 0, 0, 1);
                    mTopLine.setVisibility(View.VISIBLE);
                    mBottomLine.setVisibility(View.VISIBLE);
                    startAnimation(false);// animation of split the shakehand picture into 2
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runCountdown(10);
                        }
                    }, 1000);
                    break;
            }
        }
    }

    private void runCountdown(long time) {
        if (time == 10){
            General.UIHandler.post(() -> {
                updateCountDownText(10);
            });
        }
        if (time == 0) {
            General.UIHandler.post(() -> {
                toggleShakeListener(true);
                shakeJoin(true);
                isShake = false;
                // two pictures return to original place
                startAnimation(true);
            });
        } else {
            time--;
            long finalTime = time;
            General.UIHandler.post(() -> {
                shakeJoin(false);
                updateCountDownText(finalTime);
                if (isPaired) {
                    toggleShakeListener(true);
                    //added
                    shakePaired();
                    isShake = false;
                    // two pictures return to original place
                    startAnimation(true);
                } else {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runCountdown(finalTime);
                        }
                    }, 1000);
                }
            });
        }
    }

    private void shakeJoin(boolean isLast) {
        final String[] url = {"/event"};
        JSONObject body = new JSONObject();
        try {
            Location location_data = ((JioActivity) ActivityManager.getAc(JioActivity.name)).getLastKnownLocation();
            List<Address> addressList = geocoder.getFromLocation(location_data.getLatitude(), location_data.getLongitude(), 1);
            location = addressList.get(0).getAddressLine(0).split(",")[0];

            body.put(General.ACCOUNTID, General.user.getAccountId());
            body.put(General.LATITUDE, location_data.getLatitude());
            body.put(General.LONGITUDE, location_data.getLongitude());
            body.put(General.LOCATION, location);
            body.put(General.LASTCALL, isLast);
            body.put(General.SHAKE, "shake");
            General.httpRequest(mContext, General.HTTP_PUT, url[0], body, false, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)) {
                            case General.HTTP_SUCCESS:
                                //TODO
                                JSONObject data = response.getJSONObject(General.HTTP_DATA_KEY);
//                                System.out.println(data);
                                id = data.getLong(General.ID);
                                isPaired = data.getBoolean(General.EVENTSTATUS);
//                                System.out.println(isPaired);
                                break;
                            case General.HTTP_EXCEPTION:
                                General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                break;
                            case General.HTTP_FAIL:
                                if (response.getString(General.HTTP_MESSAGE_KEY).equals(General.PAIR_IN_EVENT)){
                                    isPaired = true;
                                }else {
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shakePaired() {
        try {
            General.httpRequest(mContext, General.HTTP_GET, "/event/" + id,null,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                General.currentEvent = Event.JsonToObject(response.getJSONObject(General.HTTP_DATA_KEY));
                                Intent it = new Intent(mContext, EventActivity.class);
                                isShake = false;
                                startActivityForResult(it, JioActivity.EVENT_DETAIL_REQUEST_CODE);
                                break;
                            case General.HTTP_EXCEPTION:
                                General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                break;
                            case General.HTTP_FAIL:
                                General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    General.makeToast(mContext, mContext.getResources().getText(R.string.unknown_error).toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
