package com.is416.smujio;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.is416.smujio.component.LoadingButton;
import com.is416.smujio.model.User;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.is416.smujio.util.SharedPreferenceManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class LandingActivity extends AppCompatActivity {

    private Context mContext;
    private Activity this_ac;

    private LinearLayout main_frame;
    private LinearLayout logo_frame;
    private RelativeLayout action_frame;
    private LoadingButton register;
    private LoadingButton login;
    private LinearLayout buttons;
    private LinearLayout actions;
    private EditText email;
    private EditText password;
    private LoadingButton action;
    private TextView switchAction;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        this.mContext = this;
        this.this_ac = this;
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
        this.actions = findViewById(R.id.actions);
        this.buttons = findViewById(R.id.buttons);
        this.action = findViewById(R.id.action);
        this.switchAction = findViewById(R.id.switch_action);
        this.email = findViewById(R.id.email);
        this.password = findViewById(R.id.password);
    }

    private void init(){
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        this.login.setText(getResources().getString(R.string.login));
        this.register.setText(getResources().getString(R.string.register));
        getMetrics();
        loginCheck();
        ActivityManager.emptyStack();
    }

    private void addListeners(){
        this.login.setOnClickListener((v) -> {
            chooseAction(false);
        });

        this.register.setOnClickListener((v) -> {
            chooseAction(true);
        });

        this.main_frame.setOnClickListener((v) -> {
            if (imm.isActive()){
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    private void loginCheck(){
        //TODO Login
        if (!SharedPreferenceManager.get(General.EMAIL, mContext).equals(SharedPreferenceManager.nullable) &&  !SharedPreferenceManager.get(General.PERSIST, mContext).equals(SharedPreferenceManager.nullable)){
            String email = SharedPreferenceManager.get(General.EMAIL, mContext);
            String password = SharedPreferenceManager.get(General.PASSWORD, mContext);
            login(email, password, false, true, 0);
        }else{
            ValueAnimator anim = ValueAnimator.ofFloat(0f,1.25f);
            anim.setDuration(1000);
            anim.setStartDelay(2000);
            //anim.setRepeatCount(0);
            anim.addUpdateListener(animation -> {
                float currentValue = (float) animation.getAnimatedValue();
                action_frame.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,0,currentValue));
                action_frame.setAlpha(currentValue);
                action_frame.requestLayout();
            });
            anim.start();
        }
    }



    private void chooseAction(boolean isNew){
        this.switchAction.setText(isNew ? getResources().getText(R.string.switch_action_new) : getResources().getText(R.string.switch_action_exist));
        this.action.setText(isNew ? getResources().getText(R.string.register) : getResources().getText(R.string.login));
        this.actions.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofFloat(1.25f,2f);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            float ratio = ((float) animation.getAnimatedValue() - 1.25f)/0.75f;
            action_frame.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,0,currentValue));
            buttons.setAlpha(1f - ratio);
            actions.setAlpha(ratio);
            action_frame.requestLayout();
            buttons.requestLayout();
            actions.requestLayout();
        });

        anim.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                buttons.setVisibility(View.GONE);
                super.onAnimationEnd(animation);
            }
        });

        anim.start();
    }

    public void switchAction(View view) {
        String action = ((TextView)view).getText().toString();
        boolean isNew = action.equals(getResources().getText(R.string.switch_action_new));
        this.switchAction.setText(isNew ? getResources().getText(R.string.switch_action_exist) : getResources().getText(R.string.switch_action_new));
        this.action.setText(isNew ? getResources().getText(R.string.login) : getResources().getText(R.string.register));
    }


    public void performAction(View view) {
        String action = ((LoadingButton)view).getText();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        boolean isLogin = action.equals(getResources().getText(R.string.login));
        this.action.setLoading(true);
        if (isLogin){
            login(email, password, true, false, 0);
        }else {
            register(email, password);
        }
    }

    private void register(String email, String password){
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            General.httpRequest(mContext, General.HTTP_POST, "/user",body,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    action.setLoading(false);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                login(email, password, true, false,0);
                                break;
                            case General.HTTP_EXCEPTION:
                                General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                break;
                            case General.HTTP_FAIL:
                                General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    action.setLoading(false);
                    General.makeToast(mContext, mContext.getResources().getText(R.string.unknown_error).toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(String email, String password, boolean isToLocal, boolean isTimer, int count){
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            General.httpRequest(mContext, General.HTTP_POST, "/login",body,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    action.setLoading(false);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                JSONObject data = response.getJSONObject(General.HTTP_DATA_KEY);
                                General.token = data.getString("secret");
                                General.email = email;
                                General.user = User.JsonToObject(data.getJSONObject("user"));
                                if (isToLocal){
                                    HashMap<String, String> values = new HashMap<>();
                                    values.put(General.EMAIL, email);
                                    values.put(General.PASSWORD, password);
                                    values.put(General.ACCOUNTID,General.user.getAccountId()+"");
                                    values.put(General.PERSIST, "TRUE");
                                    SharedPreferenceManager.saveMultiple(values, mContext);
                                }

                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(this_ac, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, JioActivity.EVENT_DETAIL_REQUEST_CODE);
                                }else {
                                    start_main(isTimer);
                                }

                                break;
                            case General.HTTP_EXCEPTION:
                                if (count < 3){
                                    login(email, password, isToLocal, isTimer, count + 1);
                                }else {
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                }
                                break;
                            case General.HTTP_FAIL:
                                General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    action.setLoading(false);
                    General.makeToast(mContext, mContext.getResources().getText(R.string.unknown_error).toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMetrics(){
        Rect r = new Rect();
        this.main_frame.getWindowVisibleDisplayFrame(r);
        General.METRIC_HEIGHT = r.bottom - r.top;
        General.METRIC_WIDTH = r.right - r.left;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0){
            start_main(false);
        }
    }

    private void start_main(boolean isTimer){
        Intent it = new Intent(mContext, JioActivity.class);
        it.putExtra("isNew", action.getText().equals(getResources().getString(R.string.register)));

        if (isTimer){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(it);
                    finish();
                }
            }, 2000);
        }else{
            startActivity(it);
            finish();
        }
    }
}
