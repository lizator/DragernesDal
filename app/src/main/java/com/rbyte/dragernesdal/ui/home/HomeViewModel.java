package com.rbyte.dragernesdal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CharacterDTO> mCharacter;
    private MutableLiveData<List<AbilityDTO>> mAbilities;
    private MutableLiveData<List<InventoryDTO>> mMoney;
    private MutableLiveData<List<RaceDTO>> mRace;
    private CharacterRepository repo;
    private static HomeViewModel instance;

    public static HomeViewModel getInstance(){
        if (instance == null) instance = new HomeViewModel();
        return instance;
    }

    private HomeViewModel() {
        this.mCharacter = new MutableLiveData<>();
        this.mAbilities = new MutableLiveData<>();
        this.mMoney = new MutableLiveData<>();
        this.mRace = new MutableLiveData<>();
        this.repo = CharacterRepository.getInstance();
        //initialing observers
    }

    /*public void updateCurrentCharacter(){
        if (mCharacter.getValue() != null)
        mCharacter.postValue(repo.updateSavedCharacter(mCharacter.getValue().getIdcharacter())); //TODO Could maybe check to see if is the same? might not be needed
    }

    public void updateCurrentAbilities(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            if (mCharacter.getValue() != null)
                mAbilities.postValue(repo.updateAbilities(mCharacter.getValue().getIdcharacter())); //TODO Could maybe check to see if is the same? might not be needed
        });
    }

    public void updateCurrentMoney(){
        if (mCharacter.getValue() != null) {
            ArrayList<InventoryDTO> tmpLst = (ArrayList<InventoryDTO>) repo.updateInventory(mCharacter.getValue().getIdcharacter());
            //We only want to show money here (ID's: 1 gold, 2 silver, 3 kobber)
            ArrayList<InventoryDTO> moneyLst = new ArrayList<InventoryDTO>();
            moneyLst.add(tmpLst.get(0));//Gold first
            moneyLst.add(tmpLst.get(1));//Silver next
            moneyLst.add(tmpLst.get(2));//Kobber last

            mMoney.postValue(moneyLst); //TODO Could maybe check to see if is the same? might not be needed
        }
    }*/

    public void startGetThread(int characterID){
        GetCharacterThread thread = new GetCharacterThread(characterID);
        thread.start();
    }

    public LiveData<CharacterDTO> getCharacter() {
        return mCharacter;
    }

    public LiveData<List<AbilityDTO>> getAbilities() {
        return mAbilities;
    }

    public LiveData<List<InventoryDTO>> getMoney() {
        return mMoney;
    }

    public LiveData<List<RaceDTO>> getRaces() {
        return mRace;
    }

    private void getCharacterByCharacterID(int characterid){
        Result<CharacterDTO> result;
        result = repo.getCharacterByID(characterid);

        if (result instanceof Result.Success) {
            CharacterDTO character = ((Result.Success<CharacterDTO>) result).getData();
            mCharacter.postValue(character);
            getAbilitiesByCharacterID(characterid);
            getMoneyByCharacterID(characterid);
            if (character.getIdrace() == 6){
                getRacesByCharacterID(characterid);
            }
            //loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName() + " " + data.getLastName(), data.getEmail(), data.getPassHash())));
        } else {
            //loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }

    public void getAbilitiesByCharacterID(int characterid){
        Result<List<AbilityDTO>> result;
        result = repo.getAbilitiesByCharacterID(characterid);

        if (result instanceof Result.Success) {
            ArrayList<AbilityDTO> tmpLst = ((Result.Success<ArrayList<AbilityDTO>>) result).getData();
            if (!tmpLst.equals((ArrayList<AbilityDTO>) mAbilities.getValue())) mAbilities.postValue(tmpLst);
            //loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName() + " " + data.getLastName(), data.getEmail(), data.getPassHash())));
        } else {
            //loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }

    public void getMoneyByCharacterID(int characterid){
        Result<List<InventoryDTO>> result;
        result = repo.getInventoryByCharacterID(characterid);

        if (result instanceof Result.Success) {
            ArrayList<InventoryDTO> tmpLst = ((Result.Success<ArrayList<InventoryDTO>>) result).getData();
            //We only want to show money here (ID's: 1 gold, 2 silver, 3 kobber)
            ArrayList<InventoryDTO> moneyLst = new ArrayList<InventoryDTO>();
            moneyLst.add(tmpLst.get(0));//Gold first
            moneyLst.add(tmpLst.get(1));//Silver next
            moneyLst.add(tmpLst.get(2));//Kobber last

            mMoney.postValue(moneyLst);
            //loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName() + " " + data.getLastName(), data.getEmail(), data.getPassHash())));
        } else {
            //loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }

    public void getRacesByCharacterID(int characterid){
        Result<List<RaceDTO>> res = repo.getKrydsRaces(characterid);
        if (res instanceof Result.Success){
            ArrayList<RaceDTO> raceLst = ((Result.Success<ArrayList<RaceDTO>>) res).getData();
            mRace.postValue(raceLst);
        }

    }


    class GetCharacterThread extends Thread {
        private int id;

        public GetCharacterThread(int characterID) {
            this.id = characterID;
        }

        @Override
        public void run() {
            getCharacterByCharacterID(this.id);
        }
    }
}