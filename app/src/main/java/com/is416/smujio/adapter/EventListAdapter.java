package com.is416.smujio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.is416.smujio.R;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.General;

import java.util.ArrayList;

/**
 * Created by Gods on 3/13/2018.
 */

public class EventListAdapter extends BaseAdapter{

    private Context mContext;
    private String master;
    private ArrayList<Event> events;

    public EventListAdapter(Context mContext, String master, ArrayList<Event> events) {
        this.mContext = mContext;
        this.master = master;
        this.events = events;
    }

    @Override
    public int getCount() {
        return this.events.size();
    }

    @Override
    public Event getItem(int i) {
        return this.events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.events.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        Event e = events.get(i);
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.event_list_item, viewGroup,false);
            holder = new ViewHolder();
            holder.event_icon = (ImageView)view.findViewById(R.id.imageType);
            holder.type = (TextView) view.findViewById(R.id.type);
            holder.location = (TextView) view.findViewById(R.id.location);
            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }

        holder.event_icon.setImageResource(General.getMarker(e.getType()));
        holder.type.setText(e.getType());
        holder.location.setText(e.getLocation());

        return view;
    }

    public void updateOne(Event event){
        this.events.add(event);
        notifyDataSetChanged();
    }

    public void removeOne(long id){
        for(int i = 0; i < events.size(); i++){
            if (events.get(i).getId() == id){
                events.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void update(ArrayList<Event> arrayList){
        this.events = arrayList;
        notifyDataSetChanged();
    }

    private static class ViewHolder{
        ImageView event_icon;
        TextView type;
        TextView location;
    }
}
