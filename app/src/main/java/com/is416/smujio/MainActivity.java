package com.is416.smujio;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.is416.smujio.util.General;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private LinearLayout main_frame;
    private LinearLayout logo_frame;
    private LinearLayout action_frame;
    private Button register;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        init();
        addListeners();
    }

    private void bindView(){
        this.main_frame = findViewById(R.id.main);
        this.logo_frame = findViewById(R.id.logo_frame);
        this.action_frame = findViewById(R.id.action_frame);
        this.register = findViewById(R.id.register);
        this.login = findViewById(R.id.login);
    }

    private void init(){
        //TODO Login
        if (General.token != null){

        }else{
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            action_frame.measure(w, h);
            final int height = action_frame.getMeasuredHeight();
            WindowManager wm = (WindowManager) this .getSystemService(Context.WINDOW_SERVICE);
            final int sHeight = wm.getDefaultDisplay().getHeight();
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                public void run() {
                    ValueAnimator anim = ValueAnimator.ofInt(sHeight, sHeight - height);
                    anim.setDuration(500)
                }
            }, 3000);

        }
    }

    private void addListeners(){

    }
}
