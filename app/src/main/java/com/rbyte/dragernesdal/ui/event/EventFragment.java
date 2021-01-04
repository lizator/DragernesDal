package com.rbyte.dragernesdal.ui.event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class EventFragment extends Fragment implements View.OnClickListener {

    private EventViewModel eventViewModel;
    private CalendarView calendar;
    private Button button_event_info;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);
        View root = inflater.inflate(R.layout.fragment_event, container, false);
        calendar = root.findViewById(R.id.calendarView);
        button_event_info = root.findViewById(R.id.button_event_info);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                Log.d("dateChange","Changed date to:"+String.valueOf(year)+"/" +String.valueOf(month+1)+"/" +String.valueOf(dayOfMonth));
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


    @Override
    public void onClick(View v) {
        System.out.println(calendar.getDate());
    }

    class EventHandler {
        private void setEvents(CalendarView calendarView) {
            ArrayList<Date> dates = new ArrayList<>();

        }
    }

}