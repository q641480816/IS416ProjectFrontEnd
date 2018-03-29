package com.is416.smujio;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.is416.smujio.adapter.EventFragmentAdapter;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import cz.msebera.android.httpclient.Header;

public class EventActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 1000;
    public static final String name = "EVENT";
    public static final int PAGE_ONE = 0;
    private Context mContext;
    private Intent init_intent;
    private ViewPager main_content;
    private EventFragmentAdapter eventFragmentAdapter;
    public final Handler myHandler = new Handler();

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        bindView();
        init();
        addListeners();
    }

    private void bindView(){
        this.main_content = findViewById(R.id.viewPager);
    }

    private void init() {
        ActivityManager.add(name, this);
        this.mContext = this;
        this.init_intent = getIntent();
        this.event = General.currentEvent;
        this.eventFragmentAdapter = new EventFragmentAdapter(getSupportFragmentManager());
        this.main_content.setAdapter(this.eventFragmentAdapter);
        this.main_content.setCurrentItem(PAGE_ONE);
    }

    private void addListeners(){

    }

    @Override
    protected void onResume() {
        General.isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        General.isForeground = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void finish() {
        ActivityManager.remove(name);
        setResult(RESULT_CODE);
        General.currentEvent = null;
        super.finish();
    }

    public Event getEvent(){
        return this.event;
    }

    public Context getContext(){
        return mContext;
    }

    public String getName(){
        return name;
    }

    public Handler getMyHandler(){
        return myHandler;
    }

    public void updateEvent(){
        try {
            General.httpRequest(mContext, General.HTTP_GET, "/event/" + this.event.getId(),null,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                event = Event.JsonToObject(response.getJSONObject(General.HTTP_DATA_KEY));
                                General.currentEvent = event;
                                eventFragmentAdapter.update();
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
