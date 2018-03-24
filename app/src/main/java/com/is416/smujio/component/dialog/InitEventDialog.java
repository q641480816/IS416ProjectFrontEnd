package com.is416.smujio.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.is416.smujio.EventActivity;
import com.is416.smujio.JioActivity;
import com.is416.smujio.R;
import com.is416.smujio.adapter.EventTypeSpinnerAdapter;
import com.is416.smujio.component.LoadingButton;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gods on 3/15/2018.
 */

public class InitEventDialog extends Dialog implements AdapterView.OnItemSelectedListener, Runnable{

    private Context mContext;
    private String master;
    private LinearLayout main_view;
    private Location location_data;
    private String location_name;
    private Spinner type;
    private TextView location;
    private LoadingButton init_bt;
    private ImageView close;
    private EventTypeSpinnerAdapter eventTypeSpinnerAdapter;
    private Geocoder geocoder;

    private String selectedType;

    public InitEventDialog(@NonNull Context context, String master) {
        super(context);

        this.mContext = context;
        this.master = master;
    }

    public InitEventDialog(@NonNull Context context, int themeResId, String master) {
        super(context, themeResId);

        this.mContext = context;
        this.master = master;
    }

    protected InitEventDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, String master) {
        super(context, cancelable, cancelListener);

        this.mContext = context;
        this.master = master;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_event_dialog);
        setCanceledOnTouchOutside(false);

        bindView();
        init();
        addListener();
    }

    private void bindView(){
        this.main_view = findViewById(R.id.main_view);
        this.type = findViewById(R.id.type);
        this.location = findViewById(R.id.location);
        this.init_bt = findViewById(R.id.init);
        this.close = findViewById(R.id.close);
    }

    private void init(){
        //setCancelable(true);
        this.geocoder = new Geocoder(mContext.getApplicationContext());
        this.eventTypeSpinnerAdapter = new EventTypeSpinnerAdapter(mContext, master);
        main_view.setLayoutParams(new FrameLayout.LayoutParams((int)(General.METRIC_WIDTH*0.9), ViewGroup.LayoutParams.MATCH_PARENT));

        type.setAdapter(this.eventTypeSpinnerAdapter);
        type.setOnItemSelectedListener(this);
        type.setSelection(0);
        init_bt.setText(mContext.getResources().getString(R.string.init_event_start));
        Thread tN = new Thread(this);
        tN.run();
    }

    private void addListener(){
        this.close.setOnClickListener((v)->{
            this.dismiss();
            System.gc();
        });

        this.init_bt.setOnClickListener((v)->{
            this.init_bt.setLoading(true);
            String url = "/event";

            JSONObject body = new JSONObject();
            try {
                body.put(General.ACCOUNTID, General.user.getAccountId());
                body.put(General.LATITUDE, location_data.getLatitude());
                body.put(General.LONGITUDE, location_data.getLongitude());

                body.put(General.LOCATION, location_name);
                body.put(General.TYPE,this.selectedType);
                General.httpRequest(mContext,General.HTTP_POST,url, body, false, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            switch (response.getInt(General.HTTP_STATUS_KEY)){
                                case General.HTTP_SUCCESS:
                                    Event newEvent = Event.JsonToObject(((JSONObject)response.get(General.HTTP_DATA_KEY)).getJSONObject(General.EVENT));
                                    General.currentEvent = newEvent;
                                    Intent intent = new Intent(mContext, EventActivity.class);
                                    dismiss();
                                    ((JioActivity) ActivityManager.getAc(JioActivity.name)).startActivityForResult(intent,JioActivity.EVENT_DETAIL_REQUEST_CODE);
                                    break;
                                case General.HTTP_EXCEPTION:
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                    init_bt.setLoading(false);
                                    break;
                                case General.HTTP_FAIL:
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                    init_bt.setLoading(false);
                                    break;
                            }
                        } catch (Exception e) {
                            init_bt.setLoading(false);
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                init_bt.setLoading(false);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.selectedType = ((TextView)view.findViewById(R.id.type)).getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void run() {
        //init location
        try {
            location_data = ((JioActivity) ActivityManager.getAc("MAIN")).getLastKnownLocation();
            List<Address> addressList = geocoder.getFromLocation(location_data.getLatitude(), location_data.getLongitude(), 1);
            this.location_name = addressList.get(0).getAddressLine(0).split(",")[0];
            this.location.setText(this.location_name);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
