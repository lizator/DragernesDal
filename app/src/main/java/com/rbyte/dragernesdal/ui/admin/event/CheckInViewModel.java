package com.rbyte.dragernesdal.ui.admin.event;

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

public class CheckInViewModel extends ViewModel {

    private static CheckInViewModel instance;
    private MutableLiveData<List<CharacterDTO>> mCharacters;
    private CharacterRepository repo;
    private int eventID = -1;

    private CheckInViewModel() {
        mCharacters = new MutableLiveData<>();
        repo = CharacterRepository.getInstance();
    }

    public static CheckInViewModel getInstance() {
        if (instance == null) instance = new CheckInViewModel();
        return instance;
    }

    public void setEventID(int eventID){
        this.eventID = eventID;
    }

    public LiveData<List<CharacterDTO>> getCharacters() {
        return mCharacters;
    }

    public void startGetThread(int eventID){
        setEventID(eventID);
        GetCharacterListThread thread = new GetCharacterListThread(eventID);
        thread.start();
    }

    public void getCharactersByEventID(int eventID){
        Result<List<CharacterDTO>> result;
        result = repo.getCharactersByEventID(eventID);
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

        public GetCharacterListThread(int eventID) {
            this.id = eventID;
        }

        @Override
        public void run() {
            getCharactersByEventID(this.id);
        }
    }
}