package com.is416.smujio.component.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.is416.smujio.JioActivity;
import com.is416.smujio.R;
import com.is416.smujio.adapter.EventListAdapter;
import com.is416.smujio.component.dialog.InitEventDialog;
import com.is416.smujio.component.dialog.JoinEventDialog;
import com.is416.smujio.model.Event;
import com.is416.smujio.model.User;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gods on 2/26/2018.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Context mContext;
    private LayoutInflater inflater;

    private int MOCK_TOP_HEIGHT;
    private int MOCK_SIDE_WIDTH;

    private HashMap<Marker, Event> marker_info;
    private Location myLastKnownLocation;
    private View mainView;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private LinearLayout list_container;
    private LinearLayout list_top;
    private LinearLayout list_top_back;
    private LinearLayout list_top_front;
    private ImageView list_top_back_back;
    private ListView event_list;
    private FloatingActionButton init_event;
    private EventListAdapter eventListAdapter;

    private boolean isListHeadPressed = false;
    private boolean isListOpen = false;
    private float listHeadMoveDis = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.map_fragment, container, false);
        this.inflater = inflater;

        bindView();
        init();
        addListener();
        return mainView;
    }

    private void bindView() {
        this.mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        this.list_container = mainView.findViewById(R.id.list_content);
        this.list_top = mainView.findViewById(R.id.list_top);
        this.init_event = mainView.findViewById(R.id.fab_add);
        this.list_top_front = mainView.findViewById(R.id.list_title_front);
        this.list_top_back = mainView.findViewById(R.id.list_title_back);
        this.list_top_back_back = mainView.findViewById(R.id.list_title_back_back);
        this.event_list = mainView.findViewById(R.id.event_list);

        mapFrag.getMapAsync(this);
    }

    private void init() {
        this.mContext = ((JioActivity) ActivityManager.getAc("MAIN")).getContext();
        this.eventListAdapter = new EventListAdapter(mContext,"",new ArrayList<>());
        this.MOCK_TOP_HEIGHT = General.METRIC_HEIGHT/11*10 - General.convertDpToPixel(65,mContext);
        this.MOCK_SIDE_WIDTH = General.convertDpToPixel(30, mContext);
        //init list
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(MOCK_SIDE_WIDTH, MOCK_TOP_HEIGHT, MOCK_SIDE_WIDTH, 0);
        this.list_container.setLayoutParams(params);
        this.event_list.setAdapter(eventListAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListener() {
        this.list_top.setOnClickListener(view -> {
            System.out.println("open");
        });
        this.list_top_back_back.setOnClickListener((v)->{
            if (isListOpen){
                reDrawList(false);
            }
        });
        this.init_event.setOnClickListener((v) -> {
            InitEventDialog initEventDialog = new InitEventDialog(mContext, "");
            initEventDialog.show();
        });
        this.list_top.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    this.isListHeadPressed = true;
                    this.mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                    this.listHeadMoveDis = motionEvent.getY();
                    return false;
                case MotionEvent.ACTION_UP:
                    this.isListHeadPressed = false;
                    this.mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                    if (!isListOpen){
                        reDrawList(true);
                    }
                    this.listHeadMoveDis = 0;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    //System.out.println(this.listHeadMoveDis - motionEvent.getY());
                    //reDrawList(this.listHeadMoveDis - motionEvent.getY());
                    return false;
                default:
                    return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (marker_info == null) {marker_info = new HashMap<>();}
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);
        //If save power, turn off
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        //googleMap.getUiSettings().setScrollGesturesEnabled(false);

        this.myLastKnownLocation = ((JioActivity) ActivityManager.getAc("MAIN")).getLastKnownLocation();
        double latitude = this.myLastKnownLocation.getLatitude();
        double longitude = this.myLastKnownLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        mGoogleMap.setOnMarkerClickListener(this);
        init_event_list(latitude, longitude);
        System.out.println(latitude + " " + longitude);
    }

    private void init_event_list(double la, double lo){
        mGoogleMap.clear();
        marker_info.clear();
        ArrayList<Event> events = new ArrayList<>();
        StringBuffer url = new StringBuffer();
        url.append("/event/all/");
        url.append(la);
        url.append(",");
        url.append(lo);

        try {
            General.httpRequest(mContext,General.HTTP_GET,url.toString(), null, false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                JSONArray data = response.getJSONArray(General.HTTP_DATA_KEY);
                                for (int i = 0; i < data.length(); i ++){
                                    Event e = Event.JsonToObject(data.getJSONObject(i));
                                    events.add(e);
                                    MarkerOptions markerOption = new MarkerOptions().position(new LatLng(e.getLatitude(), e.getLongitude()));
                                    //Custom icon
                                    markerOption.icon(BitmapDescriptorFactory.fromResource(General.getMarker(e.getType())));
                                    Marker currentMarker = mGoogleMap.addMarker(markerOption);
                                    marker_info.put(currentMarker, e);
                                }
                                eventListAdapter.update(events);
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
        /*ArrayList<Event> events = new ArrayList<>();
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

            Geocoder geocoder = new Geocoder(mContext.getApplicationContext());
            try {
                List<Address> addressList = geocoder.getFromLocation(la,lo,1);
                String str = addressList.get(0).getLocality()+",";
                str += addressList.get(0).getCountryName();
                if (!type_name.equals("")){
                    events.add(new Event(i,new User(),la,lo, str,new Date(),0,type_name,new ArrayList<>()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        JoinEventDialog joinEventDialog = new JoinEventDialog(mContext, "",marker_info.get(marker));
        joinEventDialog.show();
        return false;
    }

    private void reDrawList(boolean isOpen){
        this.isListOpen = isOpen;
        toggleGPSTrack(!isOpen);
        this.mGoogleMap.getUiSettings().setScrollGesturesEnabled(!isOpen);
        ValueAnimator anim = isOpen ? ValueAnimator.ofFloat(1f, 0f) : ValueAnimator.ofFloat(0f, 1f);
        if (isOpen){
            this.list_top_back.setVisibility(View.VISIBLE);
        }else {
            this.list_top_front.setVisibility(View.VISIBLE);
        }
        anim.setDuration(400);
        anim.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            int top = (int)(MOCK_TOP_HEIGHT * currentValue);
            int side = (int)(MOCK_SIDE_WIDTH * currentValue);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(side, top, side, 0);
            this.list_container.setLayoutParams(params);
            this.list_top_front.setAlpha(currentValue);
            this.list_top_back.setAlpha(1 - currentValue);
            this.event_list.setAlpha(1 - currentValue);
            this.list_top_back.requestLayout();
            this.list_top_front.requestLayout();
            this.list_container.requestLayout();
        });
        anim.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                list_top_back.setVisibility(isOpen? View.VISIBLE : View.GONE);
                list_top_front.setVisibility(isOpen? View.GONE : View.VISIBLE);
                super.onAnimationEnd(animation);
            }
        });
        anim.start();
    }

    public boolean isListOpen(){
        return isListOpen;
    }

    public void closeList(){
        reDrawList(false);
    }

    @SuppressLint("MissingPermission")
    public void toggleGPSTrack(boolean isOpen){
        this.mGoogleMap.getUiSettings().setMyLocationButtonEnabled(isOpen);
        this.mGoogleMap.setMyLocationEnabled(isOpen);
    }
}