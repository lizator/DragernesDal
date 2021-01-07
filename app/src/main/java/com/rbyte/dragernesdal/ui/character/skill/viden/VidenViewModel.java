package com.rbyte.dragernesdal.ui.character.skill.viden;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VidenViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VidenViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is skill fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}