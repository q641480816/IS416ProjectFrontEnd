package com.is416.smujio.component.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.is416.smujio.JioActivity;
import com.is416.smujio.R;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Gods on 2/26/2018.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private Context mContext;
    private LayoutInflater inflater;

    private Location myLastKnownLocation;
    private View mainView;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.map_fragment, container, false);
        this.inflater = inflater;

        init();
        bindView();
        addListener();
        return mainView;
    }

    private void bindView() {
        this.mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFrag.getMapAsync(this);
    }

    private void init() {
        this.mContext = ((JioActivity) ActivityManager.getAc("MAIN")).getContext();
    }

    private void addListener() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //If save power, turn off
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);

        this.myLastKnownLocation = ((JioActivity) ActivityManager.getAc("MAIN")).getLastKnownLocation();
        double latitude = this.myLastKnownLocation.getLatitude();
        double longitude = this.myLastKnownLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        init_event_list();
    }

    private void init_event_list(){
        mGoogleMap.clear();
        ArrayList<Event> events = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < 20; i++){
            int type = (r.nextInt(10)/3);
            String type_name = "";
            switch (type){
                case 0:
                    type_name = General.EVENT_TYPE_BAR;
                    break;
                case 1:
                    type_name = General.EVENT_TYPE_DINE;
                    break;
                case 2:
                    type_name = General.EVENT_TYPE_MOVIE;
                    break;
            }
            double lav = ((double)r.nextInt(25)/10000.0);
            double lov = ((double)r.nextInt(30)/10000.0);

            double la = r.nextInt(2) == 0 ? this.myLastKnownLocation.getLatitude() + lav : this.myLastKnownLocation.getLatitude() - lav;
            double lo = r.nextInt(2) == 0 ? this.myLastKnownLocation.getLongitude() + lov : this.myLastKnownLocation.getLongitude() - lov;

            if (!type_name.equals("")){
                events.add(new Event(i,la,lo,new Date(),0,type_name,new ArrayList<>()));
            }
        }

        for (Event e: events){
            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(e.getLatitude(), e.getLongitude()));
            System.out.println(General.getMarker(e.getType()));
            //Custom icon
            markerOption.icon(BitmapDescriptorFactory.fromResource(General.getMarker(e.getType())));
            Marker currentMarker = mGoogleMap.addMarker(markerOption);
        }
    }
}