package com.rbyte.dragernesdal.ui.admin.event;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.StringTimeFormatter;
import com.rbyte.dragernesdal.data.event.model.AttendingDTO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;
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
    private ArrayList<EventDTO> events;
    private Handler uiThread = new Handler();
    SharedPreferences prefs;
    private int characterID;
    public static final String EVENT_ID_ARGUMENT = "eventIDArgument";
    private static final String EVENT_SELECTED_NAME = "eventName";
    View root2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_event, container, false);
        prefs = getDefaultSharedPreferences(root.getContext());
        characterID = prefs.getInt(HomeFragment.CHARACTER_ID_SAVESPACE, -1);
        eventViewModel = EventViewModel.getInstance();
        eventViewModel.startGetThread(characterID);
        root2 = root;
        events = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.eventRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        eventViewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<EventDTO>>() {
            @Override
            public void onChanged(List<EventDTO> eventDTOS) {
                eventCards.clear();
                events.clear();
                StringTimeFormatter stringTimeFormatter = new StringTimeFormatter();
                eventDTOS.forEach((n) -> {
                    events.add(n);
                    String start = stringTimeFormatter.format(n.getStartDate());
                    String end = stringTimeFormatter.format(n.getEndDate());
                    String date =  stringTimeFormatter.equalDate(start, end)?
                            stringTimeFormatter.getDate(start) :
                            stringTimeFormatter.getDate(start) + " - " + stringTimeFormatter.getDate(end);
                    eventCards.add(new EventCard(date, n.getInfo(),
                            "Klokken: " + stringTimeFormatter.getTime(n.getStartDate()), stringTimeFormatter.getTime(n.getEndDate()), n.getAddress(), n.getName(),n.getHyperlink(),n.getEventID()));
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
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(getResources().getString(R.string.editOrCheckIn));
                    final int position = getAdapterPosition();
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Rediger event", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            PopupHandler popupHandler = new PopupHandler(getContext());
                            events.get(position).setEventID(eventCards.get(position).getEventID());
                            System.out.println(eventCards.get(position).getEventID());
                            AlertDialog.Builder builder = popupHandler.editEvent(root2, events.get(position), uiThread, Navigation.findNavController(root2));
                            builder.show();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.check_ind), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            SharedPreferences prefs = getDefaultSharedPreferences(root2.getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(EVENT_ID_ARGUMENT, events.get(position).getEventID());
                            editor.putString(EVENT_SELECTED_NAME, events.get(position).getName());
                            editor.commit();
                            NavController navController = Navigation.findNavController(root2);
                            navController.navigate(R.id.nav_admin_checkin);
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Check ud", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            SharedPreferences prefs = getDefaultSharedPreferences(root2.getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(EVENT_ID_ARGUMENT, events.get(position).getEventID());
                            editor.putString(EVENT_SELECTED_NAME, events.get(position).getName());
                            editor.commit();
                            NavController navController = Navigation.findNavController(root2);
                            navController.navigate(R.id.nav_admin_checkout);
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

    public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
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
            vh.attending.setVisibility(View.INVISIBLE);
            vh.attending.setHeight(0);
            vh.attending.setText("");
            vh.attending.setEnabled(false);
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
        private int eventID = -1;

        public EventCard(String date, String info, String startTime, String endTime, String address, String title, String hyperlink, int eventID) {
            this.title = title;
            this.date = date;
            this.info = info;
            this.startTime = startTime;
            this.endTime = endTime;
            this.address = address;
            this.hyperlink = hyperlink;
            this.eventID = eventID;
        }

        public EventCard() {

        }

        public int getEventID() {
            return eventID;
        }

        public void setEventID(int eventID) {
            this.eventID = eventID;
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