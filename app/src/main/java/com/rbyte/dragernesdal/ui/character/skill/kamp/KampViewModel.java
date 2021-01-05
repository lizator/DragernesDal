package com.rbyte.dragernesdal.ui.character.skill.kamp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class KampViewModel extends ViewModel {
    private CharacterRepository characterRepo;
    private MutableLiveData<ArrayList<Integer>> mCurrAbilityIDs;

    public KampViewModel() {
        mCurrAbilityIDs = new MutableLiveData<>();
        characterRepo = CharacterRepository.getInstance();
    }

    public LiveData<ArrayList<Integer>> getCurrAbilitys() {
        return mCurrAbilityIDs;
    }

    public void updateCurrAbilities(int characterID){
        Result<List<AbilityDTO>> res = characterRepo.getAbilitiesByCharacterID(characterID);

        if (res instanceof Result.Success){
            ArrayList<AbilityDTO> data = ((Result.Success<ArrayList<AbilityDTO>>) res).getData();
            ArrayList<Integer> IDlist = new ArrayList<>();
            for (AbilityDTO dto : data) IDlist.add(dto.getId());
            mCurrAbilityIDs.postValue(IDlist);
        }
    }

    public void startGetThread(int characterID){
        GetCurrAbilitiesThread thread = new GetCurrAbilitiesThread(characterID);
        thread.start();
    }

    class GetCurrAbilitiesThread extends Thread {
        private int id;

        public GetCurrAbilitiesThread(int characterID) {
            this.id = characterID;
        }

        @Override
        public void run() {
            updateCurrAbilities(this.id);
        }
    }
}