package com.is416.smujio.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.is416.smujio.R;
import com.is416.smujio.component.LoadingButton;
import com.is416.smujio.model.Event;

/**
 * Created by Gods on 3/14/2018.
 */

public class JoinEventDialog extends Dialog {

    private Event event;
    private Context mContext;
    private String master;
    private TextView type;
    private TextView location;
    private TextView group_size;
    private LoadingButton join_bt;

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
        this.type = findViewById(R.id.type);
        this.location = findViewById(R.id.location);
        this.group_size = findViewById(R.id.group_size);
        this.join_bt = findViewById(R.id.join);
    }

    private void init(){
        type.setText(event.getType());
        location.setText(event.getLocation());
        //group_size.setText(event.getParticipants().size());
        join_bt.setText(mContext.getResources().getString(R.string.join_event_title));
    }

    private void addListener(){

    }
}
