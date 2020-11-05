package com.example.dragernesdal.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoginHandler { // for saved login
    static final String PREF_USER_EMAIL= "userEmail";
    static final String PREF_PASS_HASH= "userPassHash";
    static final String PREF_DISPLAY_NAME= "userDisplayName";
    public static final int LOGOUT_CODE = 1;
    private SharedPreferences prefs;

    public LoginHandler(Context ctx) {
        prefs = ctx.getSharedPreferences("login_pref", ctx.MODE_MULTI_PROCESS);
    }


    public void setUser(String email, String passHash, String displayName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_USER_EMAIL, email);
        editor.putString(PREF_PASS_HASH, passHash);
        editor.putString(PREF_DISPLAY_NAME, displayName);
        editor.commit();
    }

    public String getEmail() {
        return prefs.getString(PREF_USER_EMAIL, "");
    }

    public String getPasshash() {
        return prefs.getString(PREF_PASS_HASH, "");
    }

    public String getDisplayName() {
        return prefs.getString(PREF_DISPLAY_NAME, "");
    }


    public void clear()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }
}
