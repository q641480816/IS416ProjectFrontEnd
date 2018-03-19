package com.is416.smujio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.is416.smujio.model.Event;
import com.is416.smujio.util.General;

public class EventActivity extends AppCompatActivity {

    private Context mContext;
    public static final String name = "EVENT";
    private Intent init_intent;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        init();
        bindView();
        addListeners();
    }

    private void init(){
        this.mContext = this;
        this.init_intent = getIntent();
        this.event = General.currentEvent;
        System.out.println(this.event.getId());
    }

    private void bindView(){

    }

    private void addListeners(){

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
