package com.rbyte.dragernesdal.ui.character.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventoryViewModel extends ViewModel {
    private static InventoryViewModel instance;

    public static InventoryViewModel getInstance(){
        if (instance == null) instance = new InventoryViewModel();
        return instance;
    }

    private InventoryViewModel() {
    }

}