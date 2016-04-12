package com.elgroup.givvruk.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * Created by windows 7 on 1/12/2016.
 */
public class SharedP_Result {
    public static final String PREFS_NAME = "USER_PREFS";
    public static final String VALUE_KEY = "USER_INFO";
    Context context;
    public SharedP_Result(Context context) {
        super();
        this.context=context;
    }

    public void save(String text) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(VALUE_KEY, text); //3

        editor.commit(); //4
    }

    public String getValue() {
        SharedPreferences settings;
        String text;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(VALUE_KEY, null);
        return text;
    }

    public void clearSharedPreference() {
        SharedPreferences settings;
        Editor editor;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

}