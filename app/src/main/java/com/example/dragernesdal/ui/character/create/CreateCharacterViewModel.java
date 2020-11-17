package com.example.dragernesdal.ui.character.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateCharacterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CreateCharacterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is create character fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}