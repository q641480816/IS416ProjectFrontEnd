package com.is416.smujio;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.is416.smujio.adapter.JioFragmentPagerAdapter;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;

public class JioActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private Intent pIntent;
    private Context mContext;
    public static final String name = "MAIN";
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;

    private JioFragmentPagerAdapter jioFragmentPagerAdapter;
    private ViewPager main_content;
    private RadioGroup bt_tab_bar;
    private RadioButton tb_map;
    private RadioButton tb_init;
    private RadioButton tb_pair;

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
        this.bt_tab_bar = findViewById(R.id.bt_tab_bar);
        this.tb_map = findViewById(R.id.tb_map);
        this.tb_pair = findViewById(R.id.tb_pair);
        this.tb_init = findViewById(R.id.tb_init);

        this.bt_tab_bar.setOnCheckedChangeListener(this);
        this.main_content.setAdapter(this.jioFragmentPagerAdapter);
        this.main_content.setCurrentItem(0);
        this.main_content.addOnPageChangeListener(this);
        this.tb_map.setChecked(true);
    }

    private void addListeners(){

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
            System.out.println("test" + main_content.getCurrentItem());
            switch (main_content.getCurrentItem()) {
                case PAGE_ONE:
                    tb_map.setChecked(true);
                    break;
                case PAGE_TWO:
                    tb_pair.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.tb_map:
                this.main_content.setCurrentItem(PAGE_ONE);
                break;
            case R.id.tb_pair:
                this.main_content.setCurrentItem(PAGE_TWO);
                break;
            case R.id.tb_init:
                General.makeToast(mContext, "Init event");
                break;
        }
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

