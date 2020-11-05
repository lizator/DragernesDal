package com.example.dragernesdal.ui.character.background;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BackgroundViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BackgroundViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is background fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}