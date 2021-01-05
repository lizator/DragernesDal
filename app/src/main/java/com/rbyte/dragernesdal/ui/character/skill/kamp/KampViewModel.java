package com.rbyte.dragernesdal.ui.character.skill.kamp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KampViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public KampViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is skill fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}