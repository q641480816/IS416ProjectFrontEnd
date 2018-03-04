package com.is416.smujio.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.is416.smujio.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Gods on 2/26/2018.
 */

public class LoadingButton extends LinearLayout {

    private Context mContext;
    private AttributeSet attrs;
    private AVLoadingIndicatorView loadingIndicator;
    private TextView title;

    public LoadingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.loading_button, this, true);

        mContext = context;
        this.attrs = attrs;
        bindView();
        init();
    }

    private void bindView(){
        this.loadingIndicator = findViewById(R.id.loading);
        this.title = findViewById(R.id.title);
    }

    private void init(){
        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.LoadingButton);
        if (attributes != null) {
            String titleContent = attributes.getString(R.styleable.LoadingButton_text);
            if (!TextUtils.isEmpty(titleContent)) {
                setText(titleContent);
            }
            attributes.recycle();
        }
    }

    public void setText(CharSequence titleContent){
        this.title.setText(titleContent);
    }

    public String getText(){
        return this.title.getText().toString();
    }

    public void setLoading(boolean isLoading){
        this.title.setVisibility(isLoading ? GONE : VISIBLE);
        this.loadingIndicator.setVisibility(!isLoading ? GONE : VISIBLE);
    }
}
