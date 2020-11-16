package com.example.dragernesdal.ui.character.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.character.model.CharacterDTO;

import java.util.ArrayList;

public class SelectFragment extends Fragment {

    private SelectViewModel selectViewModel;
    private CharacterAdapter characterAdapter = new CharacterAdapter();
    private ArrayList<CharacterDTO> characterList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        selectViewModel =
                new ViewModelProvider(this).get(SelectViewModel.class);
        View root = inflater.inflate(R.layout.fragment_character_select, container, false);
        RecyclerView recyclerView;
        //Finding recyclerview to input abilities
        recyclerView = (RecyclerView) root.findViewById(R.id.charRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(characterAdapter);
        characterList.add(new CharacterDTO(0,1,"Legolas",1,20));
        characterList.add(new CharacterDTO(1,1,"Illidan",1,21));
        characterList.add(new CharacterDTO(2,1,"Legolas Illidan",1,20));
        characterList.add(new CharacterDTO(3,1,"Illidan legolas",1,25));
        characterList.add(new CharacterDTO(4,1,"Illidian Illidan",1,20));
        characterList.add(new CharacterDTO(5,1,"Legolas Legolas",1,21));
        return root;
    }


    class CharacterViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public CharacterViewHolder(View charViews) {
            super(charViews);
            name = charViews.findViewById(R.id.characterName);
            System.out.println(name);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    class CharacterAdapter extends RecyclerView.Adapter<SelectFragment.CharacterViewHolder> {
        @Override
        public int getItemCount() {
            return characterList.size();
        }

        @Override
        public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_character_list_view, parent, false);
            CharacterViewHolder vh = new CharacterViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(CharacterViewHolder vh, int position) {
            vh.name.setText(characterList.get(position).getName());
            //TODO set onclick to show abilityList.get(position).getDesc()

        }

    }
}