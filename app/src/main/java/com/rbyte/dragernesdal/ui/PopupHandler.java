package com.rbyte.dragernesdal.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.ui.character.background.BackgroundViewModel;
import com.rbyte.dragernesdal.data.event.EventDAO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.time.format.DateTimeFormatter;
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
    BackgroundViewModel backgroundViewModel = BackgroundViewModel.getInstance();
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

    public AlertDialog.Builder getInfoAlert(View thisView, String name, String msg){
        builder.setTitle(name);
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_ability_info, (ViewGroup) thisView.getRootView(), false);
        ((TextView) alertView.findViewById(R.id.abilityInfo)).setText(msg);
        builder.setView(alertView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    public AlertDialog.Builder getKrysRaceAlert(View thisView, Context context, Handler uiThread){
        builder.setTitle("Krydsningen");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_krysracer, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin1 = alertView.findViewById(R.id.race1spinner);
        Spinner spin2 = alertView.findViewById(R.id.race2spinner);
        String[] choices = new String[]{"vælg race!", "Dværg", "Elver", "Gobliner", "Granitaner", "Havfolk", "Menneske", "Orker", "Sortelver"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapter);
        spin2.setAdapter(adapter);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pos1 = spin1.getSelectedItemPosition();
                int pos2 = spin2.getSelectedItemPosition();
                int origpos1 = pos1;
                int origpos2 = pos2;
                if (pos1 != 0 && pos2 != 0){
                    if (pos1 != pos2) {
                        if (pos1 > 5) pos1++;
                        if (pos1 > 7) pos1++;
                        if (pos2 > 5) pos2++;
                        if (pos2 > 7) pos2++;
                        int newpos1 = pos1;
                        int newpos2 = pos2;
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(() -> {
                            Result<List<AbilityDTO>> res = abilityRepo.confirmBuy(charRepo.getCurrentChar().getIdcharacter(), 35); // id 35 == blandet blod (krysRacer evnen)
                            uiThread.post(() -> {
                                if (res instanceof Result.Success) {
                                    Executor bgThread2 = Executors.newSingleThreadExecutor();
                                    bgThread2.execute(() -> {
                                        Result<CharacterDTO> charRes;
                                        while (true) {
                                            charRes = charRepo.createKrysling(charRepo.getCurrentChar().getIdcharacter(), newpos1, newpos2);
                                            if (charRes instanceof Result.Success) break;
                                        }
                                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                                        Result<CharacterDTO> charResRes = charRes;
                                        uiThread.post(() -> {
                                            if (charResRes instanceof Result.Success){
                                                Toast.makeText(context, String.format("Racerne '%s' og '%s' valgt!", choices[origpos1], choices[origpos2]), Toast.LENGTH_SHORT).show();
                                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                                skillViewModel.getUpdate().postValue(true);
                                                HomeViewModel.getInstance().startGetThread(charRepo.getCurrentChar().getIdcharacter());
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(context, "Fejl i at få racerne, prøv igen!", Toast.LENGTH_SHORT).show();
                                                getKrysRaceAlert(thisView, context, uiThread).show();
                                                dialog.dismiss();
                                            }
                                        });
                                    });

                                } else {
                                    Toast.makeText(context, "Fejl i at få racerne, prøv igen!", Toast.LENGTH_SHORT).show();
                                    getKrysRaceAlert(thisView, context, uiThread).show();
                                    dialog.dismiss();
                                }
                            });
                        });
                    }else {
                        Toast.makeText(context, "Du kan ikke vælge den samme race 2 gange" , Toast.LENGTH_SHORT).show();
                        getKrysRaceAlert(thisView, context, uiThread).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(context, "Sørg for at du har valgt begge racer" , Toast.LENGTH_SHORT).show();
                    getKrysRaceAlert(thisView, context, uiThread).show();
                    dialog.dismiss();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                Toast.makeText(context, "Du skal vælge nu!", Toast.LENGTH_SHORT).show();
                getKrysRaceAlert(thisView, context, uiThread).show();
            }
        });
        return builder;
    }

    public AlertDialog.Builder getKrys2EPAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currentAbilities){
        builder.setTitle("Deformitet");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_krys2ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin1 = alertView.findViewById(R.id.ep3spinner);
        Spinner spin2 = alertView.findViewById(R.id.badspinner);

        ArrayList<AbilityDTO> race3EPAbilities = (ArrayList<AbilityDTO>) HomeViewModel.getInstance().getPotential3epRaceAbilities();
        ArrayList<AbilityDTO> foundBadAbilities = (ArrayList<AbilityDTO>) backgroundViewModel.getBadAbilities().getValue();
        ArrayList<AbilityDTO> badAbilities = new ArrayList<>();
        ArrayList<String> raceAbilityNames = new ArrayList<>();
        ArrayList<String> badAbilityNames = new ArrayList<>();
        if (race3EPAbilities != null && badAbilities != null){

            raceAbilityNames.add("Vælg RaceEvne!");
            raceAbilityNames.add(race3EPAbilities.get(0).getName());
            raceAbilityNames.add(race3EPAbilities.get(1).getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, raceAbilityNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin1.setAdapter(adapter);

            badAbilityNames.add("Vælg dårligt karaktertræk!");
            for (AbilityDTO dto : foundBadAbilities){
                boolean notBought = true;
                for (int id : currentAbilities){
                    if (id == dto.getId()){
                        notBought = false;
                        break;
                    }
                }
                if (notBought && dto.getCost() == -1) {
                    badAbilityNames.add(dto.getName());
                    badAbilities.add(dto);
                }
            }
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, badAbilityNames);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin2.setAdapter(adapter2);
        }


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pos1 = spin1.getSelectedItemPosition() - 1;
                int pos2 = spin2.getSelectedItemPosition() - 1;
                if (pos1 != -1 && pos2 != -1){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.confirmBuy(charRepo.getCurrentChar().getIdcharacter(), 36); // id 36 == Deformitet (krys2EP evnen)
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Executor bgThread2 = Executors.newSingleThreadExecutor();
                                bgThread2.execute(() -> {
                                    String ab3epRes = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), race3EPAbilities.get(pos1).getId(), true);
                                    String badRes = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), badAbilities.get(pos2).getId(), true);
                                    uiThread.post(() -> {
                                        buyOtherAbility(ab3epRes, thisView, context, uiThread, currentAbilities);
                                        Toast.makeText(context, String.format("Evnen '%s' og dårlige karaktertræk '%s' opnået", raceAbilityNames.get(pos1+1), badAbilityNames.get(pos2+1)), Toast.LENGTH_SHORT).show();
                                        skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                        skillViewModel.getUpdate().postValue(true);
                                        HomeViewModel.getInstance().startGetThread(charRepo.getCurrentChar().getIdcharacter());
                                        dialog.dismiss();

                                    });
                                });

                            } else {
                                Toast.makeText(context, "Fejl i at få racerne, prøv igen!", Toast.LENGTH_SHORT).show();
                                getKrys2EPAlert(thisView, context, uiThread, currentAbilities).show();
                                dialog.dismiss();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "Sørg for at du har valgt begge evner!" , Toast.LENGTH_SHORT).show();
                    getKrys2EPAlert(thisView, context, uiThread, currentAbilities).show();
                    dialog.dismiss();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    public AlertDialog.Builder getKrys2EPAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currentAbilities, boolean free){
        builder.setTitle("Deformitet");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_krys2ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin1 = alertView.findViewById(R.id.ep3spinner);
        Spinner spin2 = alertView.findViewById(R.id.badspinner);

        ArrayList<AbilityDTO> race3EPAbilities = (ArrayList<AbilityDTO>) HomeViewModel.getInstance().getPotential3epRaceAbilities();
        ArrayList<AbilityDTO> foundBadAbilities = (ArrayList<AbilityDTO>) backgroundViewModel.getBadAbilities().getValue();
        ArrayList<AbilityDTO> badAbilities = new ArrayList<>();
        ArrayList<String> raceAbilityNames = new ArrayList<>();
        ArrayList<String> badAbilityNames = new ArrayList<>();
        if (race3EPAbilities != null && badAbilities != null){

            raceAbilityNames.add("Vælg RaceEvne!");
            raceAbilityNames.add(race3EPAbilities.get(0).getName());
            raceAbilityNames.add(race3EPAbilities.get(1).getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, raceAbilityNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin1.setAdapter(adapter);

            badAbilityNames.add("Vælg dårligt karaktertræk!");
            for (AbilityDTO dto : foundBadAbilities){
                boolean notBought = true;
                for (int id : currentAbilities){
                    if (id == dto.getId()){
                        notBought = false;
                        break;
                    }
                }
                if (notBought && dto.getCost() == -1) {
                    badAbilityNames.add(dto.getName());
                    badAbilities.add(dto);
                }
            }
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, badAbilityNames);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin2.setAdapter(adapter2);
        }


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pos1 = spin1.getSelectedItemPosition() - 1;
                int pos2 = spin2.getSelectedItemPosition() - 1;
                if (pos1 != -1 && pos2 != -1){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.confirmBuy(charRepo.getCurrentChar().getIdcharacter(), 36); // id 36 == Deformitet (krys2EP evnen)
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Executor bgThread2 = Executors.newSingleThreadExecutor();
                                bgThread2.execute(() -> {
                                    int thisEP = charRepo.getCurrentChar().getCurrentep() - 2;
                                    String ab3epRes = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), race3EPAbilities.get(pos1).getId(), true);
                                    String badRes = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), badAbilities.get(pos2).getId(), true);
                                    uiThread.post(() -> {
                                        buyOtherAbility(ab3epRes, thisView, context, uiThread, currentAbilities);
                                        Toast.makeText(context, String.format("Evnen '%s' og dårlige karaktertræk '%s' opnået", raceAbilityNames.get(pos1+1), badAbilityNames.get(pos2+1)), Toast.LENGTH_SHORT).show();
                                        skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                        skillViewModel.getUpdate().postValue(true);
                                        HomeViewModel.getInstance().startGetThread(charRepo.getCurrentChar().getIdcharacter());
                                        dialog.dismiss();

                                    });
                                });

                            } else {
                                Toast.makeText(context, "Fejl i at få racerne, prøv igen!", Toast.LENGTH_SHORT).show();
                                getKrys2EPAlert(thisView, context, uiThread, currentAbilities).show();
                                dialog.dismiss();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "Sørg for at du har valgt begge evner!" , Toast.LENGTH_SHORT).show();
                    getKrys2EPAlert(thisView, context, uiThread, currentAbilities).show();
                    dialog.dismiss();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
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
                                HomeViewModel.getInstance().startGetThread(charRepo.getCurrentChar().getIdcharacter());
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

    public AlertDialog.Builder getCraftsAlert(View thisView, Context context, Handler uiThread, boolean humanFirstBuy){
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
                        if (humanFirstBuy) {
                            charRepo.getCurrentChar().setCurrentep(charRepo.getCurrentChar().getCurrentep() + 1);// reset from buying craft
                            charRepo.updateCharacter(charRepo.getCurrentChar());
                            abilityRepo.freeGet(charRepo.getCurrentChar().getIdcharacter(), 3); //id == 3 (Proffesion - start human ability)
                        } else { // for krys
                            charRepo.getCurrentChar().setCurrentep(charRepo.getCurrentChar().getCurrentep() + 1);// reset from buying craft
                            charRepo.updateCharacter(charRepo.getCurrentChar());
                        }
                        Result<AbilityDTO> res = abilityRepo.craftBuy(charRepo.getCurrentChar().getIdcharacter(), craft);
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());

                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Toast.makeText(context, String.format("Håndværk '%s' oprettet!", craft), Toast.LENGTH_SHORT).show();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
                                HomeViewModel.getInstance().startGetThread(charRepo.getCurrentChar().getIdcharacter());
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Fejl i opret håndværk, prøv igen!", Toast.LENGTH_SHORT).show();
                                getCraftsAlert(thisView, context, uiThread, humanFirstBuy).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "Husk at skrive navnet på et Håndværk!", Toast.LENGTH_SHORT).show();
                    getCraftsAlert(thisView, context, uiThread, humanFirstBuy).show();
                }
            }
        });
        if (!humanFirstBuy) {
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { //TODO: overwite in create character to reappear if not done!
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        } else {
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    Toast.makeText(context, "Du skal vælge et Håndværk nu!", Toast.LENGTH_SHORT).show();
                    getCraftsAlert(thisView, context, uiThread, humanFirstBuy).show();
                }
            });
        }
        return builder;
    }

    public AlertDialog.Builder getStartChoiceAlert(View thisView, Context context, Handler uiThread){
        builder.setTitle("Start Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_starter, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin = alertView.findViewById(R.id.startspinner);
        ArrayList<AbilityDTO> collected;
        ArrayList<String> names = new ArrayList<>();

        if (abilityRepo.getStarterAbilities().size() == 0) {
            Result<List<AbilityDTO>> res = abilityRepo.getStarters();
            if (res instanceof Result.Success){
                collected = (ArrayList) ((Result.Success<List<AbilityDTO>>) res).getData();
            } else{
                Log.d("PopupHandler", "getStartChoiceAlert: couldn't get startabilities");
                return null;
            }
        } else {
            collected = abilityRepo.getStarterAbilities();
        }


        names.add("Vælg startevne!");
        for (AbilityDTO dto : collected){
            names.add(dto.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);



        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pos = spin.getSelectedItemPosition() - 1;
                if (pos != -1){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<AbilityDTO>> res = abilityRepo.confirmBuy(charRepo.getCurrentChar().getIdcharacter(), 39); // id 39 == tilbagevendt (starter evnen)
                        String commandType = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), collected.get(pos).getId());
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                switch (commandType) { //TODO: copy from createCharacterFragment
                                    case "auto": //do nothing
                                        Log.d("CharacterCreation", "correct auto getting ability");
                                        break;
                                    case "HÅNDVÆRK":
                                        Log.d("CharacterCreation", "Getting craft ability");
                                        getCraftsAlert(thisView, context, uiThread, true).show();
                                        break;
                                    default: //Error
                                        Log.d("CharacterCreation", "error getting ability");
                                        //TODO: handle error
                                        break;
                                }
                                Toast.makeText(context, String.format("evnen '%s' opnået!", collected.get(pos).getName()), Toast.LENGTH_SHORT).show();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
                                HomeViewModel.getInstance().startGetThread(charRepo.getCurrentChar().getIdcharacter());
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Fejl i at få evne til 3 EP, prøv igen!", Toast.LENGTH_SHORT).show();
                                getStartChoiceAlert(thisView, context, uiThread).show();
                                dialog.dismiss();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "husk at vælge!" , Toast.LENGTH_SHORT).show();
                    getStartChoiceAlert(thisView, context, uiThread).show();
                    dialog.dismiss();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                Toast.makeText(context, "Du skal vælge nu!", Toast.LENGTH_SHORT).show();
                getStartChoiceAlert(thisView, context, uiThread).show();
            }
        });
        return builder;
    } // Mørkskabt start

    public AlertDialog.Builder get3EPChoiceAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currAbilities){
        builder.setTitle("3EP Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_3ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin = alertView.findViewById(R.id.ep3spinner);
        ArrayList<AbilityDTO> kampDTOS = skillViewModel.getKampAbilities().getValue();
        ArrayList<AbilityDTO> snigerDTOS = skillViewModel.getSnigerAbilities().getValue();
        ArrayList<AbilityDTO> videnDTOS = skillViewModel.getVidenAbilities().getValue();
        ArrayList<AbilityDTO> alleDTOS = skillViewModel.getAlleAbilities().getValue();
        ArrayList<AbilityDTO> raceDTOs = skillViewModel.getRaceAbilities().getValue();
        ArrayList<AbilityDTO> collected = new ArrayList<>();

        ArrayList<AbilityDTO> ep3DTOS = new ArrayList<>(); // finding all possible 3ep abilities
        ArrayList<String> names = new ArrayList<>();
        if (kampDTOS != null && snigerDTOS != null && videnDTOS != null && alleDTOS != null && raceDTOs != null){
            collected.addAll(kampDTOS); //adding all abilities to collected
            collected.addAll(snigerDTOS);
            collected.addAll(videnDTOS);
            collected.addAll(alleDTOS);
            collected.addAll(raceDTOs);


            names.add("Vælg evne!");
            for (AbilityDTO dto : collected){
                boolean notOwned = true;
                for (int id : currAbilities){
                    if (id == dto.getId()){
                        notOwned = false;
                        break;
                    }
                }
                boolean ownParent = true;
                if (dto.getIdparent() != 0){
                    ownParent = false;
                    for (int id : currAbilities){
                        if (id == dto.getIdparent()){
                            ownParent = true;
                            break;
                        }
                    }
                }

                if (notOwned && ownParent && dto.getCost() == 3){
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
                if (names.size() != 0) {
                    int pos = spin.getSelectedItemPosition() - 1;
                    if (pos != -1) {
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(() -> {
                            Result<List<AbilityDTO>> res = abilityRepo.confirmBuy(charRepo.getCurrentChar().getIdcharacter(), 4); // id 4 == lille talent (3EP evnen)
                            String command = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), ep3DTOS.get(pos).getId(), true);
                            charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                            uiThread.post(() -> {
                                if (res instanceof Result.Success) {
                                    buyOtherAbility(command, thisView, context, uiThread, currAbilities);
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
                        Toast.makeText(context, "husk at vælge!", Toast.LENGTH_SHORT).show();
                        get3EPChoiceAlert(thisView, context, uiThread, currAbilities).show();
                    }
                } else {
                    Toast.makeText(context, "Du har ikke nogle valg. Køb andre evner først!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
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

    public AlertDialog.Builder get3EPChoiceAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currAbilities, boolean free){
        builder.setTitle("3EP Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_3ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin = alertView.findViewById(R.id.ep3spinner);
        ArrayList<AbilityDTO> kampDTOS = skillViewModel.getKampAbilities().getValue();
        ArrayList<AbilityDTO> snigerDTOS = skillViewModel.getSnigerAbilities().getValue();
        ArrayList<AbilityDTO> videnDTOS = skillViewModel.getVidenAbilities().getValue();
        ArrayList<AbilityDTO> alleDTOS = skillViewModel.getAlleAbilities().getValue();
        ArrayList<AbilityDTO> raceDTOs = skillViewModel.getRaceAbilities().getValue();
        ArrayList<AbilityDTO> collected = new ArrayList<>();

        ArrayList<AbilityDTO> ep3DTOS = new ArrayList<>(); // finding all possible 3ep abilities
        ArrayList<String> names = new ArrayList<>();
        if (kampDTOS != null && snigerDTOS != null && videnDTOS != null && alleDTOS != null && raceDTOs != null){
            collected.addAll(kampDTOS); //adding all abilities to collected
            collected.addAll(snigerDTOS);
            collected.addAll(videnDTOS);
            collected.addAll(alleDTOS);
            collected.addAll(raceDTOs);


            names.add("Vælg evne!");
            for (AbilityDTO dto : collected){
                boolean notOwned = true;
                for (int id : currAbilities){
                    if (id == dto.getId()){
                        notOwned = false;
                        break;
                    }
                }
                boolean ownParent = true;
                if (dto.getIdparent() != 0){
                    ownParent = false;
                    for (int id : currAbilities){
                        if (id == dto.getIdparent()){
                            ownParent = true;
                            break;
                        }
                    }
                }

                if (notOwned && ownParent && dto.getCost() == 3){
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
                if (names.size() != 0) {
                    int pos = spin.getSelectedItemPosition() - 1;
                    if (pos != -1) {
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(() -> {
                            String command = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), ep3DTOS.get(pos).getId(), free); // getting the free thingy
                            Result<AbilityDTO> res = abilityRepo.freeGet(charRepo.getCurrentChar().getIdcharacter(), 4); // id 4 == lille talent (3EP evnen)
                            charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                            uiThread.post(() -> {
                                if (res instanceof Result.Success) {
                                    buyOtherAbility(command, thisView, context, uiThread, currAbilities);
                                    Toast.makeText(context, String.format("evnen '%s' opnået!", ep3DTOS.get(pos).getName()), Toast.LENGTH_SHORT).show();
                                    skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                    skillViewModel.getUpdate().postValue(true);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Fejl i at få evne til 3 EP, prøv igen!", Toast.LENGTH_SHORT).show();
                                    get3EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                                }
                            });
                        });
                    } else {
                        Toast.makeText(context, "husk at vælge!", Toast.LENGTH_SHORT).show();
                        get3EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                    }
                } else {
                    Toast.makeText(context, "Du har ikke nogle valg. Køb andre evner først!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });


        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Du skal vælge nu!" , Toast.LENGTH_SHORT).show();
                get3EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
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
        ArrayList<AbilityDTO> raceDTOs = skillViewModel.getRaceAbilities().getValue();
        ArrayList<AbilityDTO> collected = new ArrayList<>();

        ArrayList<AbilityDTO> ep4DTOS = new ArrayList<>(); // finding all possible 3ep abilities
        ArrayList<String> names = new ArrayList<>();
        if (kampDTOS != null && snigerDTOS != null && videnDTOS != null && alleDTOS != null && raceDTOs != null){
            collected.addAll(kampDTOS); //adding all abilities to collected
            collected.addAll(snigerDTOS);
            collected.addAll(videnDTOS);
            collected.addAll(alleDTOS);
            collected.addAll(raceDTOs);


            names.add("Vælg evne!");
            for (AbilityDTO dto : collected){
                boolean notOwned = true;
                for (int id : currAbilities){
                    if (id == dto.getId()){
                        notOwned = false;
                        break;
                    }
                }
                boolean ownParent = true;
                if (dto.getIdparent() != 0){
                    ownParent = false;
                    for (int id : currAbilities){
                        if (id == dto.getIdparent()){
                            ownParent = true;
                            break;
                        }
                    }
                }

                if (notOwned && ownParent && dto.getCost() == 4){
                    ep4DTOS.add(dto);
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
                if (names.size() != 0){
                    int pos = spin.getSelectedItemPosition() - 1;
                    if (pos != -1) {
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(() -> {
                            Result<List<AbilityDTO>> res = abilityRepo.confirmBuy(charRepo.getCurrentChar().getIdcharacter(), 5); // id 5 == stort talent (4EP evnen)
                            String command = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), ep4DTOS.get(pos).getId(), true);
                            charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                            uiThread.post(() -> {
                                if (res instanceof Result.Success) {
                                    buyOtherAbility(command, thisView, context, uiThread, currAbilities);
                                    Toast.makeText(context, String.format("evnen '%s' opnået!", ep4DTOS.get(pos).getName()), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "husk at vælge!", Toast.LENGTH_SHORT).show();
                        get3EPChoiceAlert(thisView, context, uiThread, currAbilities).show();
                    }
                } else {
                    Toast.makeText(context, "Du har ikke nogle valg. Køb andre evner først!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
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

    public AlertDialog.Builder get4EPChoiceAlert(View thisView, Context context, Handler uiThread, ArrayList<Integer> currAbilities, boolean free){
        builder.setTitle("4EP Evne!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_choice_4ep, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        Spinner spin = alertView.findViewById(R.id.ep3spinner);
        ArrayList<AbilityDTO> kampDTOS = skillViewModel.getKampAbilities().getValue();
        ArrayList<AbilityDTO> snigerDTOS = skillViewModel.getSnigerAbilities().getValue();
        ArrayList<AbilityDTO> videnDTOS = skillViewModel.getVidenAbilities().getValue();
        ArrayList<AbilityDTO> alleDTOS = skillViewModel.getAlleAbilities().getValue();
        ArrayList<AbilityDTO> raceDTOs = skillViewModel.getRaceAbilities().getValue();
        ArrayList<AbilityDTO> collected = new ArrayList<>();

        ArrayList<AbilityDTO> ep4DTOS = new ArrayList<>(); // finding all possible 3ep abilities
        ArrayList<String> names = new ArrayList<>();
        if (kampDTOS != null && snigerDTOS != null && videnDTOS != null && alleDTOS != null && raceDTOs != null){
            collected.addAll(kampDTOS); //adding all abilities to alleDTOS
            collected.addAll(snigerDTOS);
            collected.addAll(videnDTOS);
            collected.addAll(alleDTOS);
            collected.addAll(raceDTOs);


            names.add("Vælg evne!");
            for (AbilityDTO dto : collected){
                boolean notOwned = true;
                for (int id : currAbilities){
                    if (id == dto.getId()){
                        notOwned = false;
                        break;
                    }
                }
                boolean ownParent = true;
                if (dto.getIdparent() != 0){
                    ownParent = false;
                    for (int id : currAbilities){
                        if (id == dto.getIdparent()){
                            ownParent = true;
                            break;
                        }
                    }
                }

                if (notOwned && ownParent && dto.getCost() == 4){
                    ep4DTOS.add(dto);
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
                if (names.size() != 1) {
                    int pos = spin.getSelectedItemPosition() - 1;
                    if (pos != -1) {
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(() -> {
                            Result<AbilityDTO> res = abilityRepo.freeGet(charRepo.getCurrentChar().getIdcharacter(),  5); // id 5 == stort talent (4EP evnen)
                            String command = abilityRepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), ep4DTOS.get(pos).getId(), free);
                            charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                            uiThread.post(() -> {
                                if (res instanceof Result.Success) {
                                    buyOtherAbility(command, thisView, context, uiThread, currAbilities);
                                    Toast.makeText(context, String.format("evnen '%s' opnået!", ep4DTOS.get(pos).getName()), Toast.LENGTH_SHORT).show();
                                    skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                    skillViewModel.getUpdate().postValue(true);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Fejl i at få evne til 4 EP, prøv igen!", Toast.LENGTH_SHORT).show();
                                    get4EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                                    dialog.dismiss();
                                }
                            });
                        });
                    } else {
                        Toast.makeText(context, "husk at vælge!", Toast.LENGTH_SHORT).show();
                        get4EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(context, "Du har ikke nogle valg. Køb andre evner først!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Du skal vælge nu!" , Toast.LENGTH_SHORT).show();
                get4EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                dialog.cancel();
            }
        });
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AlertDialog.Builder editEvent(View thisView, EventDTO eventDTO, Handler uiThread, NavController navController) {
        builder.setTitle("Rediger Event");
        View alertView = LayoutInflater.from(context).inflate(R.layout.fragment_admin_create_event, (ViewGroup) thisView.getRootView(), false);
        final EditText title, startDate, endDate, address, info, hyperlink;
        final String[] timeStart = new String[1];
        final String[] timeEnd = new String[1];
        Button create;
        title = alertView.findViewById(R.id.editText_Title);
        startDate = alertView.findViewById(R.id.editTextStartDate);
        endDate = alertView.findViewById(R.id.editTextEndDate);
        address = alertView.findViewById(R.id.editText_Address);
        info = alertView.findViewById(R.id.eventInformation);
        create = alertView.findViewById(R.id.create_event);
        hyperlink = alertView.findViewById(R.id.editText_hyperlink);
        create.setEnabled(false);
        create.setVisibility(View.INVISIBLE);
        create.setHeight(0);
        title.setText(eventDTO.getName());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-YYYY");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        startDate.setText(dateFormat.format(eventDTO.getStartDate().toLocalDate())+" "+ timeFormat.format(eventDTO.getStartDate().toLocalTime())+":00");
        endDate.setText(dateFormat.format(eventDTO.getEndDate().toLocalDate())+" "+ timeFormat.format(eventDTO.getEndDate().toLocalTime())+":00");
        final Boolean[] startChanged = {false};
        final Boolean[] endChanged = { false };
        address.setText(eventDTO.getAddress());
        info.setText(eventDTO.getInfo());
        hyperlink.setText(eventDTO.getHyperlink());

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
                                String curDate = String.format("%02d-%02d", dayOfMonth, (monthOfYear + 1)) + "-" + year;
                                String dateToDTO = year + "-" + String.format("%02d-%02d", (monthOfYear + 1), dayOfMonth);
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
                                                timeStart[0] = dateToDTO + "T" + curTime + ":00";
                                                Log.d("date set", timeStart[0]);
                                                startDate.setText(startDate.getText() + " " + curTime + ":00");
                                                startChanged[0] = true;
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
                                String curDate = String.format("%02d-%02d", dayOfMonth, (monthOfYear + 1)) + "-" + year;
                                String dateToDTO = year + "-" + String.format("%02d-%02d", (monthOfYear + 1), dayOfMonth);
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
                                                timeEnd[0] = dateToDTO + "T" + curTime + ":00";
                                                Log.d("date set", timeEnd[0]);
                                                endDate.setText(endDate.getText() + " " + curTime + ":00");
                                                endChanged[0] = true;
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    EventDAO eventDAO = new EventDAO();
                    eventDTO.setName(title.getText() + "");
                    eventDTO.setAddress(address.getText() + "");
                    if(startChanged[0]) eventDTO.setStartDate(timeStart[0]);
                    if(endChanged[0]) eventDTO.setEndDate(timeEnd[0]);
                    eventDTO.setInfo(info.getText() + "");
                    eventDTO.setHyperlink(hyperlink.getText()+"");
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Log.d("Event", "Event edited: " + eventDTO.getEventID());
                        eventDAO.editEvent(eventDTO);
                        uiThread.post(() -> {
                            Toast.makeText(thisView.getContext(), "Event rettet", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            navController.popBackStack();
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

    private void buyOtherAbility(String command ,View thisView, Context context, Handler uiThread, ArrayList<Integer> currAbilities){
        switch (command.split(",")[0]){
            case "HÅNDVÆRK":
                getCraftsAlert(thisView, context, uiThread, true).show();
                break;
            case "3EP":
                get3EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                break;
            case "4EP":
                get4EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                break;
            case "EVNE":
                break;
            case "KRYS2EP":
                getKrys2EPAlert(thisView, context, uiThread, currAbilities, true).show();
                break;
            case "KRYS":
                ArrayList<String> frees = new ArrayList<>();
                frees.add(command.split(",")[1]);
                frees.add(command.split(",")[2]);
                for (String command2 : frees){
                    switch (command2){
                        case "HÅNDVÆRK":
                            getCraftsAlert(thisView, context, uiThread, true).show();
                            break;
                        case "3EP":
                            get3EPChoiceAlert(thisView, context, uiThread, currAbilities, true).show();
                            break;
                        default:
                            break;
                    }
                }
                break;
            case "STARTEVNE": //only happens in create character and should be handled there
                Log.d("CharacterCreation", "Getting starter ability");
                Executor bgThread4 = Executors.newSingleThreadExecutor();
                bgThread4.execute(() -> {
                    AlertDialog.Builder builder = getStartChoiceAlert(thisView, context, uiThread);
                    uiThread.post(() ->{
                        builder.show();
                    });
                });
                break;
        }
    }

}
