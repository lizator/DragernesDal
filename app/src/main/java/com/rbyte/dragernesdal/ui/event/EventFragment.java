package com.rbyte.dragernesdal.ui.event;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class EventFragment extends Fragment {

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<EventDTO> eventDTOS) {
                eventCards.clear();
                eventDTOS.forEach((n) -> {
                    String date = n.getStartDate().toLocalDate().toString().equals((n.getEndDate().toLocalDate().toString())) ?
                            n.getStartDate().toLocalDate().toString() :
                            n.getStartDate().toLocalDate().toString() + " - " + n.getEndDate().toLocalDate().toString();
                    eventCards.add(new EventCard(date, n.getInfo(),
                            "Klokken: " + n.getStartDate().toLocalTime().toString()+":00", n.getEndDate().toLocalTime().toString()+":00", n.getAddress(), n.getName(),n.getHyperlink()));
                });
                eventAdapter.notifyDataSetChanged();
            }
        });

        eventViewModel.getAttending(characterID).observe(getViewLifecycleOwner(), new Observer<List<AttendingDTO>>() {
            @Override
            public void onChanged(List<AttendingDTO> attending) {
                if (attending == null || eventCards == null || eventCards.size() == 0)
                    return;
                attending.forEach((n) -> {
                    eventCards.get(n.getIdEvent()).setAttending(true);
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
        TextView date, info, time, attending, address, title, hyperlink;

        public EventViewHolder(View eventViews) {
            super(eventViews);
            cardView = eventViews.findViewById(R.id.event_card_view);
            date = eventViews.findViewById(R.id.textDate);
            info = eventViews.findViewById(R.id.textEventInfo);
            time = eventViews.findViewById(R.id.textTime);
            title = eventViews.findViewById(R.id.textTitle);
            attending = eventViews.findViewById(R.id.textAttending);
            address = eventViews.findViewById(R.id.textAddress);
            hyperlink = eventViews.findViewById(R.id.textLink);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (!eventCards.get(position).getAttending()) {
                        eventCards.get(position).setAttending(true);
                        eventViewModel.startSetThread(characterID, position);
                    } else {
                        eventCards.get(position).setAttending(false);
                        eventViewModel.startRemoveThread(characterID, position);

                    }
                    /*System.out.println(eventCards.toString());
                    System.out.println("Attending: "+eventCards.get(position).getAttending());
                    System.out.println("CharID: "+characterID +" Clicked: "+position);*/
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
            vh.time.setText(eventCards.get(position).getStartTime() + "-" + eventCards.get(position).getEndTime());
            vh.address.setText("Adresse: " + eventCards.get(position).getAddress());
            vh.title.setText(eventCards.get(position).getTitle());
            vh.hyperlink.setText(eventCards.get(position).getHyperlink());
            vh.attending.setText(eventCards.get(position).getAttending() ? "Deltager" : "Deltager ikke");
        }

    }

    private class EventCard {
        private String date = "";
        private String info = "";
        private String startTime = "";
        private String endTime = "";
        private Boolean attending = false;
        private String address = "";
        private String title = "";
        private String hyperlink = "";

        public EventCard(String date, String info, String startTime, String endTime, String address, String title, String hyperlink) {
            this.title = title;
            this.date = date;
            this.info = info;
            this.startTime = startTime;
            this.endTime = endTime;
            this.address = address;
            this.hyperlink = hyperlink;
        }

        public EventCard() {

        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public Boolean getAttending() {
            return attending;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getHyperlink() {
            return hyperlink;
        }

        public void setHyperlink(String hyperlink) {
            this.hyperlink = hyperlink;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setAttending(Boolean attending) {
            this.attending = attending;
        }

        @Override
        public String toString() {
            return date + "\n" + info + "\n" + startTime + "\n" + attending;
        }
    }
}
