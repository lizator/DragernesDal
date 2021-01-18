package com.rbyte.dragernesdal.ui.character.background;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackgroundViewModel extends ViewModel {

    private static BackgroundViewModel instance;

    private MutableLiveData<ArrayList<AbilityDTO>> badAbilities;
    private Handler uiThread = new Handler();
    private AbilityRepository abilityRepo = AbilityRepository.getInstance();
    private MutableLiveData<Boolean> update;

    public static BackgroundViewModel getInstance(){
        if (instance == null) instance = new BackgroundViewModel();
        return instance;
    }

    private BackgroundViewModel() {
        badAbilities = new MutableLiveData<>();
        update = new MutableLiveData<>();
        updateBad();
    }

    public void updateBad(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("bad");
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        badAbilities.postValue(data);
                        return;
                    }
                }
                updateBad();
            });
        });
    }

    public LiveData<ArrayList<AbilityDTO>> getBadAbilities() {
        return badAbilities;
    }

    public LiveData<Boolean> getUpdate(){
        return update;
    }

    public void postUpdate(boolean update){
        this.update.postValue(update);
    }
}