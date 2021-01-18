package com.rbyte.dragernesdal.ui.character.background;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.lang.Math.abs;

public class BackgroundFragment extends Fragment {

    private BackgroundViewModel backgroundViewModel;
    private AbilityRepository abilityRepo = AbilityRepository.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private ArrayList<AbilityDTO> abilityList = new ArrayList<AbilityDTO>();
    private PopupHandler popHandler;
    private Handler uiThread = new Handler();
    private View root2;
    private Button addBadbtn;
    private Button saveBackgroundbtn;
    private EditText backgroundEdit;

    private AbilityAdapter adapter = new AbilityAdapter();
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        backgroundViewModel = BackgroundViewModel.getInstance();
        View root = inflater.inflate(R.layout.fragment_character_background, container, false);
        popHandler = new PopupHandler(getContext());
        addBadbtn = root.findViewById(R.id.addBadbtn);
        saveBackgroundbtn = root.findViewById(R.id.saveBackgoundbtn);

        backgroundViewModel.getUpdate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                if (update){
                    insertBad();
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.badRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(adapter);

        insertBad();

        backgroundEdit = root.findViewById(R.id.backgroundEdit);
        backgroundEdit.setText(charRepo.getCurrentChar().getBackground());

        saveBackgroundbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newBackground = backgroundEdit.getText().toString();
                charRepo.getCurrentChar().setBackground(newBackground);
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() -> {
                    charRepo.updateCharacter(charRepo.getCurrentChar());
                    uiThread.post(() -> {
                        Toast.makeText(getContext(), "Bagground gemt!", Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in BackgroundFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        root2 = root;
        return root;
    }

    private void insertBad(){
        abilityList.clear();
        for (AbilityDTO dto : HomeViewModel.getInstance().getAbilities().getValue()){
            if (dto.getType().equals("Bad")){
                abilityList.add(dto);
                adapter.notifyDataSetChanged();
            }
        }
        //update btn
        if (abilityList.size() < 3) {
            addBadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Integer> currAbilityIDs = new ArrayList<>();
                    for (AbilityDTO dto : HomeViewModel.getInstance().getAbilities().getValue()){
                        currAbilityIDs.add(dto.getId());
                    }
                    popHandler.getBadChoiceAlert(root2, getContext(), uiThread, currAbilityIDs, backgroundViewModel.getBadAbilities().getValue()).show();
                }
            });
        } else {
            addBadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getInfoAlert(root2, "Overmuteret", "Du har allerede det maksimale antal dårlige karaktertræk!").show();
                }
            });
        }
    }



    private class AbilityViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView cost;
        View view;
        public AbilityViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.lineName);
            cost = abilityViews.findViewById(R.id.abilityCostTv);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            view.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    private class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (abilityList != null) return abilityList.size();
            return 0;
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_bad_line, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(abilityList.get(position).getName());
            vh.cost.setText(abs(abilityList.get(position).getCost()) + "");
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getInfoAlert(root2, abilityList.get(position).getName(), abilityList.get(position).getDesc()).show();
                }
            });
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));

        }
    }
}