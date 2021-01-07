package com.rbyte.dragernesdal.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;

public class PopupHandler {
    private AlertDialog.Builder builder;
    private Context context;

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

    public AlertDialog.Builder getCraftsAlert(View thisView, Context context, int characterID, Handler uiThread){
        builder.setTitle("Håndværk!");
        View alertView = LayoutInflater.from(context).inflate(R.layout.popup_input_a_craft, (ViewGroup) thisView.getRootView(), false);
        builder.setView(alertView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = alertView.findViewById(R.id.editCraftName);
                String craft = input.getText().toString();
                CharacterRepository charRepo = CharacterRepository.getInstance();
                if (craft.length() != 0) {
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<AbilityDTO> res = AbilityRepository.getInstance().craftBuy(characterID, craft);
                        charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                        uiThread.post(() -> {
                            if (res instanceof Result.Success) {
                                Toast.makeText(context, String.format("Håndværk '%s' oprettet!", craft), Toast.LENGTH_SHORT).show();
                                SkillViewModel skillViewModel = SkillViewModel.getInstance();
                                skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                skillViewModel.getUpdate().postValue(true);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Fejl i opret håndværk, prøv igen!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(context, "Husk at skrive navnet på et Håndværk!", Toast.LENGTH_SHORT).show();
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

    public void buildAbilityPopup(android.content.Context context, String command){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

    }

    

}
