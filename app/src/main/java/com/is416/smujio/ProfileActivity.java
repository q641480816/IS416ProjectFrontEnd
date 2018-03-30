package com.is416.smujio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.is416.smujio.model.User;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.is416.smujio.util.SharedPreferenceManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private Context mContext;
    public static final String name = "PROFILE";
    public static final int REQUEST_CODE_CHOOSE_AVATAR = 1242;
    public static final int PROFILE_REQUEST_PERMISSION = 7765;

    private TextView email;
    private EditText nickname;
    private TextView gender;
    private ImageView edit_nickname;
    private TextView edit_nickname_confirm;
    private AVLoadingIndicatorView edit_nickname_loadingIndicator;
    private ImageView edit_gender;
    private AVLoadingIndicatorView edit_gender_loadingIndicator;
    private LinearLayout main_shadow;
    private TextView logout;
    private ImageView edit_avatar;
    private ImageView avatar;
    private AVLoadingIndicatorView edit_avatar_loading;
    private Bitmap avatar_content;
    private String avatar_content_base64;
    private Target avatar_target;

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

    private void bindView() {
        this.email = findViewById(R.id.email);
        this.nickname = findViewById(R.id.nickname);
        this.edit_nickname = findViewById(R.id.edit_nickname);
        this.edit_nickname_confirm = findViewById(R.id.edit_nickname_confirm);
        this.edit_nickname_loadingIndicator = findViewById(R.id.edit_nickname_loading);
        this.gender = findViewById(R.id.gender);
        this.edit_gender = findViewById(R.id.edit_gender);
        this.edit_gender_loadingIndicator = findViewById(R.id.edit_gender_loading);
        this.avatar = findViewById(R.id.avatar);
        this.edit_avatar = findViewById(R.id.edit_avatar);
        this.main_shadow = findViewById(R.id.main_shadow);
        this.logout = findViewById(R.id.logout);
        this.edit_avatar_loading = findViewById(R.id.edit_avatar_loading);
    }

    private void init() {
        ActivityManager.add(name, this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.user = General.user;
        this.email.setText(this.user.getEmail());
        this.nickname.setText(this.user.getNickName());
        this.gender.setText(this.user.getGender() == General.GENDER_FEMALE ? getResources().getText(R.string.profile_gender_female) : getResources().getText(R.string.profile_gender_male));
        this.nickname.setClickable(false);
        this.nickname.setFocusableInTouchMode(false);
        this.nickname.setFocusable(false);
        this.avatar_content_base64 = this.user.getAvatar();
        if (!this.avatar_content_base64.equals("null")) {
            this.avatar_content = General.getCroppedBitmap(General.base64ToBitmap(this.avatar_content_base64));
            this.avatar.setImageBitmap(this.avatar_content);
        }

        this.avatar_target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmap = General.comp(bitmap);
                avatar_content = General.getCroppedBitmap(bitmap);
                avatar_content_base64 = General.bitmapToBase64(bitmap);
                avatar.setImageBitmap(avatar_content);
                updateUser(edit_avatar_loading, edit_avatar,General.user.getNickName(), avatar_content_base64,General.user.getGender());
                System.gc();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                //
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //
            }
        };
    }

    private void addListeners() {
        this.main_shadow.setOnClickListener(v -> {
            try {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            } catch (Exception e) {}
        });
        this.edit_nickname.setOnClickListener(view -> {
            this.nickname.setFocusable(true);
            this.nickname.setFocusableInTouchMode(true);
            this.nickname.setClickable(true);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            this.nickname.requestFocus();
            this.edit_nickname.setVisibility(View.GONE);
            this.edit_nickname_confirm.setVisibility(View.VISIBLE);
        });
        this.edit_nickname_confirm.setOnClickListener((v) -> {
            try {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            } catch (Exception e) {}
            this.edit_nickname_confirm.setVisibility(View.GONE);
            this.edit_nickname_loadingIndicator.setVisibility(View.VISIBLE);
            updateUser(this.edit_nickname_loadingIndicator, this.edit_nickname, this.nickname.getText().toString()
                    ,this.avatar_content_base64, this.user.getGender());
            this.nickname.setClickable(false);
            this.nickname.setFocusableInTouchMode(false);
            this.nickname.setFocusable(false);
        });
        this.edit_gender.setOnClickListener((v) -> {
            showPopupMenu(mContext, v);
        });
        this.edit_avatar.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, this.PROFILE_REQUEST_PERMISSION);
            }else {
                chooseAvatar();
            }
        });
        this.logout.setOnClickListener((v) -> {
            SharedPreferenceManager.save(General.PERSIST, SharedPreferenceManager.nullable , mContext);
            Intent it = new Intent(mContext, LandingActivity.class);
            startActivity(it);
            finish();
        });
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(final Context context, View ancher) {
        PopupMenu popupMenu = new PopupMenu(context, ancher);
        popupMenu.inflate(R.menu.profile_gender_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            this.edit_gender.setVisibility(View.GONE);
            this.edit_gender_loadingIndicator.setVisibility(View.VISIBLE);
            switch (menuItem.getItemId()) {
                case R.id.menu_gender_male:
                    this.gender.setText(getResources().getText(R.string.profile_gender_male));
                    updateUser(this.edit_gender_loadingIndicator, this.edit_gender, General.user.getNickName(),
                            this.avatar_content_base64, General.GENDER_MALE);
                    break;
                case R.id.menu_gender_female:
                    this.gender.setText(getResources().getText(R.string.profile_gender_female));
                    updateUser(this.edit_gender_loadingIndicator, this.edit_gender, General.user.getNickName(),
                            this.avatar_content_base64, General.GENDER_FEMALE);
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_AVATAR && resultCode == RESULT_OK) {
            Uri uri = Matisse.obtainResult(data).get(0);
            try {
                if (!TextUtils.isEmpty(uri.getPath())) {
                    Picasso.with(mContext).load(uri).into(this.avatar_target);
                    this.edit_avatar.setVisibility(View.GONE);
                    this.edit_avatar_loading.setVisibility(View.VISIBLE);
                } else {
                    General.makeToast(mContext, "Invalid image!");
                }
            }catch (Exception e){
                e.printStackTrace();
                General.makeToast(mContext, "Exception");
            }
        }
    }

    private void chooseAvatar(){
        Matisse.from(ProfileActivity.this)
                .choose(MimeType.allOf())
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .maxSelectable(1)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE_AVATAR);
    }

    private void updateUser(View hide, View show, String nickname, String avatar, int gender){
        try {
            JSONObject body = new JSONObject();
            body.put(General.ACCOUNTID, this.user.getAccountId());
            body.put(General.NICKNAME,nickname);
            body.put(General.AVATAR, avatar);
            body.put(General.GENDER, gender);
            General.httpRequest(mContext, General.HTTP_PUT, "/user",body,false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                General.user.setAvatar(avatar);
                                General.user.setGender(gender);
                                General.user.setNickName(nickname);
                                hide.setVisibility(View.GONE);
                                show.setVisibility(View.VISIBLE);
                                General.makeToast(mContext, "Profile updated!");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chooseAvatar();
        }else {
            General.makeToast(mContext, "We need permission");
        }
    }

    @Override
    public void finish() {
        ActivityManager.remove(name);
        super.finish();
    }
}