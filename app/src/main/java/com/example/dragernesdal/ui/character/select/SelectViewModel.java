package com.example.dragernesdal.ui.character.select;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.character.CharacterRepository;
import com.example.dragernesdal.data.character.model.CharacterDTO;

import java.util.ArrayList;
import java.util.List;

public class SelectViewModel extends ViewModel {

    private static SelectViewModel instance;
    private MutableLiveData<List<CharacterDTO>> mCharacters;
    private CharacterRepository repo;

    private SelectViewModel() {
        mCharacters = new MutableLiveData<>();
        repo = CharacterRepository.getInstance();
    }

    public static SelectViewModel getInstance() {
        if (instance == null) instance = new SelectViewModel();
        return instance;
    }

    public void updateCurrentCharacters(){
        if (mCharacters.getValue() != null)
            mCharacters.postValue(repo.updateCharacterList(mCharacters.getValue().get(0).getIduser())); //TODO Could maybe check to see if is the same? might not be needed
    }

    public LiveData<List<CharacterDTO>> getCharacters() {
        return mCharacters;
    }

    public void startGetThread(int userID){
        GetCharacterListThread thread = new GetCharacterListThread(userID);
        thread.start();
    }

    public void getCharactersByUserID(int userID){
        Result<List<CharacterDTO>> result;
        result = repo.getCharactersByUserID(userID);

        if (result instanceof Result.Success) {
            ArrayList<CharacterDTO> lst = ((Result.Success<ArrayList<CharacterDTO>>) result).getData();
            mCharacters.postValue(lst);
            //loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName() + " " + data.getLastName(), data.getEmail(), data.getPassHash())));
        } else {
            //loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }


    class GetCharacterListThread extends Thread {
        private int id;

        public GetCharacterListThread(int characterID) {
            this.id = characterID;
        }

        @Override
        public void run() {
            getCharactersByUserID(this.id);
        }
    }
}