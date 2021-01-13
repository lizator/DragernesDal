package com.rbyte.dragernesdal.ui.character.skill;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SkillViewModel extends ViewModel {
    private static SkillViewModel instace;
    private MutableLiveData<Integer> currentEP;
    private MutableLiveData<Boolean> update;
    private MutableLiveData<ArrayList<AbilityDTO>> kampAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> snigerAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> videnAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> alleAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> raceAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> uncommonAbilities;
    private MutableLiveData<ArrayList<Integer>> currentAbilityIDs;
    private Handler uiThread = new Handler();
    private AbilityRepository abilityRepo = AbilityRepository.getInstance();

    public static SkillViewModel getInstance(){
        if (instace == null) instace = new SkillViewModel();
        return instace;
    }

    private SkillViewModel() {
        currentEP = new MutableLiveData<>();
        update = new MutableLiveData<>();
        kampAbilities = new MutableLiveData<>();
        snigerAbilities = new MutableLiveData<>();
        videnAbilities = new MutableLiveData<>();
        alleAbilities = new MutableLiveData<>();
        raceAbilities = new MutableLiveData<>();
        uncommonAbilities = new MutableLiveData<>();
        currentAbilityIDs = new MutableLiveData<>();

        updateUncommon();
        updateKamp();
        updateSniger();
        updateViden();
        updateAlle();

    }

    public void reset(){
        raceAbilities.setValue(null);
    }

    public void updateKamp(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("kamp");
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        kampAbilities.postValue(data);
                    }
                }
            });
        });
    }

    public void updateSniger() {
        Executor bgThread2 = Executors.newSingleThreadExecutor();
        bgThread2.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("sniger");
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        snigerAbilities.postValue(data);
                    }
                }
            });
        });
    }

    public void updateViden() {
        Executor bgThread3 = Executors.newSingleThreadExecutor();
        bgThread3.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("viden");
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        videnAbilities.postValue(data);
                    } else {
                    }
                }
            });
        });
    }

    public void updateAlle() {
        Executor bgThread4 = Executors.newSingleThreadExecutor();
        bgThread4.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("allemand");
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        alleAbilities.postValue(data);
                        System.out.println("Alle abilities");
                    }
                }
            });
        });
    }

    public void updateUncommon() {
        Executor bgThread5 = Executors.newSingleThreadExecutor();
        bgThread5.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getAllUnCommonAbilities();
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        uncommonAbilities.postValue(data);
                        System.out.println("Alle uncommon abilities");
                    }
                }
            });
        });
    }


    public void setRaceAbilities(int raceID){
        Executor bgThread1 = Executors.newSingleThreadExecutor();
        bgThread1.execute(() -> {
            Result<List<AbilityDTO>> res = abilityRepo.getRaceAbilities(raceID);
            uiThread.post(() -> {
                if (res instanceof Result.Success) {
                    ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                    if (data != null) {
                        raceAbilities.postValue(data);
                    } else {
                        setRaceAbilities(raceID);
                    }
                }
            });
        });
    }


    public MutableLiveData<ArrayList<AbilityDTO>> getKampAbilities() {
        return kampAbilities;
    }

    public MutableLiveData<ArrayList<AbilityDTO>> getSnigerAbilities() {
        return snigerAbilities;
    }

    public MutableLiveData<ArrayList<AbilityDTO>> getVidenAbilities() {
        return videnAbilities;
    }

    public MutableLiveData<ArrayList<AbilityDTO>> getAlleAbilities() {
        return alleAbilities;
    }

    public MutableLiveData<ArrayList<AbilityDTO>> getRaceAbilities() {
        return raceAbilities;
    }

    public MutableLiveData<ArrayList<AbilityDTO>> getUncommonAbilities() {return uncommonAbilities;}

    public MutableLiveData<Integer> getCurrentEP() {
        return currentEP;
    }

    public void setCurrentEP(int currentEP) {
        this.currentEP.setValue(currentEP);
    }

    public MutableLiveData<Boolean> getUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update.setValue(update);
    }

    public MutableLiveData<ArrayList<Integer>> getCurrentAbilityIDs() {
        return currentAbilityIDs;
    }
}