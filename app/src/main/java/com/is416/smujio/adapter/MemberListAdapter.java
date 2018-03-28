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
import com.is416.smujio.model.User;
import com.is416.smujio.util.General;

import java.util.ArrayList;

/**
 * Created by Gods on 3/23/2018.
 */

public class MemberListAdapter extends BaseAdapter {

    private Context mContext;
    private String master;
    private ArrayList<User> userArrayList;
    private long event_id;

    public MemberListAdapter(Context mContext, String master, ArrayList<User> userArrayList, long event_id) {
        this.mContext = mContext;
        this.master = master;
        this.userArrayList = userArrayList;
        this.event_id = event_id;
    }

    @Override
    public int getCount() {
        return this.userArrayList.size();
    }

    @Override
    public User getItem(int i) {
        return this.userArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getAccountId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        User user = userArrayList.get(i);
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.member_list_item, viewGroup,false);
            holder = new ViewHolder();
            //holder.event_icon = null;
            holder.admin_tag = view.findViewById(R.id.admin_tag);
            holder.avatar = view.findViewById(R.id.profile_icon);
            holder.name = view.findViewById(R.id.name);
            holder.email = view.findViewById(R.id.email);
            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }

        holder.admin_tag.setVisibility(user.getAccountId() == this.event_id ? View.VISIBLE : View.INVISIBLE);
        holder.name.setText(user.getNickName());
        holder.email.setText(user.getEmail());
        if (!user.getAvatar().equals("null")) {
            holder.avatar.setImageBitmap(General.getCroppedBitmap(General.base64ToBitmap(user.getAvatar())));
        }
        return view;
    }

    public void update(ArrayList<User> arrayList){
        this.userArrayList = arrayList;
        notifyDataSetChanged();
    }

    private static class ViewHolder{
        ImageView admin_tag;
        ImageView avatar;
        TextView name;
        TextView email;
    }
}
