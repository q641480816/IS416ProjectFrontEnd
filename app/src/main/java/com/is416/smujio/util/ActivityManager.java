package com.is416.smujio.util;

import android.app.Activity;

import java.util.HashMap;

/**
 * Created by PETER LU on 26/2/2018.
 */

public class ActivityManager {

    private static HashMap<String ,Activity> activities;

    public static void add(String name, Activity ac){
        if(activities == null){
            activities = new HashMap<>();
        }

        activities.put(name, ac);
    }

    public static Activity getAc(String name){
        return activities.get(name);
    }

    public static void remove(String name){
        activities.remove(name);
    }
}
