package com.is416.smujio;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.is416.smujio.adapter.JioFragmentPagerAdapter;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;

public class JioActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private Intent pIntent;
    private Context mContext;
    public static final String name = "MAIN";
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;

    private JioFragmentPagerAdapter jioFragmentPagerAdapter;
    private ViewPager main_content;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jio);

        this.pIntent = getIntent();
        this.mContext = this;

        init();
        bindView();
        addListeners();
    }

    private void init(){
        ActivityManager.add(name, this);
        this.jioFragmentPagerAdapter = new JioFragmentPagerAdapter(getSupportFragmentManager());

        //TODO: Welcome new user
        System.out.println(this.pIntent.getBooleanExtra("isNew",false));
    }

    private void bindView(){
        this.main_content = findViewById(R.id.viewPager);
        this.bottomNavigationView = findViewById(R.id.bt_nav);
        this.main_content.setAdapter(this.jioFragmentPagerAdapter);

        this.main_content.setCurrentItem(0);
    }

    private void addListeners(){
        this.main_content.addOnPageChangeListener(this);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (main_content.getCurrentItem()) {
                case PAGE_ONE:
                    this.bottomNavigationView.setSelectedItemId(R.id.menu_nav_map);
                    break;
                case PAGE_TWO:
                    this.bottomNavigationView.setSelectedItemId(R.id.menu_nav_pair);
                    break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_nav_map:
                this.main_content.setCurrentItem(PAGE_ONE);
                break;
            case R.id.menu_nav_pair:
                this.main_content.setCurrentItem(PAGE_TWO);
            break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        ActivityManager.remove(name);
    }

    public Context getContext(){
        return mContext;
    }
}

