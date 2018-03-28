package com.is416.smujio.util;

import android.app.Activity;

import java.util.HashMap;
import java.util.Set;

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

    public static void emptyStack(){
        if(activities != null){
            Set<String> names = activities.keySet();
            for (String name : names) {
                activities.get(name).finish();;
                activities.remove(name);
            }
        }
        System.gc();
    }
}
