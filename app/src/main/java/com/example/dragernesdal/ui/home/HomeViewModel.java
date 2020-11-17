package com.example.dragernesdal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dragernesdal.data.ability.model.AbilityDTO;
import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.character.CharacterRepository;
import com.example.dragernesdal.data.inventory.model.InventoryDTO;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CharacterDTO> mCharacter;
    private MutableLiveData<List<AbilityDTO>> mAbilities;
    private MutableLiveData<List<InventoryDTO>> mMoney;
    private CharacterRepository repo;
    private static HomeViewModel instance;
    //TODO Make singleton, so i doens't load character in again

    public static HomeViewModel getInstance(){
        if (instance == null) instance = new HomeViewModel();
        return instance;
    }

    private HomeViewModel() {
        mCharacter = new MutableLiveData<>();
        mAbilities = new MutableLiveData<>();
        mMoney = new MutableLiveData<>();
        repo = CharacterRepository.getInstance();
        //Getting character
    }

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

    private void getCharacterByCharacterID(int characterid){
        Result<CharacterDTO> result;
        result = repo.getCharacterByID(characterid);

        if (result instanceof Result.Success) {
            CharacterDTO character = ((Result.Success<CharacterDTO>) result).getData();
            mCharacter.postValue(character);
            getAbilitiesByCharacterID(characterid);
            getMoneyByCharacterID(characterid);
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
            mAbilities.postValue(tmpLst);
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