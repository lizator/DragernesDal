package com.example.dragernesdal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dragernesdal.data.ability.model.Ability;
import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.character.CharacterRepository;
import com.example.dragernesdal.ui.login.LoginViewModel;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CharacterDTO> mCharacter;
    private MutableLiveData<List<Ability>> mAbilities;
    private CharacterRepository repo;
    private static HomeViewModel instance;
    //TODO Make singleton, so i doens't load character in again

    public static HomeViewModel getInstance(){
        if (instance == null) instance = new HomeViewModel();
        return instance;
    }

    private HomeViewModel() {
        mCharacter = new MutableLiveData<>();
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

    public LiveData<List<Ability>> getAbilities() {
        return mAbilities;
    }

    private void getCharacterByID(int characterid){
        Result<CharacterDTO> result;
        result = repo.getCharacterByID(characterid);

        if (result instanceof Result.Success) {
            CharacterDTO character = ((Result.Success<CharacterDTO>) result).getData();
            mCharacter.postValue(character);
            mAbilities.postValue();
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
            getCharacterByID(this.id);
        }
    }
}