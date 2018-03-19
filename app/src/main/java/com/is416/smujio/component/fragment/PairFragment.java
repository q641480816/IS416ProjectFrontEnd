package com.is416.smujio.component.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.is416.smujio.R;
import com.is416.smujio.SensorActivity;


/**
 * Created by Gods on 2/26/2018.
 */

public class PairFragment extends Fragment {

    private Context mContext;
    private LayoutInflater inflater;

    private View mainView;
    private ImageView mTopLine;
    private ImageView mBottomLine;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.pair_fragment, container, false);
        this.inflater = inflater;

//        mTopLine= getView().findViewById(R.id.main_shake_top_line);
//        mBottomLine =  getView().findViewById(R.id.main_shake_bottom_line);
//
//        //default
//        mTopLine.setVisibility(View.GONE);
//        mBottomLine.setVisibility(View.GONE);

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


        Intent intent = new Intent(getActivity(), SensorActivity.class);
        startActivity(intent);
    }


}
