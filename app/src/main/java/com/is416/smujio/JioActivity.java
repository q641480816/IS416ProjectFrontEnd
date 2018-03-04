package com.is416.smujio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.is416.smujio.adapter.JioFragmentPagerAdapter;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;

public class JioActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener,
        LocationListener {

    private Intent pIntent;
    private Context mContext;
    public static final String name = "MAIN";
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final String LOCATION_SERVICE = Context.LOCATION_SERVICE;
    public static String myProvider;

    private LocationManager locationManager;
    private JioFragmentPagerAdapter jioFragmentPagerAdapter;
    private ViewPager main_content;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jio);

        this.pIntent = getIntent();
        this.mContext = this;

        init();
        bindView();
        addListeners();
    }

    private void init() {
        ActivityManager.add(name, this);
        this.locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        this.jioFragmentPagerAdapter = new JioFragmentPagerAdapter(getSupportFragmentManager());

        //TODO: Welcome new user
        System.out.println(this.pIntent.getBooleanExtra("isNew", false));
    }

    private void bindView() {
        this.main_content = findViewById(R.id.viewPager);
        this.bottomNavigationView = findViewById(R.id.bt_nav);
        this.main_content.setAdapter(this.jioFragmentPagerAdapter);

        this.main_content.setCurrentItem(0);
    }

    private void addListeners() {
        this.main_content.addOnPageChangeListener(this);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);
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
                    this.bottomNavigationView.setSelectedItemId(R.id.menu_nav_map);
                    break;
                case PAGE_TWO:
                    this.bottomNavigationView.setSelectedItemId(R.id.menu_nav_pair);
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
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    public Context getContext() {
        return mContext;
    }

    private void setLocationService(boolean isToOpen) {
        if (this.locationManager == null) {
            //check init
            this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (isToOpen) {
            //TODO: check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            if (this.locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    myProvider = LocationManager.NETWORK_PROVIDER;
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, this, null);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    myProvider = LocationManager.GPS_PROVIDER;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this, null);
                } else {
                    General.makeToast(mContext, "GG");
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
            if ((General.myLastLocation == null && location != null) || (General.myLastLocation != null && (General.myLastLocation.getLongitude() != location.getLongitude() || General.myLastLocation.getLatitude() != location.getLatitude()))){
                l = location;
                General.myLastLocation = location;
                //System.out.println(location);
                locationManager.removeUpdates(this);
            }
        }
        return l;
    }
}

