package com.is416.smujio.component.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.is416.smujio.EventActivity;
import com.is416.smujio.JioActivity;
import com.is416.smujio.R;
import com.is416.smujio.adapter.MemberListAdapter;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gods on 3/23/2018.
 */

public class EventMainFragment extends Fragment{

    private Context mContext;
    private String master = "EVENT";
    private View mainView;
    private LayoutInflater inflater;
    private Event event;

    private MemberListAdapter memberListAdapter;
    private ListView user_list;
    private TextView location;
    private TextView members;
    private TextView leave;
    private TextView eventType;
    private ImageView imageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.event_main_fragment, container, false);
        this.inflater = inflater;

        this.mContext = ((EventActivity) ActivityManager.getAc(master)).getContext();
        bindView();
        init();
        addListener();

        return mainView;
    }

    private void bindView(){
        this.user_list = mainView.findViewById(R.id.user_list);
        this.location = mainView.findViewById(R.id.location);
        this.members = mainView.findViewById(R.id.group_size);
        this.leave = mainView.findViewById(R.id.leave);
        this.imageView = mainView.findViewById(R.id.imageType);
        this.eventType = mainView.findViewById(R.id.eventType);
    }

    private void init(){
        this.event = ((EventActivity) ActivityManager.getAc(master)).getEvent();
        this.memberListAdapter = new MemberListAdapter(mContext,master,this.event.getParticipants(),this.event.getId());
        this.user_list.setAdapter(this.memberListAdapter);
        this.location.setText(this.event.getLocation());
        this.members.setText(" " + this.event.getParticipantsCount());
        this.imageView.setImageResource(General.getMarker(this.event.getType()));
        this.eventType.setText(event.getType());
    }

    private void addListener(){
        this.leave.setOnClickListener((v)->{
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(General.user.getAccountId() == this.event.getId() ? R.string.event_leave_group_body_admin : R.string.event_leave_group_body)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {
                        // FIRE ZE MISSILES!
                        leaveGroup();
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }).create().show();
        });

        this.location.setOnClickListener((v)->{
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+this.event.getLatitude()+","+this.event.getLongitude()));
            startActivity(intent);
        });
    }

    private void leaveGroup(){
        String url = "/event";

        JSONObject body = new JSONObject();
        try {
            if (General.user.getAccountId() == this.event.getId()){
                body.put(General.ID, this.event.getId());
                body.put(General.EVENTSTATUS, General.EVENT_STATUS_STOPPED);
            }else {
                body.put(General.LEAVE, "daads");
                body.put(General.ID, this.event.getId());
                body.put(General.ACCOUNTID, General.user.getAccountId());
            }

            General.httpRequest(mContext,General.HTTP_PUT,url, body, false, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        switch (response.getInt(General.HTTP_STATUS_KEY)){
                            case General.HTTP_SUCCESS:
                                finish();
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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(){
        this.event = ((EventActivity) ActivityManager.getAc(master)).getEvent();
        this.memberListAdapter.update(this.event.getParticipants());
        this.members.setText(" " + this.event.getParticipantsCount());
    }

    private void finish(){
        ((EventActivity) ActivityManager.getAc(master)).finish();
    }

}
