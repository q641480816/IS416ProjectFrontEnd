package com.is416.smujio;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.is416.smujio.model.User;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;

public class ProfileActivity extends AppCompatActivity {

    private Context mContext;
    public static final String name = "PROFILE";

    private TextView email;
    private EditText nickname;
    private TextView gender;
    private ImageView edit_nickname;
    private ImageView edit_gender;
    private LinearLayout main_shadow;

    private User user;
    private InputMethodManager imm;

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
        this.email = findViewById(R.id.email);
        this.nickname = findViewById(R.id.nickname);
        this.gender = findViewById(R.id.gender);
        this.edit_gender = findViewById(R.id.edit_gender);
        this.edit_nickname = findViewById(R.id.edit_nickname);
        this.main_shadow = findViewById(R.id.main_shadow);
    }

    private void init(){
        ActivityManager.add(name, this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.user = General.user;
        this.email.setText(this.user.getEmail());
        this.nickname.setText(this.user.getNickName());
        this.gender.setText(this.user.getGender() == General.GENDER_FEMALE ? getResources().getText(R.string.profile_gender_female) : getResources().getText(R.string.profile_gender_male));
        this.nickname.setClickable(false);
        this.nickname.setFocusableInTouchMode(false);
        this.nickname.setFocusable(false);
    }

    private void addListeners(){
        this.main_shadow.setOnClickListener(v -> {
            try {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                this.nickname.setClickable(false);
                this.nickname.setFocusableInTouchMode(false);
                this.nickname.setFocusable(false);
            }catch (Exception e){e.printStackTrace();}
        });
        this.edit_nickname.setOnClickListener(view -> {
            this.nickname.setFocusable(true);
            this.nickname.setFocusableInTouchMode(true);
            this.nickname.setClickable(true);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            this.nickname.requestFocus();
        });
    }

    @Override
    public void finish() {
        ActivityManager.remove(name);
        super.finish();
    }
}
