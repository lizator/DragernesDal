package com.rbyte.dragernesdal.ui.admin.race;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.event.model.AttendingDTO;
import com.rbyte.dragernesdal.data.race.RaceDAO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;
import com.rbyte.dragernesdal.ui.event.EventViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RaceViewModel extends ViewModel {
    private static RaceViewModel instace;
    private Handler uiThread = new Handler();
    private MutableLiveData<List<RaceDTO>> mRaces;

    public static RaceViewModel getInstance(){
        if (instace == null) instace = new RaceViewModel();
        return instace;
    }

    private RaceViewModel() {
        mRaces = new MutableLiveData<>();
    }

    public LiveData<List<RaceDTO>> getRaces() {
        return mRaces;
    }

    public void startGetCustomThread() {
        GetRacesThread thread = new GetRacesThread();

        thread.start();

    }

    public void createRace(RaceDTO dto){
        RaceDAO dao = new RaceDAO();
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            dao.createRace(dto);
        });
    }

    public void updateRace(RaceDTO dto){
        RaceDAO dao = new RaceDAO();
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            dao.updateRace(dto);
        });
    }

    class GetRacesThread extends Thread {
        public GetRacesThread() {
        }

        @Override
        public void run() {
            RaceDAO dao = new RaceDAO();
            Result result = dao.getRaceInfoCustom();
            ArrayList<RaceDTO> lst = ((Result.Success<ArrayList<RaceDTO>>) result).getData();
            mRaces.postValue(lst);
            System.out.println("Getting races");
        }
    }



}