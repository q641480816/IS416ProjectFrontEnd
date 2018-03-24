package com.is416.smujio.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.is416.smujio.EventActivity;
import com.is416.smujio.component.fragment.EventMainFragment;
import com.is416.smujio.model.Event;

/**
 * Created by Gods on 3/23/2018.
 */

public class EventFragmentAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 1;
    private EventMainFragment eventMainFragment;

    public EventFragmentAdapter(FragmentManager fm) {
        super(fm);
        eventMainFragment = new EventMainFragment();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case EventActivity.PAGE_ONE:
                fragment = eventMainFragment;
                break;
        }
        return fragment;
    }
}
