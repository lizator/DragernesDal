package com.example.dragernesdal.ui.character;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CharViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CharViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is character fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}