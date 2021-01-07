package com.rbyte.dragernesdal.ui.character.skill.alle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlleViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is skill fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}