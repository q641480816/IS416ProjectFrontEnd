package com.is416.smujio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.is416.smujio.adapter.JioFragmentPagerAdapter;
import com.is416.smujio.model.Event;
import com.is416.smujio.service.EventSocketService;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class JioActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener,
        LocationListener {

    private Intent pIntent;
    private Context mContext;
    public static final String name = "MAIN";
    public static final int EVENT_DETAIL_REQUEST_CODE = 999;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final String LOCATION_SERVICE = Context.LOCATION_SERVICE;
    public static String myProvider;
    public boolean isInit = false;
    public boolean isOnTop = false;
    public static List<Long> pendingActivity = new ArrayList<>();
    public static List<Long> pendingRemoveActivity = new ArrayList<>();

    private LocationManager locationManager;
    private JioFragmentPagerAdapter jioFragmentPagerAdapter;
    private ViewPager main_content;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout main_window;
    private LinearLayout fallback_window;
    private ImageView options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jio);

        this.pIntent = getIntent();
        this.mContext = this;

        bindView();
        init();
        addListeners();
    }

    private void bindView() {
        this.main_content = findViewById(R.id.viewPager);
        this.bottomNavigationView = findViewById(R.id.bt_nav);
        this.main_window = findViewById(R.id.main_window);
        this.fallback_window = findViewById(R.id.fallback_window);
        this.options = findViewById(R.id.options);
    }

    private void init() {
        ActivityManager.add(name, this);

        if (isOPen(mContext)){
            this.fallback_window.setVisibility(View.GONE);
            this.main_window.setVisibility(View.VISIBLE);
            this.locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            this.jioFragmentPagerAdapter = new JioFragmentPagerAdapter(getSupportFragmentManager());
            this.main_content.setAdapter(this.jioFragmentPagerAdapter);
            this.main_content.setCurrentItem(0);
            //TODO: Welcome new user
            System.out.println(this.pIntent.getBooleanExtra("isNew", false));
            this.isInit = true;
        }else {
            this.fallback_window.setVisibility(View.VISIBLE);
            this.main_window.setVisibility(View.GONE);
        }

        Intent it = new Intent(mContext, EventSocketService.class);
        it.putExtra(General.ACCOUNTID, General.user.getAccountId());
        startService(it);
        checkInEvent();
    }

    private void addListeners() {
        this.main_content.addOnPageChangeListener(this);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);
        this.options.setOnClickListener((v)->{
            showPopupMenu(mContext, v);
        });
    }

    private void checkInEvent(){
        if (General.user.getInEventStatus() != General.USER_NOT_IN_EVENT){
            try {
                General.httpRequest(mContext, General.HTTP_GET, "/event/" + General.user.getInEventStatus(),null,false, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt(General.HTTP_STATUS_KEY)){
                                case General.HTTP_SUCCESS:
                                    Event e = Event.JsonToObject(response.getJSONObject(General.HTTP_DATA_KEY));
                                    General.currentEvent = e;
                                    Intent intent = new Intent(mContext, EventActivity.class);
                                    startActivityForResult(intent,EVENT_DETAIL_REQUEST_CODE);
                                    break;
                                case General.HTTP_EXCEPTION:
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                    break;
                                case General.HTTP_FAIL:
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                            }
                        } catch (Exception e) {
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (main_content.getCurrentItem()) {
                case PAGE_ONE:
                    if (!jioFragmentPagerAdapter.isListOpen()){
                        jioFragmentPagerAdapter.toggleGPSTrack(true);
                    }
                    this.bottomNavigationView.setSelectedItemId(R.id.menu_nav_map);
                    this.jioFragmentPagerAdapter.toggleShakeListener(false);
                    break;
                case PAGE_TWO:
                    jioFragmentPagerAdapter.toggleGPSTrack(false);
                    this.bottomNavigationView.setSelectedItemId(R.id.menu_nav_pair);
                    this.jioFragmentPagerAdapter.toggleShakeListener(true);
                    break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_nav_map:
                this.main_content.setCurrentItem(PAGE_ONE);
                break;
            case R.id.menu_nav_pair:
                this.main_content.setCurrentItem(PAGE_TWO);
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        ActivityManager.remove(name);
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        General.makeToast(mContext, "LOCATION PROVIDER CHANGED!!!");
    }

    @Override
    public void onProviderEnabled(String s) {
        if (isInit){
            this.fallback_window.setVisibility(View.GONE);
            this.main_window.setVisibility(View.VISIBLE);
        }else {
            if (isOnTop){
                init();
                addListeners();
            }
        }
    }

    @Override
    public void onProviderDisabled(String s) {
        this.fallback_window.setVisibility(View.VISIBLE);
        this.main_window.setVisibility(View.GONE);
    }

    public Context getContext() {
        return mContext;
    }

    @SuppressLint("MissingPermission")
    private void setLocationService(boolean isToOpen) {
        if (this.locationManager == null) {
            //check init
            this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (isToOpen) {
            if (this.locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    myProvider = LocationManager.NETWORK_PROVIDER;
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this, null);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    myProvider = LocationManager.GPS_PROVIDER;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this, null);
                } else {
                    General.makeToast(mContext, "GG");
                    myProvider = LocationManager.NETWORK_PROVIDER;
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500000000, 500000000, this, null);
                }
            } else {
                General.makeToast(mContext, "Location service is not available!");
            }
        }else {
            locationManager.removeUpdates(this);
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLastKnownLocation(){
        setLocationService(true);
        Location l = null;
        Location location = null;
        while (l == null){
            location = locationManager.getLastKnownLocation(myProvider);
            if ((General.myLastLocation == null && location != null) || (General.myLastLocation != null)){
                l = location;
                General.myLastLocation = location;
                //System.out.println(location);
                //locationManager.removeUpdates(this);
            }
        }
        //System.out.println("Found");
        return l;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setLocationService(true);
        this.isOnTop = true;
        if (pendingActivity.size() > 1){
            this.jioFragmentPagerAdapter.update_event_list();
        } else if (pendingActivity.size() > 0){
            this.jioFragmentPagerAdapter.update_event_one(pendingActivity.get(0));
        }

        for(long id : pendingRemoveActivity){
            removeOneEvent(id);
        }
    }

    @Override
    public void onBackPressed() {
        if (isInit && jioFragmentPagerAdapter.isListOpen()){
            jioFragmentPagerAdapter.closeList();
        }else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onPause() {
        try {
            this.jioFragmentPagerAdapter.toggleShakeListener(false);
            this.setLocationService(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.isOnTop = false;
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_DETAIL_REQUEST_CODE){
            if (isInit) {
                this.updateAllEvent();
            }else {
                init();
                addListeners();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(final Context context, View ancher) {
        PopupMenu popupMenu = new PopupMenu(context, ancher);
        popupMenu.inflate(R.menu.main_hamberger_menu);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_profile:
                    Intent it = new Intent(this, ProfileActivity.class);
                    startActivity(it);
                    break;
                case R.id.menu_setting:
                    //System.out.println("setting");
                    break;
            }
            return true;
        });
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        popupMenu.show();
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public void updateMenuState(float alpha){
        this.options.setAlpha(alpha);
        this.options.requestLayout();
    }

    public void updateMenuState(boolean isVisible){
        this.options.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void updateOneEvent(long id){
        this.jioFragmentPagerAdapter.update_event_one(id);
    }

    public void updateAllEvent(){
        this.jioFragmentPagerAdapter.update_event_list();
    }

    public void removeOneEvent(long id){
        this.jioFragmentPagerAdapter.removeOneEvent(id);
    }
}