package com.rbyte.dragernesdal.ui;

public class PopupHandler {
    private static PopupHandler instance;

    public static PopupHandler getInstance(){
        if (instance == null) instance = new PopupHandler();
        return instance;
    }

    private PopupHandler(){

    }


    

}
