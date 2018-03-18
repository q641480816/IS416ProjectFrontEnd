package com.is416.smujio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.is416.smujio.R;

import java.util.ArrayList;

/**
 * Created by Gods on 3/15/2018.
 */

public class EventTypeSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private String master;
    private ArrayList<String> types;

    public EventTypeSpinnerAdapter(Context mContext, String master){
        this.master = master;
        this.mContext = mContext;
        this.types = new ArrayList<>();
        types.add("MOVIE");
        types.add("BAR");
        types.add("DINE");
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public String getItem(int i) {
        return types.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.type_spinner, viewGroup,false);
            holder = new ViewHolder();
            holder.type = (TextView) view.findViewById(R.id.type);
            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }

        holder.type.setText(types.get(i));
        return view;
    }

    private static class ViewHolder{
        TextView type;
    }
}
