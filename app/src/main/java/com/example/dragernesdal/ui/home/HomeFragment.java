package com.example.dragernesdal.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.ability.model.Ability;

import java.util.ArrayList;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private ArrayList<Ability> abilityList = new ArrayList<Ability>();
    private RecyclerView recyclerView;

    public static final String CHARACTER_ID_SAVESPACE = "currCharacterID";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        int characterID = prefs.getInt(CHARACTER_ID_SAVESPACE, -1);
        if (characterID == -1){
            //TODO send to create character activity
        } else {

        }

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Finding recyclerview to input abilities
        ImageView imgView = (ImageView) root.findViewById(R.id.characterPicView);
        recyclerView = (RecyclerView) root.findViewById(R.id.abilityRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);

        root.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
                ViewGroup.LayoutParams paramsImg=imgView.getLayoutParams();
                int h = (int) Math.floor(root.getMeasuredHeight() * 5 / 9 - 200);
                int w = (int) Math.floor(root.getMeasuredWidth() * 5 / 13 + 25);
                params.height=h;
                params.width=w;
                paramsImg.height=h;
                paramsImg.width=w;
                recyclerView.setLayoutParams(params);
                imgView.setLayoutParams(paramsImg);
                imgView.setImageResource(R.drawable.rac_menneske);
            }
        });

        //testing ability
        abilityList.add(new Ability("name1", "long asssss desc"));
        abilityList.add(new Ability("name2", "long asssss desc"));
        abilityList.add(new Ability("name3", "long asssss desc"));
        abilityList.add(new Ability("name1", "long asssss desc"));
        abilityList.add(new Ability("name2", "long asssss desc"));
        abilityList.add(new Ability("name3", "long asssss desc"));
        abilityList.add(new Ability("name1", "long asssss desc"));
        abilityList.add(new Ability("name2", "long asssss desc"));
        abilityList.add(new Ability("name3", "long asssss desc"));
        abilityList.add(new Ability("name1", "long asssss desc"));
        abilityList.add(new Ability("name2", "long asssss desc"));
        abilityList.add(new Ability("name3", "long asssss desc"));
        abilityList.add(new Ability("name1", "long asssss desc"));
        abilityList.add(new Ability("name2", "long asssss desc"));
        abilityList.add(new Ability("name3", "long asssss desc"));
        abilityAdapter.notifyDataSetChanged();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    class AbilityViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public AbilityViewHolder(View abilityViews) {
            super(abilityViews);
            name = abilityViews.findViewById(R.id.abilityName);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            return abilityList.size();
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_home_ability_view, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(abilityList.get(position).getName());
            //TODO set onclick to show abilityList.get(position).getDesc()

        }

    }

}