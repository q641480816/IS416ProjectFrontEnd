package com.is416.smujio;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.is416.smujio.util.ActivityManager;

public class ProfileActivity extends AppCompatActivity {

    private Context mContext;
    public static final String name = "PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.mContext = this;

        bindView();
        init();
        addListeners();
    }

    private void bindView(){

    }

    private void init(){
        ActivityManager.add(name, this);

    }

    private void addListeners(){

    }

    @Override
    public void finish() {
        ActivityManager.remove(name);
        super.finish();
    }
}
