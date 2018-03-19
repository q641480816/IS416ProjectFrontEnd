package com.is416.smujio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EventActivity extends AppCompatActivity {

    private Context mContext;
    public static final String name = "EVENT";
    private Intent init_intent;

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
    }

    private void bindView(){

    }

    private void addListeners(){

    }
}
