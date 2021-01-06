package com.rbyte.dragernesdal.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.rbyte.dragernesdal.R;

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

    public void buildAbilityPopup(android.content.Context context, String command){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

    }

    

}
