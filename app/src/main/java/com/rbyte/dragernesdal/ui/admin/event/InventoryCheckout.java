package com.rbyte.dragernesdal.ui.admin.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.inventory.InventoryRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.inventory.InventoryFragment;
import com.rbyte.dragernesdal.ui.character.inventory.InventoryViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InventoryCheckout {
    private final View view;
    private final AlertDialog.Builder builder;
    private final AlertDialog dialog;
    private final Context context;
    private final Button savebtn, newLinebtn;
    private final EditText copper, silver, gold;
    private final RecyclerView recyclerView;
    private final LinearLayoutManager linearLayoutManager;
    private final CharacterDTO characterDTO;
    private final CharacterRepository charRepo = CharacterRepository.getInstance();
    private final InventoryViewModel inventoryViewModel = InventoryViewModel.getInstance();
    private final LifecycleOwner lifecycleOwner;
    private final ArrayList<InventoryDTO> inventoryDTOS = new ArrayList<>();
    private final InventoryAdapter adapter = new InventoryAdapter();
    private final InventoryRepository inventoryRepo = InventoryRepository.getInstance();
    private Handler uithread = new Handler();

    public InventoryCheckout(Context context, View viewFragment, CharacterDTO characterDTO, LifecycleOwner lifecycleOwner) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
        this.view = LayoutInflater.from(context).inflate(R.layout.fragment_character_inventory, (ViewGroup) viewFragment.getRootView(), false);
        this.characterDTO = characterDTO;
        savebtn = view.findViewById(R.id.savebtn);
        newLinebtn = view.findViewById(R.id.newLinebtn);
        copper = view.findViewById(R.id.copperEdit);
        silver = view.findViewById(R.id.silverEdit);
        gold = view.findViewById(R.id.goldEdit);
        recyclerView = view.findViewById(R.id.inventoryRecycler);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        this.lifecycleOwner = lifecycleOwner;
        builder.setView(view);
        inventoryViewModel.updateInventory(characterDTO);
        inventoryViewModel.getInventory().observe(lifecycleOwner, new Observer<ArrayList<InventoryDTO>>() {
            @Override
            public void onChanged(ArrayList<InventoryDTO> inventoryDTOS) {
                onInventoryChange(inventoryDTOS);
            }
        });
        savebtn.setOnClickListener(saveInventory());
        newLinebtn.setOnClickListener(newLine());
        dialog = builder.show();
    }

    private void onInventoryChange(ArrayList<InventoryDTO> inventoryDTOS) {
        if (inventoryDTOS != null && inventoryDTOS.size() != 0) {
            InventoryCheckout.this.inventoryDTOS.clear();
            InventoryCheckout.this.inventoryDTOS.addAll(inventoryDTOS);
            for(int i = inventoryDTOS.size()-1; i >= 0; i--){
                final InventoryDTO n = inventoryDTOS.get(i);
                if (n.getIdItem() == 0) {
                    copper.setText(n.getAmount() + "");
                    InventoryCheckout.this.inventoryDTOS.remove(i);
                } else if (n.getIdItem() == 1) {
                    gold.setText(n.getAmount() + "");
                    InventoryCheckout.this.inventoryDTOS.remove(i);
                } else if (n.getIdItem() == 2) {
                    silver.setText(n.getAmount() + "");
                    InventoryCheckout.this.inventoryDTOS.remove(i);
                }
            }
            adapter.notifyDataSetChanged();
        }

    }


    public void showInventory() {
        dialog.show();
    }

    public void closeInventory() {
        dialog.dismiss();
    }


    private View.OnClickListener newLine(){
        View.OnClickListener click = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                InventoryDTO dto = new InventoryDTO();
                if (inventoryDTOS.size() > 0) dto.setIdItem(inventoryDTOS.get(inventoryDTOS.size()-1).getIdItem()+1);
                else  dto.setIdItem(3);
                dto.setIdInventoryRelation(inventoryRepo.getRelationID());
                dto.setItemName("");
                dto.setAmount(0);
                inventoryDTOS.add(dto);
                adapter.notifyDataSetChanged();
            }
        };
        return click;
    }

    private View.OnClickListener saveInventory(){
        View.OnClickListener click = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() -> {
                    ArrayList<InventoryDTO> newInventory = new ArrayList<>();
                    InventoryDTO goldI = new InventoryDTO();
                    InventoryDTO silverI = new InventoryDTO();
                    InventoryDTO copperI = new InventoryDTO();
                    goldI.setAmount(Integer.parseInt(gold.getText().toString()));
                    silverI.setAmount(Integer.parseInt(silver.getText().toString()));
                    copperI.setAmount(Integer.parseInt(copper.getText().toString()));
                    goldI.setIdItem(0);
                    silverI.setIdItem(1);
                    copperI.setIdItem(2);
                    newInventory.add(goldI);
                    newInventory.add(silverI);
                    newInventory.add(copperI);
                    //newInventory.addAll(inventoryDTOS);


                    inventoryRepo.saveInventory(characterDTO.getIdcharacter(), newInventory);
                    inventoryRepo.startGetThread();

                    uithread.post(() -> {
                        inventoryViewModel.updateStatus();
                    });

                });
            }
        };
        return click;
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {
        EditText name;
        EditText amount;
        ImageView closeImg;
        View view;

        public InventoryViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.itemNameEdit);
            amount = abilityViews.findViewById(R.id.itemAmountEdit);
            closeImg = abilityViews.findViewById(R.id.closeLine);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
        }

    }

    class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {
        @Override
        public int getItemCount() {
            return inventoryDTOS.size();
        }

        @Override
        public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = LayoutInflater.from(context).inflate(R.layout.recycler_inventory_line, parent, false);
            InventoryViewHolder vh = new InventoryViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(InventoryViewHolder vh, int position) {
            InventoryDTO newInv = inventoryDTOS.get(position);
            vh.name.setText(newInv.getItemName() + "");
            vh.amount.setText(newInv.getAmount() + "");
            vh.closeImg.setOnClickListener(removeItem(position));
        }

        private View.OnClickListener removeItem(int position){
            View.OnClickListener click = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    PopupHandler popHandler = new PopupHandler(context);
                    popHandler.getConfirmRemoveInventoryAlert(view, inventoryDTOS.get(position).getItemName(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inventoryDTOS.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }).show();
                }
            };
            return click;
        }
    }
}