package com.is416.smujio.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.is416.smujio.R;
import com.is416.smujio.adapter.EventTypeSpinnerAdapter;
import com.is416.smujio.component.LoadingButton;
import com.is416.smujio.util.General;

/**
 * Created by Gods on 3/15/2018.
 */

public class InitEventDialog extends Dialog {

    private Context mContext;
    private String master;
    private LinearLayout main_view;
    private Spinner type;
    private TextView location;
    private LoadingButton init_bt;
    private ImageView close;
    private EventTypeSpinnerAdapter eventTypeSpinnerAdapter;

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
        this.eventTypeSpinnerAdapter = new EventTypeSpinnerAdapter(mContext, master);
        main_view.setLayoutParams(new FrameLayout.LayoutParams((int)(General.METRIC_WIDTH*0.9), ViewGroup.LayoutParams.MATCH_PARENT));

        type.setAdapter(this.eventTypeSpinnerAdapter);
        init_bt.setText(mContext.getResources().getString(R.string.init_event_start));
    }

    private void addListener(){
        this.close.setOnClickListener((v)->{
            this.dismiss();
        });
    }
}
