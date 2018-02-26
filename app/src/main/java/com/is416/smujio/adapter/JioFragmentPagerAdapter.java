package com.is416.smujio.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.is416.smujio.MainActivity;
import com.is416.smujio.component.fragment.MapFragment;
import com.is416.smujio.component.fragment.PairFragment;

/**
 * Created by Gods on 2/26/2018.
 */

public class JioFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 2;
    private MapFragment mapFragment;
    private PairFragment pairFragment;

    public JioFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        mapFragment = new MapFragment();
        pairFragment = new PairFragment();
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
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = mapFragment;
                break;
            case MainActivity.PAGE_TWO:
                fragment = pairFragment;
                break;
        }
        return fragment;
    }
}