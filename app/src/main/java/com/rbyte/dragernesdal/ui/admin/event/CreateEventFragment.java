package com.rbyte.dragernesdal.ui.admin.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.event.EventDAO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.character.background.BackgroundViewModel;
import com.rbyte.dragernesdal.ui.event.EventViewModel;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
public class CreateEventFragment extends Fragment {

    private Button createEvent;
    private EditText title, startDate, endDate, address, info, hyperlink;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String timeStart = "", timeEnd = "";
    private Handler uiThread = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_create_event, container, false);
        createEvent = root.findViewById(R.id.create_event);
        createEvent.setEnabled(false);
        final boolean[] startDateSet = {false};
        final boolean[] endDateSet = {false};
        title = root.findViewById(R.id.editText_Title);
        startDate = root.findViewById(R.id.editTextStartDate);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(root.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String curDate = String.format("%02d-%02d", dayOfMonth, (monthOfYear+1))+"-"+year;
                                String dateToDTO = year+"-"+String.format("%02d-%02d", (monthOfYear+1),dayOfMonth);
                                startDate.setText(curDate);
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(root.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                String curTime = String.format("%02d:%02d", hourOfDay, minute);
                                                timeStart = dateToDTO+"T"+curTime+":00";
                                                Log.d("date set", timeStart);
                                                startDate.setText(startDate.getText()+" "+curTime+":00");
                                                startDateSet[0] = true;
                                                if(startDateSet[0] && endDateSet[0]) createEvent.setEnabled(true);
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        endDate = root.findViewById(R.id.editTextEndDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(root.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String curDate = String.format("%02d-%02d", dayOfMonth, (monthOfYear+1))+"-"+year;
                                String dateToDTO = year+"-"+String.format("%02d-%02d", (monthOfYear+1),dayOfMonth);
                                endDate.setText(curDate);
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(root.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                String curTime = String.format("%02d:%02d", hourOfDay, minute);
                                                timeEnd = dateToDTO+"T"+curTime+":00";
                                                Log.d("date set", timeStart);
                                                endDate.setText(endDate.getText()+" "+curTime+":00");
                                                endDateSet[0] = true;
                                                if(startDateSet[0] && endDateSet[0]) createEvent.setEnabled(true);
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        address = root.findViewById(R.id.editText_Address);
        info = root.findViewById(R.id.eventInformation);
        hyperlink = root.findViewById(R.id.editText_hyperlink);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                EventDTO eventDTO = new EventDTO();
                EventDAO eventDAO = new EventDAO();
                eventDTO.setName(title.getText()+"");
                eventDTO.setAddress(address.getText()+"");
                eventDTO.setStartDate(timeStart);
                eventDTO.setEndDate(timeEnd);
                eventDTO.setInfo(info.getText()+"");
                eventDTO.setHyperlink(hyperlink.getText()+"");
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() ->{
                    Log.d("Event","Event created");
                    eventDAO.createEvent(eventDTO);
                    uiThread.post(()->{
                        Toast.makeText(getActivity(), "Event oprettet", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(root);
                        navController.popBackStack();
                    });
                });
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in create event");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }
}
