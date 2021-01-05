package com.rbyte.dragernesdal.ui.character.skill.kamp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class KampFragment extends Fragment {

    private KampViewModel kampViewModel = new KampViewModel();
    private AbilityRepository abilityrepo;
    private CharacterRepository charRepo;
    private ArrayList<AbilityDTO> abilityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private ArrayList<Integer> currentAbilityIDs = new ArrayList<>();
    private Handler uiThread = new Handler();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_skill_kamp, container, false);
        abilityrepo = AbilityRepository.getInstance();
        charRepo = CharacterRepository.getInstance();

        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        int currCharacterID = prefs.getInt(HomeFragment.CHARACTER_ID_SAVESPACE, -1);
        if (currCharacterID != -1) kampViewModel.startGetThread(0); //TO-DO: error handle (not ever likely to happen, nvm)

        recyclerView = (RecyclerView) root.findViewById(R.id.kampRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);

        for (AbilityDTO dto : charRepo.getCurrentAbilitiesList()){
            currentAbilityIDs.add(dto.getId());
        }

        kampViewModel.getCurrAbilitys().observe(getViewLifecycleOwner(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> integers) {
                currentAbilityIDs = integers;
                abilityAdapter.notifyDataSetChanged();
            }
        });

        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() ->{
            Result<List<AbilityDTO>> res = abilityrepo.getTypeAbilities("kamp");
            uiThread.post(() -> {
                if (res instanceof Result.Success){
                    abilityList = ((Result.Success<ArrayList<AbilityDTO>>) res).getData();
                    abilityAdapter.notifyDataSetChanged();
                }
            });

        });

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

    class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> { //TODO make use onclick
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
                int currEP = charRepo.getCurrentChar().getCurrentep();
                if (abilityList.get(position).getCost() <= currEP){
                    vh.buybtn.setClickable(true);
                    vh.buybtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: make popup, buy ability and refresh
                        }
                    });
                }
            }

        }
    }


}