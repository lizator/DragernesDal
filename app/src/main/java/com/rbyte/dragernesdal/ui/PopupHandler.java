package com.rbyte.dragernesdal.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.event.EventDAO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.admin.event.EditEventFragment;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;

public class PopupHandler {
    private AlertDialog.Builder builder;
    private Context context;
    CharacterRepository charRepo = CharacterRepository.getInstance();
    SkillViewModel skillViewModel = SkillViewModel.getInstance();
    AbilityRepository abilityRepo = AbilityRepository.getInstance();

    public PopupHandler(Context context){
        builder = new AlertDialog.Builder(context);
        this.context = context;
    }

    public AlertDialog.Builder getConfirmBuyAlert(View thisView, String abilityName, int abilityCost, int currentEP, DialogInterface.OnClickListener okListener){
        builder.setTitle("Køb Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_confirmation_buy_ability, (ViewGroup) thisView.getRootView(), false);
        String text = (String) ((TextView) alertView.findViewById(R.id.Alertmsg)).getText();
        text = String.format(text, abilityName, abilityCost, currentEP-abilityCost);
        ((TextView) alertView.findViewById(R.id.Alertmsg)).setText(text);
        builder.setView(alertView);
        builder.setPositiveButton(android.R.string.ok, okListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }

    public AlertDialog.Builder getCraftsAlert(View thisView, Context context, Handler uiThread){
        builder.setTitle("Håndværk!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_input_a_craft, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = alertView.findViewById(R.id.editCraftName);
                String craft = input.getText().toString();
                if (craft.length() != 0) {
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<AbilityDTO> res = abilityRepo.craftBuy(charRepo.getCurrentChar().getIdcharacter(), craft);
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Toast.makeText(context, String.format("Håndværk '%s' oprettet!", craft), Toast.LENGTH_SHORT).show();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Fejl i opret håndværk, prøv igen!", Toast.LENGTH_SHORT).show();
                                getCraftsAlert(thisView, context, uiThread).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "Husk at skrive navnet på et Håndværk!", Toast.LENGTH_SHORT).show();
                    getCraftsAlert(thisView, context, uiThread).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { //TODO: overwite in create character to reappear if not done!
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }

    public AlertDialog.Builder get3EPChoiceAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currAbilities){
        builder.setTitle("3EP Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_3ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin = alertView.findViewById(R.id.ep3spinner);
        ArrayList<AbilityDTO> kampDTOS = skillViewModel.getKampAbilities().getValue();
        ArrayList<AbilityDTO> snigerDTOS = skillViewModel.getSnigerAbilities().getValue();
        ArrayList<AbilityDTO> videnDTOS = skillViewModel.getVidenAbilities().getValue();
        ArrayList<AbilityDTO> alleDTOS = skillViewModel.getAlleAbilities().getValue();
        ArrayList<AbilityDTO> collected = new ArrayList<>();

        ArrayList<AbilityDTO> ep3DTOS = new ArrayList<>(); // finding all possible 3ep abilities
        ArrayList<String> names = new ArrayList<>();
        if (kampDTOS != null && snigerDTOS != null && videnDTOS != null && alleDTOS != null){
            collected.addAll(kampDTOS); //adding all abilities to alleDTOS
            collected.addAll(snigerDTOS);
            collected.addAll(videnDTOS);
            collected.addAll(alleDTOS);


            names.add("Vælg evne!");
            for (AbilityDTO dto : collected){
                boolean notOwned = true;
                for (int id : currAbilities){
                    if (id == dto.getId()){
                        notOwned = false;
                        break;
                    }
                }
                if (notOwned && dto.getCost() == 3){
                    ep3DTOS.add(dto);
                    names.add(dto.getName());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);

        } else {
            Log.d("PopupHandler", "get3EPChoiceAlert: some abilities not loaded");
            return null;
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pos = spin.getSelectedItemPosition() - 1;
                if (pos != -1){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.confirmBuyWithFree(charRepo.getCurrentChar().getIdcharacter(), 4, ep3DTOS.get(pos).getId()); // id 4 == lille talent (3EP evnen)
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Toast.makeText(context, String.format("evnen '%s' opnået!", ep3DTOS.get(pos).getName()), Toast.LENGTH_SHORT).show();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Fejl i at få evne til 3 EP, prøv igen!", Toast.LENGTH_SHORT).show();
                                get3EPChoiceAlert(thisView, context, uiThread, currAbilities).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "husk at vælge!" , Toast.LENGTH_SHORT).show();
                    get3EPChoiceAlert(thisView, context, uiThread, currAbilities).show();
                }
            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }

    public AlertDialog.Builder get4EPChoiceAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currAbilities){
        builder.setTitle("4EP Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_4ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin = alertView.findViewById(R.id.ep3spinner);
        ArrayList<AbilityDTO> kampDTOS = skillViewModel.getKampAbilities().getValue();
        ArrayList<AbilityDTO> snigerDTOS = skillViewModel.getSnigerAbilities().getValue();
        ArrayList<AbilityDTO> videnDTOS = skillViewModel.getVidenAbilities().getValue();
        ArrayList<AbilityDTO> alleDTOS = skillViewModel.getAlleAbilities().getValue();
        ArrayList<AbilityDTO> collected = new ArrayList<>();

        ArrayList<AbilityDTO> ep3DTOS = new ArrayList<>(); // finding all possible 3ep abilities
        ArrayList<String> names = new ArrayList<>();
        if (kampDTOS != null && snigerDTOS != null && videnDTOS != null && alleDTOS != null){
            collected.addAll(kampDTOS); //adding all abilities to alleDTOS
            collected.addAll(snigerDTOS);
            collected.addAll(videnDTOS);
            collected.addAll(alleDTOS);


            names.add("Vælg evne!");
            for (AbilityDTO dto : collected){
                boolean notOwned = true;
                for (int id : currAbilities){
                    if (id == dto.getId()){
                        notOwned = false;
                        break;
                    }
                }
                if (notOwned && dto.getCost() == 4){
                    ep3DTOS.add(dto);
                    names.add(dto.getName());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);

        } else {
            Log.d("PopupHandler", "get3EPChoiceAlert: some abilities not loaded");
            return null;
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pos = spin.getSelectedItemPosition() - 1;
                if (pos != -1){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.confirmBuyWithFree(charRepo.getCurrentChar().getIdcharacter(), 5, ep3DTOS.get(pos).getId()); // id 5 == stort talent (4EP evnen)
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Toast.makeText(context, String.format("evnen '%s' opnået!", ep3DTOS.get(pos).getName()), Toast.LENGTH_SHORT).show();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Fejl i at få evne til 4 EP, prøv igen!", Toast.LENGTH_SHORT).show();
                                get3EPChoiceAlert(thisView, context, uiThread, currAbilities).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "husk at vælge!" , Toast.LENGTH_SHORT).show();
                    get3EPChoiceAlert(thisView, context, uiThread, currAbilities).show();
                }
            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }

    public AlertDialog.Builder editEvent(View thisView, EventDTO eventDTO, Handler uiThread, EditEventFragment.EventAdapter eventHandler){
        builder.setTitle("Rediger Event");
        View alertView = LayoutInflater.from(context).inflate(R.layout.fragment_admin_create_event, (ViewGroup) thisView.getRootView(), false);
        final EditText title, startDate, endDate, address, info;
        final String[] timeStart = new String[1];
        final String[] timeEnd = new String[1];
        Button create;
        title = alertView.findViewById(R.id.editText_Title);
        startDate = alertView.findViewById(R.id.editTextStartDate);
        endDate = alertView.findViewById(R.id.editTextEndDate);
        address = alertView.findViewById(R.id.editText_Address);
        info = alertView.findViewById(R.id.eventInformation);
        create = alertView.findViewById(R.id.create_event);
        create.setEnabled(false);
        create.setVisibility(View.INVISIBLE);
        create.setHeight(0);
        title.setText(eventDTO.getName());
        startDate.setText("Sæt ny dato");
        endDate.setText("Sæt ny dato");
        address.setText(eventDTO.getAddress());
        info.setText(eventDTO.getInfo());

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mMonth, mDay;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(thisView.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                int mHour, mMinute;
                                String curDate = String.format("%02d-%02d", dayOfMonth, (monthOfYear+1))+"-"+year;
                                String dateToDTO = year+"-"+String.format("%02d-%02d", (monthOfYear+1),dayOfMonth);
                                startDate.setText(curDate);
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(thisView.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                String curTime = String.format("%02d:%02d", hourOfDay, minute);
                                                timeStart[0] = dateToDTO+" "+curTime+":00";
                                                Log.d("date set", timeStart[0]);
                                                startDate.setText(startDate.getText()+" "+curTime+":00");
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mMonth, mDay;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(thisView.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                int mHour, mMinute;
                                String curDate = String.format("%02d-%02d", dayOfMonth, (monthOfYear+1))+"-"+year;
                                String dateToDTO = year+"-"+String.format("%02d-%02d", (monthOfYear+1),dayOfMonth);
                                endDate.setText(curDate);
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(thisView.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                String curTime = String.format("%02d:%02d", hourOfDay, minute);
                                                timeEnd[0] = dateToDTO+" "+curTime+":00";
                                                Log.d("date set", timeEnd[0]);
                                                endDate.setText(endDate.getText()+" "+curTime+":00");
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        builder.setView(alertView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventDTO eventDTO = new EventDTO();
                EventDAO eventDAO = new EventDAO();
                eventDTO.setName(title.getText()+"");
                eventDTO.setAddress(address.getText()+"");
                eventDTO.setStartDate(Timestamp.valueOf(timeStart[0]));
                eventDTO.setEndDate(Timestamp.valueOf(timeEnd[0]));
                eventDTO.setInfo(info.getText()+"");
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() ->{
                    Log.d("Event","Event edited");
                    eventDAO.editEvent(eventDTO);
                    uiThread.post(()->{
                        Toast.makeText(thisView.getContext(), "Event rettet", Toast.LENGTH_SHORT).show();
                        eventHandler.notifyDataSetChanged();
                        dialog.dismiss();
                    });
                });
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }
    

}
