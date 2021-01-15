package com.rbyte.dragernesdal.ui.admin.event;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.inventory.InventoryRepository;

public class InventoryCheckout {
    private View view;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Context context;
    private Button savebtn, newLinebtn;
    private EditText copper, silver, gold;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CharacterDTO characterDTO;
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();


    public InventoryCheckout(Context context, View viewFragment, CharacterDTO characterDTO){
        this.context = context;
        builder = new AlertDialog.Builder(context);
        this.view = LayoutInflater.from(context).inflate(R.layout.fragment_character_inventory, (ViewGroup) viewFragment.getRootView(),false);
        this.characterDTO = characterDTO;
        setup();
    }

    private void setup(){
        findElements();
        setElements();
        makeDialog();
    }

    private void makeDialog(){
        builder.setView(view);
    }

    private void setElements(){
        inventoryRepo.getActualInventory(characterDTO.getIdcharacter());
    }

    private void findElements(){
        savebtn = view.findViewById(R.id.savebtn);
        newLinebtn = view.findViewById(R.id.newLinebtn);
        copper = view.findViewById(R.id.copperEdit);
        silver = view.findViewById(R.id.silverEdit);
        gold = view.findViewById(R.id.goldEdit);
        recyclerView = view.findViewById(R.id.inventoryRecycler);
        linearLayoutManager = new LinearLayoutManager(context);
    }

    public void showInventory(){
        dialog = builder.show();
    }

    public void closeInventory(){
        if(dialog != null) dialog.dismiss();
    }
}
