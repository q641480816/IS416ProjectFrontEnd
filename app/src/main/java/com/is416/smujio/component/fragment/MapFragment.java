package com.is416.smujio.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.is416.smujio.R;

/**
 * Created by Gods on 2/26/2018.
 */

public class MapFragment extends Fragment{

    private Context mContext;
    private LayoutInflater inflater;

    private View mainView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.map_fragment, container, false);
        this.inflater = inflater;

        init();
        bindView();
        addListener();
        return mainView;
    }

    private void bindView() {

    }

    private void addListener() {

    }

    private void init() {

    }
}
