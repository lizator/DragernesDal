package com.rbyte.dragernesdal.ui.character.select;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SelectViewModel extends ViewModel {

    private static SelectViewModel instance;
    private MutableLiveData<List<CharacterDTO>> mCharacters;
    private CharacterRepository repo;
    private int userID = -1;

    private SelectViewModel() {
        mCharacters = new MutableLiveData<>();
        repo = CharacterRepository.getInstance();
    }

    public static SelectViewModel getInstance() {
        if (instance == null) instance = new SelectViewModel();
        return instance;
    }

    public void updateCurrentCharacters(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            if (mCharacters.getValue() != null && userID != -1)
                mCharacters.postValue(((Result.Success<List<CharacterDTO>>) repo.getCharactersByUserID(userID)).getData());
        });
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    public LiveData<List<CharacterDTO>> getCharacters() {
        return mCharacters;
    }

    public void startGetThread(int userID){
        setUserID(userID);
        GetCharacterListThread thread = new GetCharacterListThread(userID);
        thread.start();
    }

    public void getCharactersByUserID(int userID){
        Result<List<CharacterDTO>> result;
        result = repo.getCharactersByUserID(userID);
        if (result instanceof Result.Success) {
            ArrayList<CharacterDTO> lst = ((Result.Success<ArrayList<CharacterDTO>>) result).getData();
            if(!(lst).equals((ArrayList<CharacterDTO>) mCharacters.getValue())) {
                mCharacters.postValue(lst);
            }
            //loginResult.postValue(new LoginResult(new LoggedInUserView(data.getFirstName() + " " + data.getLastName(), data.getEmail(), data.getPassHash())));
        } else {
            //loginResult.postValue(new LoginResult(R.string.login_failed));
        }
    }


    class GetCharacterListThread extends Thread {
        private int id;

        public GetCharacterListThread(int userID) {
            this.id = userID;
        }

        @Override
        public void run() {
            getCharactersByUserID(this.id);
        }
    }
}