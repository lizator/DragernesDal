package com.rbyte.dragernesdal.ui.character.skill;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SkillViewModel extends ViewModel {
    private static SkillViewModel instace;
    private ArrayList<AbilityDTO> kampAbilities = new ArrayList<>();
    private ArrayList<AbilityDTO> snigerAbilities = new ArrayList<>();
    private ArrayList<AbilityDTO> videnAbilities = new ArrayList<>();
    private ArrayList<AbilityDTO> alleAbilities = new ArrayList<>();
    private ArrayList<AbilityDTO> raceAbilities = new ArrayList<>();
    private Handler uiThread = new Handler();
    private AbilityRepository abilityRepo = AbilityRepository.getInstance();

    public static SkillViewModel getInstance(){
        if (instace == null) instace = new SkillViewModel();
        return instace;
    }

    private SkillViewModel() {
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            Result<List<AbilityDTO>> res =  abilityRepo.getTypeAbilities("kamp");
            uiThread.post(() -> {
               if (res instanceof Result.Success){
                   ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                   if (data != null) {
                       kampAbilities = data;
                   }
               }
            });
        });

        Executor bgThread2 = Executors.newSingleThreadExecutor();
        bgThread2.execute(() -> {
            Result<List<AbilityDTO>> res =  abilityRepo.getTypeAbilities("sniger");
            uiThread.post(() -> {
               if (res instanceof Result.Success){
                   ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                   if (data != null) {
                       snigerAbilities = data;
                   }
               }
            });
        });

        Executor bgThread3 = Executors.newSingleThreadExecutor();
        bgThread3.execute(() -> {
            Result<List<AbilityDTO>> res =  abilityRepo.getTypeAbilities("viden");
            uiThread.post(() -> {
               if (res instanceof Result.Success){
                   ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                   if (data != null) {
                       videnAbilities = data;
                   }
               }
            });
        });

        Executor bgThread4 = Executors.newSingleThreadExecutor();
        bgThread4.execute(() -> {
            Result<List<AbilityDTO>> res =  abilityRepo.getTypeAbilities("allemand");
            uiThread.post(() -> {
               if (res instanceof Result.Success){
                   ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                   if (data != null) {
                       alleAbilities = data;
                   }
               }
            });
        });

    }

    public void setRaceAbilities(int raceID){
        Executor bgThread1 = Executors.newSingleThreadExecutor();
        bgThread1.execute(() -> {
            Result<List<AbilityDTO>> res =  abilityRepo.getRaceAbilities(raceID);
            uiThread.post(() -> {
                if (res instanceof Result.Success){
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        raceAbilities = data;
                    }
                }
            });
        });
    }

    public ArrayList<AbilityDTO> getKampAbilities() {
        return kampAbilities;
    }

    public ArrayList<AbilityDTO> getSnigerAbilities() {
        return snigerAbilities;
    }

    public ArrayList<AbilityDTO> getVidenAbilities() {
        return videnAbilities;
    }

    public ArrayList<AbilityDTO> getAlleAbilities() {
        return alleAbilities;
    }

    public ArrayList<AbilityDTO> getRaceAbilities() {
        return raceAbilities;
    }
}