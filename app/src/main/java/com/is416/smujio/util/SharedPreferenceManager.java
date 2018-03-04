package com.is416.smujio.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Gods on 2/10/2018.
 */

public class SharedPreferenceManager {

    private static final String name = "Jio";
    public static final String nullable = "null";

    public static void save(String key, String value, Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveMultiple(HashMap<String,String> values, Context context){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> keys = values.keySet();
        for (String k : keys){
            editor.putString(k, values.get(k));
        }
        editor.apply();
    }

    public static String get(String key, Context mContext){
        String out;
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        out = sp.getString(key, nullable);
        return out;
    }

    public static void remove(String key, Context mContext){
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }
}
