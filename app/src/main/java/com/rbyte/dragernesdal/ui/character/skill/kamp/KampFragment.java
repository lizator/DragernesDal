package com.rbyte.dragernesdal.ui.character.skill.kamp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class KampFragment extends Fragment {

    private KampViewModel kampViewModel = new KampViewModel();
    private SkillViewModel skillViewModel = SkillViewModel.getInstance();
    private AbilityRepository abilityrepo;
    private CharacterRepository charRepo;
    private ArrayList<AbilityDTO> abilityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private ArrayList<Integer> currentAbilityIDs = new ArrayList<>();
    private PopupHandler popHandler;
    private Handler uiThread = new Handler();
    private View root2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_skill_kamp, container, false);
        abilityrepo = AbilityRepository.getInstance();
        charRepo = CharacterRepository.getInstance();
        popHandler = new PopupHandler(getContext());

        recyclerView = (RecyclerView) root.findViewById(R.id.kampRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);

        for (AbilityDTO dto : charRepo.getCurrentAbilitiesList()){
            currentAbilityIDs.add(dto.getId());
        }

        skillViewModel.getKampAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                abilityList = abilityDTOS;
                abilityAdapter.notifyDataSetChanged();
            }
        });

        kampViewModel.getCurrAbilitys().observe(getViewLifecycleOwner(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> integers) {
                currentAbilityIDs = integers;
                abilityAdapter.notifyDataSetChanged();
            }
        });


        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        int currCharacterID = prefs.getInt(HomeFragment.CHARACTER_ID_SAVESPACE, -1);
        if (currCharacterID != -1) { //TO-DO: error handle (not ever likely to happen, nvm)
            Executor bgThread2 = Executors.newSingleThreadExecutor();
            bgThread2.execute(() -> {
                Result<List<AbilityDTO>> res = charRepo.getAbilitiesByCharacterID(currCharacterID);
                uiThread.post(() -> {
                    if (res instanceof Result.Success) {
                        ArrayList<AbilityDTO> data = ((Result.Success<ArrayList<AbilityDTO>>) res).getData();
                        if (data == null){
                            Log.d("KampFragment", "data null");
                        }
                        currentAbilityIDs.clear();
                        for (AbilityDTO dto : data){
                            currentAbilityIDs.add(dto.getId());
                        }
                        //abilityAdapter.notifyDataSetChanged();
                    }
                });

            });
        }
        root2 = root;
        return root;
    }

    class AbilityViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView cost;
        Button buybtn;
        ImageView checkimg;
        View view;
        public AbilityViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.abilityName);
            cost = abilityViews.findViewById(R.id.abilityCostTv);
            buybtn = abilityViews.findViewById(R.id.buyAbilitybtn);
            checkimg = abilityViews.findViewById(R.id.checkImage);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            cost.setBackgroundResource(android.R.drawable.list_selector_background);
            buybtn.setBackgroundResource(android.R.drawable.list_selector_background);
            checkimg.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (abilityList != null) return abilityList.size();
            return 0;
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_skill_line, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(abilityList.get(position).getName());
            vh.cost.setText(abilityList.get(position).getCost() + "");
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
            boolean bought = false;
            for (int id : currentAbilityIDs){
                if (abilityList.get(position).getId() == id){
                    vh.buybtn.setVisibility(View.GONE);
                    vh.checkimg.setVisibility(View.VISIBLE);
                    bought = true;
                    break;
                }
            }
            if(!bought){
                int currEP = skillViewModel.getCurrentEP().getValue();
                int parent = abilityList.get(position).getIdparent();
                Boolean ownsParent = false;
                if (parent != 0) {
                    for (int id : currentAbilityIDs){
                        if (id == parent){
                            ownsParent = true;
                                    break;
                        }
                    }
                }
                if (abilityList.get(position).getCost() <= currEP && (
                        parent == 0 || ownsParent)){ //correcte statement (testet with toasts)
                    vh.buybtn.setClickable(true);
                    vh.buybtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: make popup, buy ability and refresh
                            popHandler.getConfirmBuyAlert(root2,
                                    abilityList.get(position).getName(),
                                    abilityList.get(position).getCost(),
                                    charRepo.getCurrentChar().getCurrentep(),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Executor bgThread1 = Executors.newSingleThreadExecutor();
                                            bgThread1.execute(() -> {
                                                String command = abilityrepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), abilityList.get(position).getId());
                                                charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                                                uiThread.post(() -> {
                                                    if (command != "auto") { //new popup needed
                                                        //TODO: create more popups
                                                    }
                                                    skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                                    abilityAdapter.notifyDataSetChanged();
                                                    skillViewModel.getUpdate().postValue(true);
                                                    ArrayList<Integer> ls = new ArrayList<>();
                                                    for (AbilityDTO dto : charRepo.getCurrentAbilitiesList()){
                                                        ls.add(dto.getId());
                                                    }
                                                    skillViewModel.getCurrentAbilityIDs().postValue(ls);
                                                });
                                            });
                                        }
                                    }).show();
                        }
                    });
                } else {
                    vh.buybtn.setVisibility(View.GONE);
                }
            }

        }
    }


}