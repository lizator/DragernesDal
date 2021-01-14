package com.rbyte.dragernesdal.ui.character.inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class InventoryFragment extends Fragment {

    private InventoryViewModel inventoryViewModel;
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();
    private InventoryAdapter adapter = new InventoryAdapter();
    private PopupHandler popHandler;
    private View root2;
    private ArrayList<InventoryDTO> items = new ArrayList<>();
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        inventoryViewModel = InventoryViewModel.getInstance();
        View root = inflater.inflate(R.layout.fragment_character_inventory, container, false);
        popHandler = new PopupHandler(getContext());

        ArrayList<InventoryDTO> inventory = inventoryRepo.getInventory();
        InventoryDTO gold = inventory.get(0);
        InventoryDTO silver = inventory.get(1);
        InventoryDTO copper = inventory.get(2);

        if (inventory.size() > 3){
            items.clear();
            for (int i = 3; i < inventory.size(); i++){
                items.add(inventory.get(i));
            }
        }


        // adding last line with btn to recycler
        InventoryDTO dto = new InventoryDTO();
        dto.setIdItem(-1);
        items.add(dto);

        recyclerView = (RecyclerView) root.findViewById(R.id.inventoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(adapter);





        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in InventoryFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        root2 = root;
        return root;
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder{
        EditText name;
        EditText amount;
        LinearLayout lineView;
        Button addbtn;
        LinearLayout btnView;
        View view;
        public InventoryViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.itemNameEdit);
            amount = abilityViews.findViewById(R.id.itemAmountEdit);
            lineView = abilityViews.findViewById(R.id.itemLine);
            addbtn = abilityViews.findViewById(R.id.newLineBtn);
            btnView = abilityViews.findViewById(R.id.newItemLine);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            amount.setBackgroundResource(android.R.drawable.list_selector_background);
            addbtn.setBackgroundResource(android.R.drawable.list_selector_background);
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
            if (items.get(position).getIdItem() != -1) {
                vh.name.setText(items.get(position).getItemName());
                vh.amount.setText(items.get(position).getAmount() + "");
            } else {
                vh.lineView.setVisibility(View.GONE);
                vh.btnView.setVisibility(View.VISIBLE);
                vh.btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        InventoryDTO dto = new InventoryDTO();
                        if (items.size() == 1) {
                            dto.setIdItem(3);
                            dto.setIdInventoryRelation(inventoryRepo.getRelationID());
                        }
                        else {
                            dto.setIdItem(items.get(items.size()-2).getIdItem() + 1);
                            dto.setIdInventoryRelation(items.get(items.size()-2).getIdInventoryRelation());
                        }
                        dto.setItemName("Indsæt Genstand");
                        dto.setAmount(0);
                        items.add(items.size()-1, dto);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        }
    }
}