package com.rbyte.dragernesdal.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.event.AttendingDAO;
import com.rbyte.dragernesdal.data.event.EventDAO;
import com.rbyte.dragernesdal.data.event.model.AttendingDTO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.character.select.SelectViewModel;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EventViewModel extends ViewModel {

    private EventDAO eventDAO;
    private AttendingDAO attendingDAO;
    private MutableLiveData<List<EventDTO>> mEvents;
    private MutableLiveData<List<AttendingDTO>> mAttending;
    private static EventViewModel instance;
    EventViewModel.GetAttendingThread threadA;


    public static EventViewModel getInstance() {
        if (instance == null) instance = new EventViewModel();
        return instance;
    }

    private EventViewModel() {
        eventDAO = new EventDAO();
        attendingDAO = new AttendingDAO();
        mEvents = new MutableLiveData<>();
        mAttending = new MutableLiveData<>();
    }

    /*public LiveData<List<CharacterDTO>> getCharacters() {
        return mCharacters;
    }*/
    public LiveData<List<EventDTO>> getEvents() {
        return mEvents;
    }

    public LiveData<List<AttendingDTO>> getAttending(int charID) {
        return mAttending;
    }

    public void startGetThread(int charID) {
        EventViewModel.GetEventsThread thread = new EventViewModel.GetEventsThread();
        threadA = new EventViewModel.GetAttendingThread(charID);

        thread.start();

    }

    public void startSetThread(int charID, int eventID){
        EventViewModel.SetAttendingThread thread = new EventViewModel.SetAttendingThread(charID, eventID);
        thread.start();
    }


    class SetAttendingThread extends Thread{
        private int charID,eventID;

        public SetAttendingThread(int charID, int eventID){
            this.charID = charID;
            this.eventID = eventID;

        }
        @Override
        public void run() {
            attendingDAO.setAttending(new AttendingDTO(charID,eventID));
            EventViewModel.GetAttendingThread thread = new EventViewModel.GetAttendingThread(charID);
            thread.start();
        }
    }

    class GetAttendingThread extends Thread {
        private int charID;
        public GetAttendingThread(int charID) {
            this.charID = charID;
        }

        @Override
        public void run() {
            Result result = attendingDAO.getAttending(charID);
            ArrayList<AttendingDTO> lst = ((Result.Success<ArrayList<AttendingDTO>>) result).getData();
            mAttending.postValue(lst);
        }
    }

    class GetEventsThread extends Thread {
        public GetEventsThread() {

        }

        @Override
        public void run() {
            Result result = eventDAO.getEvents();
            ArrayList<EventDTO> lst = ((Result.Success<ArrayList<EventDTO>>) result).getData();
            mEvents.postValue(lst);
            threadA.start();
        }
    }
}