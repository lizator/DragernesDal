package com.rbyte.dragernesdal.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.provider.SelfDestructiveThread;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.security.cert.CertificateRevokedException;
import java.util.ArrayList;
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
                        Result<AbilityDTO> res = abilityRepo.craftBuy(charRepo.getCurrentChar().getIdcharacter(), craft);
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                        if (humanFirstBuy) {
                            charRepo.getCurrentChar().setCurrentep(5);// reset from buying craft
                            charRepo.updateCharacter(charRepo.getCurrentChar());
                            abilityRepo.freeGet(charRepo.getCurrentChar().getIdcharacter(), 3); //id == 3 (Proffesion - start human ability)
                        }
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Toast.makeText(context, String.format("Håndværk '%s' oprettet!", craft), Toast.LENGTH_SHORT).show();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
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
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { //TODO: overwite in create character to reappear if not done!
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (humanFirstBuy) {
                    Toast.makeText(context, "Du skal vælge et Håndværk nu!", Toast.LENGTH_SHORT).show();
                    getCraftsAlert(thisView, context, uiThread, humanFirstBuy).show();
                }
                dialog.cancel();
            }
        });
        if (humanFirstBuy){
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
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "husk at vælge!" , Toast.LENGTH_SHORT).show();
                    getStartChoiceAlert(thisView, context, uiThread).show();
                }
            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Du skal vælge nu!" , Toast.LENGTH_SHORT).show();
                getStartChoiceAlert(thisView, context, uiThread).show();
                dialog.cancel();
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


    

}
