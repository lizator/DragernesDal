package com.rbyte.dragernesdal.ui;

import androidx.appcompat.app.AlertDialog;

public class PopupHandler {
    private static PopupHandler instance;

    public static PopupHandler getInstance(){
        if (instance == null) instance = new PopupHandler();
        return instance;
    }

    private PopupHandler(){
    }

    public void buildAbilityPopup(android.content.Context context, String command){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

    }

    

}
