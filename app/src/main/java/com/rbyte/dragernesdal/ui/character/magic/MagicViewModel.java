package com.rbyte.dragernesdal.ui.character.magic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MagicViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MagicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is magic fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}