package com.is416.smujio.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.is416.smujio.EventActivity;
import com.is416.smujio.JioActivity;
import com.is416.smujio.R;
import com.is416.smujio.component.LoadingButton;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gods on 3/14/2018.
 */

public class JoinEventDialog extends Dialog {

    private Event event;
    private Context mContext;
    private String master;
    private LinearLayout main_view;
    private TextView type;
    private TextView location;
    private TextView group_size;
    private LoadingButton join_bt;
    private ImageView close;

    public JoinEventDialog(@NonNull Context context, String master, Event event) {
        super(context);

        this.mContext = context;
        this.master = master;
        this.event = event;
    }

    public JoinEventDialog(@NonNull Context context, int themeResId, String master, Event event) {
        super(context, themeResId);

        this.mContext = context;
        this.master = master;
        this.event = event;
    }

    protected JoinEventDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, String master, Event event) {
        super(context, cancelable, cancelListener);

        this.mContext = context;
        this.master = master;
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_event_dialog);
        setCanceledOnTouchOutside(false);

        bindView();
        init();
        addListener();
    }

    private void bindView(){
        this.main_view = findViewById(R.id.main_view);
        this.type = findViewById(R.id.type);
        this.location = findViewById(R.id.location);
        this.group_size = findViewById(R.id.group_size);
        this.join_bt = findViewById(R.id.join);
        this.close = findViewById(R.id.close);
    }

    private void init(){
        //setCancelable(true);
        main_view.setLayoutParams(new FrameLayout.LayoutParams((int)(General.METRIC_WIDTH*0.9), ViewGroup.LayoutParams.MATCH_PARENT));
        type.setText(event.getType());
        location.setText(event.getLocation());
        group_size.setText(event.getParticipantsCount() + "Person(s)");
        join_bt.setText(mContext.getResources().getString(R.string.join_event_title));
    }

    private void addListener(){
        this.close.setOnClickListener((v)->{
            this.dismiss();
        });

        this.join_bt.setOnClickListener((v)->{
            this.join_bt.setLoading(true);
            String url = "/event";

            JSONObject body = new JSONObject();
            try {
                body.put(General.ACCOUNTID, General.user.getAccountId());
                body.put(General.ID, this.event.getId());
                General.httpRequest(mContext,General.HTTP_PUT,url, body, false, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            switch (response.getInt(General.HTTP_STATUS_KEY)){
                                case General.HTTP_SUCCESS:
                                    Event newEvent = Event.JsonToObject(((JSONObject)response.get(General.HTTP_DATA_KEY)));
                                    General.currentEvent = newEvent;
                                    Intent intent = new Intent(mContext, EventActivity.class);
                                    dismiss();
                                    ((JioActivity) ActivityManager.getAc(JioActivity.name)).startActivityForResult(intent,JioActivity.EVENT_DETAIL_REQUEST_CODE);
                                    break;
                                case General.HTTP_EXCEPTION:
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                    join_bt.setLoading(false);
                                    break;
                                case General.HTTP_FAIL:
                                    General.makeToast(mContext, response.getString(General.HTTP_MESSAGE_KEY));
                                    join_bt.setLoading(false);
                                    break;
                            }
                        } catch (Exception e) {
                            join_bt.setLoading(false);
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                join_bt.setLoading(false);
                e.printStackTrace();
            }
        });
    }
}
