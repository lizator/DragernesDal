package com.rbyte.dragernesdal.ui.admin.event;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.event.model.AttendingDTO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.event.EventFragment;
import com.rbyte.dragernesdal.ui.event.EventViewModel;
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

//https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
public class EditEventFragment extends Fragment {
    //TODO: Gem gamle events.
    private EventViewModel eventViewModel;
    private EventAdapter eventAdapter = new EventAdapter();
    private ArrayList<EventCard> eventCards = new ArrayList<>();
    SharedPreferences prefs;
    private int characterID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_event, container, false);
        prefs = getDefaultSharedPreferences(root.getContext());
        characterID = prefs.getInt(HomeFragment.CHARACTER_ID_SAVESPACE, -1);
        eventViewModel = EventViewModel.getInstance();
        eventViewModel.startGetThread(characterID);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.eventRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        eventViewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<EventDTO>>() {
            @Override
            public void onChanged(List<EventDTO> eventDTOS) {
                eventCards.clear();
                eventDTOS.forEach((n) -> {
                    SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat dom = new SimpleDateFormat("E: dd-MM-yyyy");
                    ft.setTimeZone(TimeZone.getTimeZone("CET-1"));
                    dom.setTimeZone(TimeZone.getTimeZone("CET-1"));
                    eventCards.add(new EventCard(dom.format(n.getStartDate()), n.getInfo(), "Klokken: " + ft.format(n.getStartDate()))); //TODO: Tjek om man er tilmeldt eventet
                });
                eventAdapter.notifyDataSetChanged();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress", "Back pressed in EventFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView date, info, time, attending;

        public EventViewHolder(View eventViews) {
            super(eventViews);
            cardView = eventViews.findViewById(R.id.event_card_view);
            date = eventViews.findViewById(R.id.textDate);
            info = eventViews.findViewById(R.id.textEventInfo);
            time = eventViews.findViewById(R.id.textTime);
            attending = eventViews.findViewById(R.id.textAttending);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();

                }
            });
        }
    }

    class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
        @Override
        public int getItemCount() {
            return eventCards.size();
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_event_cardview, parent, false);
            EventViewHolder vh = new EventViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(EventViewHolder vh, int position) {
            vh.date.setText(eventCards.get(position).getDate());
            vh.info.setText(eventCards.get(position).getInfo());
            vh.time.setText(eventCards.get(position).getTime());
            vh.attending.setVisibility(View.INVISIBLE);
        }
    }

    private class EventCard {
        private String date = "";
        private String info = "";
        private String time = "";
        private Boolean attending = false;

        public EventCard(String date, String info, String time) {
            this.date = date;
            this.info = info;
            this.time = time;
        }

        public EventCard() {

        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Boolean getAttending() {
            return attending;
        }

        public void setAttending(Boolean attending) {
            this.attending = attending;
        }

        @Override
        public String toString() {
            return date + "\n" + info + "\n" + time + "\n" + attending;
        }
    }
}