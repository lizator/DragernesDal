package com.rbyte.dragernesdal.ui.character.select;


import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.ui.home.HomeFragment;
import com.rbyte.dragernesdal.ui.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SelectFragment extends Fragment{

    private SelectViewModel selectViewModel;
    private ArrayList<CharacterDTO> characterList;
    private CharacterAdapter characterAdapter;
    private RecyclerView recyclerView;
    private NavController navController;
    private View root2;
    public static final String CHARACTER_ID_ARGUMENT = "charIDArgument";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_character_select, container, false);
        characterList = new ArrayList<CharacterDTO>();
        selectViewModel = SelectViewModel.getInstance();
        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        int userID = prefs.getInt(MainActivity.USER_ID_SAVESPACE, -1);
        Log.i("SelectFrag", "UserID Found: " + userID);
        selectViewModel.startGetThread(userID);
        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.nav_chooseRaceFragment);
            }
        });
        //Finding recyclerview to input abilities
        recyclerView = (RecyclerView) root.findViewById(R.id.charRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        characterAdapter = new CharacterAdapter();
        characterAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(characterAdapter);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in SelectFragment");
                navController = Navigation.findNavController(root);
                navController.popBackStack(R.id.nav_home,false);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        selectViewModel.getCharacters().observe(getViewLifecycleOwner(), new Observer<List<CharacterDTO>>() {
            @Override
            public void onChanged(List<CharacterDTO> characterDTOS) {
                characterList.clear();
                characterList = (ArrayList<CharacterDTO>) characterDTOS;
                characterAdapter.notifyDataSetChanged();
            }
        });

        root2 = root;
        return root;
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView name;
        ImageView img;
        public CharacterViewHolder(View charViews) {
            super(charViews);
            cardView = charViews.findViewById(R.id.card_view);
            name = charViews.findViewById(R.id.characterName);
            img = charViews.findViewById(R.id.characterRecyclerImageView);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            charViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Log.i("test", "Running");
                    final int position = getAdapterPosition(); // listeelementets position
                    SharedPreferences prefs = root2.getContext().getSharedPreferences(getString(R.string.mainPrefFile), root2.getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(HomeFragment.CHARACTER_ID_SAVESPACE, characterList.get(position).getIdcharacter());
                    editor.commit();
                    navController = Navigation.findNavController(root2);
                    navController.popBackStack(R.id.nav_home,false);
                }
            });

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
            int raceID = characterList.get(position).getIdrace();
            switch (raceID) {
                case 1:
                    vh.img.setImageResource(R.drawable.rac_dvaerg);
                    break;
                case 2:
                    vh.img.setImageResource(R.drawable.rac_elver);
                    break;
                case 3:
                    vh.img.setImageResource(R.drawable.rac_gobliner);
                    break;
                case 4:
                    vh.img.setImageResource(R.drawable.rac_granitaner);
                    break;
                case 5:
                    vh.img.setImageResource(R.drawable.rac_havfolk);
                    break;
                case 6:
                    vh.img.setImageResource(R.drawable.rac_krysling);
                    break;
                case 7:
                    vh.img.setImageResource(R.drawable.rac_menneske);
                    break;
                case 8:
                    vh.img.setImageResource(R.drawable.rac_moerkskabt);
                    break;
                case 9:
                    vh.img.setImageResource(R.drawable.rac_orker);
                    break;
                case 10:
                    vh.img.setImageResource(R.drawable.rac_sortelver);
                    break;
                default:
                    vh.img.setImageResource(R.drawable.rac_menneske);
                    break;
            } //Switch for setting image resource

        }

    }
}