package com.rbyte.dragernesdal.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.event.EventDAO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.character.select.SelectViewModel;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EventViewModel extends ViewModel {

    private EventDAO eventDAO;
    private ArrayList<EventDTO> events;
    private MutableLiveData<List<EventDTO>> mEvents;
    private static EventViewModel instance;

    public static EventViewModel getInstance(){
        if (instance == null) instance = new EventViewModel();
        return instance;
    }

    private EventViewModel() {
        eventDAO = new EventDAO();
        mEvents = new MutableLiveData<>();
    }

    /*public LiveData<List<CharacterDTO>> getCharacters() {
        return mCharacters;
    }*/
    public LiveData<List<EventDTO>> getEvents(){
       return mEvents;
    }

    public void startGetThread(){
        EventViewModel.GetEventsThread thread = new EventViewModel.GetEventsThread();
        thread.start();
    }

    class GetEventsThread extends Thread{
        public GetEventsThread(){

        }

        @Override
        public void run() {
            Result result = eventDAO.getEvents();
            ArrayList<EventDTO> lst = ((Result.Success<ArrayList<EventDTO>>) result).getData();
            mEvents.postValue(lst);
        }
    }
}