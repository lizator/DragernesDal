package com.example.dragernesdal.ui.character.skill;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SkillViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SkillViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is skill fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}