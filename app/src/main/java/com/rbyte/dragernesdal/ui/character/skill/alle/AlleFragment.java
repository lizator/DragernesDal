package com.rbyte.dragernesdal.ui.character.skill.alle;

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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.background.BackgroundViewModel;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.home.HomeFragment;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AlleFragment extends Fragment {

    private SkillViewModel skillViewModel = SkillViewModel.getInstance();
    private BackgroundViewModel backgroundViewModel = BackgroundViewModel.getInstance();
    private AbilityRepository abilityrepo;
    private CharacterRepository charRepo;
    private ArrayList<AbilityDTO> abilityList = new ArrayList<>();
    private ArrayList<AbilityDTO> raceAbilityList = new ArrayList<>();
    private ArrayList<AbilityDTO> otherAbilityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView raceRecyclerView;
    private RecyclerView otherRecyclerView;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private RaceAbilityAdapter raceAbilityAdapter = new RaceAbilityAdapter();
    private OtherAbilityAdapter otherAbilityAdapter = new OtherAbilityAdapter();
    private ArrayList<Integer> currentAbilityIDs = new ArrayList<>();
    private PopupHandler popHandler;
    private Handler uiThread = new Handler();
    private View root2;
    private String[] normalTypes = new String[]{"Kamp", "Viden", "Sniger", "Race", "Bad"};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_skill_alle, container, false);
        abilityrepo = AbilityRepository.getInstance();
        charRepo = CharacterRepository.getInstance();
        popHandler = new PopupHandler(getContext());

        if (skillViewModel.getRaceAbilities().getValue() == null || skillViewModel.getRaceAbilities().getValue().size() == 0){
            skillViewModel.setRaceAbilities(charRepo.getCurrentChar().getIdrace());
        }

        recyclerView = (RecyclerView) root.findViewById(R.id.alleRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);

        raceRecyclerView = (RecyclerView) root.findViewById(R.id.raceRecycler);
        raceRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        raceRecyclerView.setAdapter(raceAbilityAdapter);

        for (AbilityDTO dto : charRepo.getCurrentAbilitiesList()){
            currentAbilityIDs.add(dto.getId());
            boolean special = true;
            for (String type : normalTypes){
                if (dto.getType().equals(type)){
                    if (dto.getType().equals("Race")){
                        RaceDTO race = HomeViewModel.getInstance().getmRace().getValue();
                        if (race != null && (race.getStart() == dto.getId() || race.getEp2() == dto.getId() || race.getEp3() == dto.getId() || race.getEp4() == dto.getId())){
                            special = false;
                            break;
                        }
                    } else {
                        special = false;
                        break;
                    }
                }
            }
            if (special){
                otherAbilityList.add(dto);
            }
        }

        otherRecyclerView = (RecyclerView) root.findViewById(R.id.otherRecycler);
        otherRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        otherRecyclerView.setAdapter(otherAbilityAdapter);

        skillViewModel.getAlleAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                abilityList = abilityDTOS;
                abilityAdapter.notifyDataSetChanged();
            }
        });

        skillViewModel.getRaceAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                raceAbilityList = abilityDTOS;
                raceAbilityAdapter.notifyDataSetChanged();
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
                            Log.d("AlleFragment", "data null");
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
        abilityAdapter.notifyDataSetChanged();
        return root;
    }

    private class AbilityViewHolder extends RecyclerView.ViewHolder{
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

    private class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
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
                                                        switch (command){
                                                            case "HÅNDVÆRK": // Only thing tha might be bought here
                                                                popHandler.getCraftsAlert(root2, getContext(), uiThread).show();
                                                                break;
                                                        }

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
                                                    abilityAdapter.notifyDataSetChanged();
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

    private class RaceAbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (raceAbilityList != null) return raceAbilityList.size();
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
            vh.name.setText(raceAbilityList.get(position).getName());
            vh.cost.setText(raceAbilityList.get(position).getCost() + "");
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
            boolean bought = false;
            for (int id : currentAbilityIDs){
                if (raceAbilityList.get(position).getId() == id){
                    vh.buybtn.setVisibility(View.GONE);
                    vh.checkimg.setVisibility(View.VISIBLE);
                    bought = true;
                    break;
                }
            }
            if(!bought){
                int currEP = skillViewModel.getCurrentEP().getValue();
                int parent = raceAbilityList.get(position).getIdparent();
                Boolean ownsParent = false;
                if (parent != 0) {
                    for (int id : currentAbilityIDs){
                        if (id == parent){
                            ownsParent = true;
                            break;
                        }
                    }
                }
                if (raceAbilityList.get(position).getCost() <= currEP && (
                        parent == 0 || ownsParent)){ //correcte statement (testet with toasts)
                    vh.buybtn.setClickable(true);
                    vh.buybtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: make popup, buy ability and refresh
                            popHandler.getConfirmBuyAlert(root2,
                                    raceAbilityList.get(position).getName(),
                                    raceAbilityList.get(position).getCost(),
                                    charRepo.getCurrentChar().getCurrentep(),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Executor bgThread1 = Executors.newSingleThreadExecutor();
                                            bgThread1.execute(() -> {
                                                String command = abilityrepo.tryBuy(charRepo.getCurrentChar().getIdcharacter(), raceAbilityList.get(position).getId());
                                                charRepo.getCharacterByID(charRepo.getCurrentChar().getIdcharacter());
                                                uiThread.post(() -> {
                                                    if (command.split(",")[0] != "auto") { //new popup needed
                                                        switch (command.split(",")[0]){
                                                            case "HÅNDVÆRK":
                                                                popHandler.getCraftsAlert(root2, getContext(), uiThread, false).show();
                                                                break;
                                                            case "3EP":
                                                                popHandler.get3EPChoiceAlert(root2, getContext(), uiThread, currentAbilityIDs).show();
                                                                break;
                                                            case "4EP":
                                                                popHandler.get4EPChoiceAlert(root2, getContext(), uiThread, currentAbilityIDs).show();
                                                                break;
                                                            case "EVNE":
                                                                break;
                                                            case "KRYS2EP":
                                                                popHandler.getKrys2EPAlert(root2, getContext(), uiThread, currentAbilityIDs).show();
                                                                break;
                                                            case "KRYS":
                                                                ArrayList<String> frees = new ArrayList<>();
                                                                frees.add(command.split(",")[1]);
                                                                frees.add(command.split(",")[2]);
                                                                for (String command2 : frees){
                                                                    switch (command2){
                                                                        case "HÅNDVÆRK":
                                                                            popHandler.getCraftsAlert(root2, getContext(), uiThread, true).show();
                                                                            break;
                                                                        case "3EP":
                                                                            popHandler.get3EPChoiceAlert(root2, getContext(), uiThread, currentAbilityIDs, true).show();
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
                                                                    AlertDialog.Builder builder = popHandler.getStartChoiceAlert(root2, getContext(), uiThread);
                                                                    uiThread.post(() ->{
                                                                        builder.show();
                                                                    });
                                                                });
                                                                break;
                                                        }

                                                        //TODO: create more popups
                                                    }
                                                    skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());
                                                    raceAbilityAdapter.notifyDataSetChanged();
                                                    skillViewModel.getUpdate().postValue(true);
                                                    ArrayList<Integer> ls = new ArrayList<>();
                                                    for (AbilityDTO dto : charRepo.getCurrentAbilitiesList()){
                                                        ls.add(dto.getId());
                                                    }
                                                    skillViewModel.getCurrentAbilityIDs().postValue(ls);
                                                    raceAbilityAdapter.notifyDataSetChanged();
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

    private class OtherAbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (otherAbilityList != null) return otherAbilityList.size();
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
            vh.name.setText(otherAbilityList.get(position).getName());
            vh.cost.setText(otherAbilityList.get(position).getCost() + "");
            if (position % 2 == (otherAbilityList.size() + 1) % 2) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
            vh.buybtn.setVisibility(View.GONE);
            vh.checkimg.setVisibility(View.VISIBLE);

        }
    }


}