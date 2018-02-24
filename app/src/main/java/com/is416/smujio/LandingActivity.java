package com.is416.smujio;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.is416.smujio.util.General;

public class LandingActivity extends AppCompatActivity {

    private LinearLayout main_frame;
    private LinearLayout logo_frame;
    private LinearLayout action_frame;
    private Button register;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

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
        login();
    }

    private void login(){
        //TODO Login
        if (General.token != null){

        }else{
            ValueAnimator anim = ValueAnimator.ofFloat(0f,1.25f);
            anim.setDuration(1000);
            anim.setStartDelay(2500);
            //anim.setRepeatCount(0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    action_frame.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,0,currentValue));
                    action_frame.setAlpha(currentValue);
                    action_frame.requestLayout();
                }
            });
            anim.start();
        }
    }

    private void addListeners(){

    }
}
