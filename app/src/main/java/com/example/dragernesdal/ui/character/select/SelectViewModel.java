package com.example.dragernesdal.ui.character.select;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SelectViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SelectViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is select fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}