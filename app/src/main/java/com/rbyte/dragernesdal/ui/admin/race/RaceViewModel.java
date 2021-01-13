package com.rbyte.dragernesdal.ui.admin.race;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.race.RaceDAO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RaceViewModel extends ViewModel {
    private static RaceViewModel instace;
    private Handler uiThread = new Handler();

    public static RaceViewModel getInstance(){
        if (instace == null) instace = new RaceViewModel();
        return instace;
    }

    private RaceViewModel() {

    }

    public void createRace(RaceDTO dto){
        RaceDAO dao = new RaceDAO();
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            dao.createRace(dto);
        });
    }



}