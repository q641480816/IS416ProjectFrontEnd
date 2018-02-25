package com.is416.smujio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.is416.smujio.util.General;
import com.is416.smujio.util.SharedPreferenceManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class LandingActivity extends AppCompatActivity {

    private Context mContext;

    private LinearLayout main_frame;
    private LinearLayout logo_frame;
    private RelativeLayout action_frame;
    private Button register;
    private Button login;
    private LinearLayout buttons;
    private LinearLayout actions;
    private EditText email;
    private EditText password;
    private Button action;
    private TextView switchAction;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        this.mContext = this;
        bindView();
        addListeners();
        init();
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
        loginCheck();
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
            login(email, password, false);
        }else{
            ValueAnimator anim = ValueAnimator.ofFloat(0f,1.25f);
            anim.setDuration(1000);
            anim.setStartDelay(2500);
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
        String action = ((Button)view).getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        boolean isLogin = action.equals(getResources().getText(R.string.login));

        if (isLogin){
            login(email, password, true);
        }else {
            register(email, password);
        }
    }

    private void register(String email, String password){
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            General.httpRequest(mContext, General.HTTP_POST, "/createUser",body,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                login(email, password, true);
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
                    General.makeToast(mContext, mContext.getResources().getText(R.string.unknown_error).toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(String email, String password, boolean isToLocal){
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            General.httpRequest(mContext, General.HTTP_POST, "/login",body,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                JSONObject data = response.getJSONObject(General.HTTP_DATA_KEY);
                                General.token = data.getString("secret");
                                General.email = email;
                                General.user = data.getJSONObject("user");
                                if (isToLocal){
                                    HashMap<String, String> values = new HashMap<>();
                                    values.put(General.EMAIL, email);
                                    values.put(General.PASSWORD, password);
                                    values.put(General.PERSIST, "TRUE");
                                    SharedPreferenceManager.saveMultiple(values, mContext);
                                }
                                General.makeToast(mContext, "Loged in ");
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
                    General.makeToast(mContext, mContext.getResources().getText(R.string.unknown_error).toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
