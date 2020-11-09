package com.example.dragernesdal.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dragernesdal.R;
import com.example.dragernesdal.ui.home.HomeViewModel;
import com.example.dragernesdal.ui.main.MainActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private ArrayList<Ability> abilityList = new ArrayList<Ability>();
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Finding recyclerview to input abilities
        recyclerView = (RecyclerView) root.findViewById(R.id.abilityRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);

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


    class Ability{
        private String name;
        private String desc;
        public Ability(String name, String desc){
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}