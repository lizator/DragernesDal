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
import java.util.concurrent.atomic.AtomicReference;

public class SkillViewModel extends ViewModel {
    private static SkillViewModel instace;
    private MutableLiveData<Integer> currentEP;
    private MutableLiveData<Boolean> update;
    private MutableLiveData<ArrayList<AbilityDTO>> kampAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> snigerAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> videnAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> alleAbilities;
    private MutableLiveData<ArrayList<AbilityDTO>> raceAbilities;
    private MutableLiveData<ArrayList<Integer>> currentAbilityIDs;
    private boolean[] updatesNeededIn = new boolean[]{true, true, true, true};
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
        currentAbilityIDs = new MutableLiveData<>();

        Executor bgThread5 = Executors.newSingleThreadExecutor();
        bgThread5.execute(() -> {
            AtomicReference<Boolean> needUpdate = new AtomicReference<>(true);
            while (needUpdate.get()) {
                needUpdate.set(false);
                if (updatesNeededIn[0]) {
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("kamp");
                        uiThread.post(() -> {

                            if (res instanceof Result.Success) {
                                ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                                if (data != null) {
                                    kampAbilities.postValue(data);
                                    updatesNeededIn[0] = false;
                                } else {
                                    needUpdate.set(true);
                                    kampAbilities = null;
                                }
                            }
                        });
                    });
                }

                if (updatesNeededIn[1]) {
                    Executor bgThread2 = Executors.newSingleThreadExecutor();
                    bgThread2.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("sniger");
                        uiThread.post(() -> {

                            if (res instanceof Result.Success) {
                                ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                                if (data != null) {
                                    snigerAbilities.postValue(data);
                                    updatesNeededIn[1] = false;
                                } else {
                                    needUpdate.set(true);
                                    snigerAbilities = null;
                                }
                            }
                        });
                    });
                }


                if (updatesNeededIn[2]) {
                    Executor bgThread3 = Executors.newSingleThreadExecutor();
                    bgThread3.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("viden");
                        uiThread.post(() -> {

                            if (res instanceof Result.Success) {
                                ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                                if (data != null) {
                                    videnAbilities.postValue(data);
                                    updatesNeededIn[2] = false;
                                } else {
                                    needUpdate.set(true);
                                    videnAbilities = null;
                                }
                            }
                        });
                    });
                }


                if (updatesNeededIn[3]) {
                    Executor bgThread4 = Executors.newSingleThreadExecutor();
                    bgThread4.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.getTypeAbilities("allemand");
                        uiThread.post(() -> {

                            if (res instanceof Result.Success) {
                                ArrayList<AbilityDTO> data = (ArrayList<AbilityDTO>) ((Result.Success) res).getData();
                                if (data != null) {
                                    alleAbilities.postValue(data);
                                    updatesNeededIn[3] = false;
                                } else {
                                    needUpdate.set(true);
                                    alleAbilities = null;
                                }
                            }
                        });
                    });
                }

            }
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