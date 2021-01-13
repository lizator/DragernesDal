package com.rbyte.dragernesdal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.ability.AbilityDAO;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CharacterDTO> mCharacter;
    private MutableLiveData<List<AbilityDTO>> mAbilities;
    private MutableLiveData<List<InventoryDTO>> mMoney;
    private MutableLiveData<RaceDTO> mRace;
    private MutableLiveData<List<RaceDTO>> mOtherRace;
    private ArrayList<AbilityDTO> potential3epRaceAbilities;
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
        this.mOtherRace = new MutableLiveData<>();
        this.potential3epRaceAbilities = new ArrayList<>();
        this.repo = CharacterRepository.getInstance();
        //initialing observers
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

    public MutableLiveData<RaceDTO> getmRace() {
        return mRace;
    }

    public LiveData<List<RaceDTO>> getRaces() {
        return mOtherRace;
    }

    public ArrayList<AbilityDTO> getPotential3epRaceAbilities() {
        return potential3epRaceAbilities;
    }

    private void getCharacterByCharacterID(int characterid){
        Result<CharacterDTO> result;
        result = repo.getCharacterByID(characterid);

        if (result instanceof Result.Success) {
            CharacterDTO character = ((Result.Success<CharacterDTO>) result).getData();
            mCharacter.postValue(character);
            getRaceByRaceID(character.getIdrace(), 0);
            getAbilitiesByCharacterID(characterid);
            getMoneyByCharacterID(characterid);
            if (character.getIdrace() == 6){
                getKrysRacesByCharacterID(characterid, character.getIdrace(), 0);
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
            if (tmpLst.size() == 0) return;
            moneyLst.add(tmpLst.get(0));//Gold first
            moneyLst.add(tmpLst.get(1));//Silver next
            moneyLst.add(tmpLst.get(2));//Kobber last

            mMoney.postValue(moneyLst);
            //loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName() + " " + data.getLastName(), data.getEmail(), data.getPassHash())));
        } else {
            //loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }

    public void getRaceByRaceID(int raceID, int count){
        Result<RaceDTO> res = repo.getSingleRace(raceID);
        if (res instanceof Result.Success){
            RaceDTO race = ((Result.Success<RaceDTO>) res).getData();
            mRace.postValue(race);
        }

        if ((res instanceof Result.Error) && count < 10){
            getRaceByRaceID(raceID, count+1);
        }
    }

    public void getKrysRacesByCharacterID(int characterid, int raceID, int count){
        Result<List<RaceDTO>> res2 = repo.getKrydsRaces(characterid);
        if (res2 instanceof Result.Success){
            ArrayList<RaceDTO> raceLst = ((Result.Success<ArrayList<RaceDTO>>) res2).getData();
            mOtherRace.postValue(raceLst);
            getAbilitiesFromRace(raceLst.get(0).getEp3(), raceLst.get(1).getEp3());
        }

        if ((res2 instanceof Result.Error) && count < 10){
            getKrysRacesByCharacterID(characterid, raceID, count+1);
        }
    }

    public void getAbilitiesFromRace(int ability1ID, int ability2ID){
        AbilityDAO tmpdao = new AbilityDAO();
        Result<AbilityDTO> resAb1 = tmpdao.getAbilityByID(ability1ID);
        Result<AbilityDTO> resAb2 = tmpdao.getAbilityByID(ability2ID);

        if (resAb1 instanceof Result.Success && resAb2 instanceof Result.Success){
            AbilityDTO ab1 = ((Result.Success<AbilityDTO>) resAb1).getData();
            AbilityDTO ab2 = ((Result.Success<AbilityDTO>) resAb2).getData();
            potential3epRaceAbilities.clear();
            potential3epRaceAbilities.add(ab1);
            potential3epRaceAbilities.add(ab2);
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