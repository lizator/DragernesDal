package com.rbyte.dragernesdal.ui.login;

import android.content.Context;
import android.content.SharedPreferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoginHandler { // for saved login
    static final String PREF_USER_EMAIL= "userEmail";
    static final String PREF_PASS_HASH= "userPassHash";
    public static final int LOGOUT_CODE = 1;
    private Context ctx;

    public LoginHandler(Context ctx) {
        this.ctx = ctx;
    }


    public void setUser(String email, String passHash) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_EMAIL, email);
        editor.putString(PREF_PASS_HASH, passHash);
        editor.commit();
    }

    public String getEmail() {
        return getDefaultSharedPreferences(this.ctx).getString(PREF_USER_EMAIL, "");
    }

    public String getPasshash() {
        return getDefaultSharedPreferences(this.ctx).getString(PREF_PASS_HASH, "");
    }


    public void clear()
    {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(this.ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }
}
