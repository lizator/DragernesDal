package com.example.dragernesdal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoginHandler {
    static final String PREF_USER_NAME= "userID";
    private Context ctx;

    public LoginHandler(Context ctx) {
        this.ctx = ctx;
    }


    public void setUserName(String userID)
    {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userID);
        editor.commit();
    }

    public String getUserID()
    {
        return getDefaultSharedPreferences(this.ctx).getString(PREF_USER_NAME, "");
    }

    public void clear()
    {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(this.ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }
}
