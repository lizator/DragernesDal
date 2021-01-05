package com.rbyte.dragernesdal.ui.character.skill.sniger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SnigerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SnigerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is skill fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}