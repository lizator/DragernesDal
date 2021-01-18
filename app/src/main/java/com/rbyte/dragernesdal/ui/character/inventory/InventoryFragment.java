package com.rbyte.dragernesdal.ui.character.inventory;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.inventory.InventoryRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InventoryFragment extends Fragment {

    private InventoryViewModel inventoryViewModel;
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();
    private InventoryAdapter adapter = new InventoryAdapter();
    private PopupHandler popHandler;
    private View root2;
    private Handler uithread = new Handler();
    private ArrayList<InventoryDTO> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private EditText goldEdit;
    private EditText silverEdit;
    private EditText copperEdit;
    private TextView updateTV;
    private InventoryDTO gold;
    private InventoryDTO silver;
    private InventoryDTO copper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        inventoryViewModel = InventoryViewModel.getInstance();
        View root = inflater.inflate(R.layout.fragment_character_inventory, container, false);
        popHandler = new PopupHandler(getContext());

        updateTV = root.findViewById(R.id.updateTV);
        goldEdit = root.findViewById(R.id.goldEdit);
        silverEdit = root.findViewById(R.id.silverEdit);
        copperEdit = root.findViewById(R.id.copperEdit);
        gold = new InventoryDTO();
        silver = new InventoryDTO();
        copper = new InventoryDTO();

        inventoryViewModel.getInventory().observe(getViewLifecycleOwner(), new Observer<ArrayList<InventoryDTO>>() {
            @Override
            public void onChanged(ArrayList<InventoryDTO> inventory) {
                if (inventory != null && inventory.size() != 0) {

                    gold = inventory.get(0);
                    if(inventory.size() > 1) silver = inventory.get(1);
                    if(inventory.size() > 2) copper = inventory.get(2);
                    goldEdit.setText(gold.getAmount() + "");
                    silverEdit.setText(silver.getAmount() + "");
                    copperEdit.setText(copper.getAmount() + "");

                    if (inventory.size() > 3) {
                        items.clear();
                        for (int i = 3; i < inventory.size(); i++) {
                            items.add(inventory.get(i));
                        }
                    } else {
                        items.clear();
                        InventoryDTO dto = new InventoryDTO();
                        dto.setIdItem(3);
                        dto.setIdInventoryRelation(inventoryRepo.getRelationID());
                        dto.setItemName("Indsæt Genstand");
                        dto.setAmount(0);
                        items.add(dto);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        recyclerView = (RecyclerView) root.findViewById(R.id.inventoryRecycler);
        llm = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        ((Button) root.findViewById(R.id.newLinebtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryDTO dto = new InventoryDTO();
                if (items.size() > 0) dto.setIdItem(items.get(items.size()-1).getIdItem()+1);
                else  dto.setIdItem(3);
                dto.setIdInventoryRelation(inventoryRepo.getRelationID());
                dto.setItemName("");
                dto.setAmount(0);
                items.add(dto);
                adapter.notifyDataSetChanged();
            }
        });

        ((Button) root.findViewById(R.id.savebtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() -> {
                    ArrayList<InventoryDTO> newInventory = new ArrayList<>();
                    gold.setAmount(Integer.parseInt(goldEdit.getText().toString()));
                    silver.setAmount(Integer.parseInt(silverEdit.getText().toString()));
                    copper.setAmount(Integer.parseInt(copperEdit.getText().toString()));
                    newInventory.add(gold);
                    newInventory.add(silver);
                    newInventory.add(copper);
                    newInventory.addAll(items);

                    inventoryRepo.saveInventory(charRepo.getCurrentChar().getIdcharacter(), newInventory);
                    inventoryRepo.startGetThread();

                    uithread.post(() -> {
                        inventoryViewModel.updateStatus();
                    });

                });
            }
        });


        inventoryViewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("update")){
                    updateTV.setVisibility(View.VISIBLE);
                } else {
                    updateTV.setVisibility(View.GONE);
                }
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in InventoryFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        inventoryViewModel.updateStatus();
        inventoryViewModel.updateInventory();
        root2 = root;
        return root;
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder{
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
            return items.size();
        }

        @Override
        public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_inventory_line, parent, false);
            InventoryViewHolder vh = new InventoryViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(InventoryViewHolder vh, int position) {
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));

            TextWatcher lineChangeWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    for (int i = 0; i < items.size(); i++){
                        View view = llm.findViewByPosition(i);
                        if (view != null) {
                            String itemName = ((EditText) view.findViewById(R.id.itemNameEdit)).getText().toString();
                            String number = ((EditText) view.findViewById(R.id.itemAmountEdit)).getText().toString();
                            items.get(i).setItemName(itemName);
                            try {
                                int amount = Integer.parseInt(number);
                                items.get(i).setAmount(amount);
                            } catch (NumberFormatException e){
                                Log.d("InventoryFragment", "lineChangeWatcher: amount not found error");
                            }

                        }
                    }
                }
            };

            vh.name.setText(items.get(position).getItemName());
            vh.amount.setText(items.get(position).getAmount() + "");
            vh.name.addTextChangedListener(lineChangeWatcher);
            vh.amount.addTextChangedListener(lineChangeWatcher);

            vh.closeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getConfirmRemoveInventoryAlert(root2, items.get(position).getItemName(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            items.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }).show();
                }
            });
        }
    }
}