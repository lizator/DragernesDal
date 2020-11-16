package com.example.dragernesdal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.character.CharacterRepository;
import com.example.dragernesdal.ui.login.LoginViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<CharacterDTO> mCharacter;
    private CharacterRepository repo;
    private static HomeViewModel instance;
    //TODO Make singleton, so i doens't load character in again

    public static HomeViewModel getInstance(int characterID){
        if (instance == null) instance = new HomeViewModel(characterID);
        return instance;
    }

    private HomeViewModel(int characterID) {
        mCharacter = new MutableLiveData<>();
        repo = CharacterRepository.getInstance();
        //Getting character
        GetCharacterThread thread = new GetCharacterThread(characterID);
        thread.start();
    }

    public LiveData<CharacterDTO> getCharacter() {
        return mCharacter;
    }

    public void getCharacterByID(int characterid){
        Result<CharacterDTO> result;
        result = repo.getCharacterByID(characterid);

        if (result instanceof Result.Success) {
            CharacterDTO character = ((Result.Success<CharacterDTO>) result).getData();
            mCharacter.postValue(character);
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