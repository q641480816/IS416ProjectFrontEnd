package com.is416.smujio;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.is416.smujio.adapter.EventFragmentAdapter;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;

public class EventActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 1000;
    public static final String name = "EVENT";
    public static final int PAGE_ONE = 0;
    private Context mContext;
    private Intent init_intent;
    private ViewPager main_content;
    private EventFragmentAdapter eventFragmentAdapter;

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
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void finish() {
        ActivityManager.remove(name);
        setResult(RESULT_CODE);
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
}
