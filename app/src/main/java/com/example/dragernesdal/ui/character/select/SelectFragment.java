package com.example.dragernesdal.ui.character.select;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.ability.model.AbilityDTO;
import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.ui.character.create.ChooseRaceFragment;
import com.example.dragernesdal.ui.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SelectFragment extends Fragment implements View.OnClickListener {

    private SelectViewModel selectViewModel;
    private ArrayList<CharacterDTO> characterList = new ArrayList<CharacterDTO>();
    private CharacterAdapter characterAdapter = new CharacterAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_character_select, container, false);
        RecyclerView recyclerView;
        selectViewModel = SelectViewModel.getInstance();
        this.characterList = new ArrayList<CharacterDTO>();
        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        int userID = prefs.getInt(MainActivity.USER_ID_SAVESPACE, -1);
        Log.i("SelectFrag", "UserID Found: " + userID);
        selectViewModel.startGetThread(userID);
        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
        //Finding recyclerview to input abilities
        recyclerView = (RecyclerView) root.findViewById(R.id.charRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(characterAdapter);

        selectViewModel.getCharacters().observe(getViewLifecycleOwner(), new Observer<List<CharacterDTO>>() {
            @Override
            public void onChanged(List<CharacterDTO> characterDTOS) {
                characterList.clear();
                characterList = (ArrayList<CharacterDTO>) characterDTOS;
                characterAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
        Fragment mFragment = new ChooseRaceFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, mFragment).commit();
    }


    class CharacterViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView name;
        public CharacterViewHolder(View charViews) {
            super(charViews);
            cardView = charViews.findViewById(R.id.card_view);
            name = charViews.findViewById(R.id.characterName);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    class CharacterAdapter extends RecyclerView.Adapter<CharacterViewHolder> {
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
            System.out.println(characterList.get(position).getName());

        }

    }
}