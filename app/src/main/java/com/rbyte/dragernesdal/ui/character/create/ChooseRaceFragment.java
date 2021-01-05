package com.rbyte.dragernesdal.ui.character.create;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;

import java.util.ArrayList;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ChooseRaceFragment extends Fragment {

    private RaceAdapter raceAdapter = new RaceAdapter();
    private ArrayList<RaceChoiceCard> raceList = new ArrayList<>();
    private View root;
    public static final String RACE_ID_SAVESPACE = "chosenRaceID";
    AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        raceList.add(new RaceChoiceCard("Dværg", R.drawable.rac_dvaerg, 1));
        raceList.add(new RaceChoiceCard("Elver", R.drawable.rac_elver, 2));
        raceList.add(new RaceChoiceCard("Gobliner", R.drawable.rac_gobliner, 3));
        raceList.add(new RaceChoiceCard("Granitaner", R.drawable.rac_granitaner, 4));
        raceList.add(new RaceChoiceCard("Havfolk", R.drawable.rac_havfolk, 5));
        raceList.add(new RaceChoiceCard("Krysling", R.drawable.rac_krysling, 6));
        raceList.add(new RaceChoiceCard("Menneske", R.drawable.rac_menneske, 7));
        raceList.add(new RaceChoiceCard("Mørkskabt", R.drawable.rac_moerkskabt, 8));
        raceList.add(new RaceChoiceCard("Orker", R.drawable.rac_orker, 9));
        raceList.add(new RaceChoiceCard("Sortelver", R.drawable.rac_sortelver, 10));
        root = inflater.inflate(R.layout.fragment_character_chooserace, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.raceChoiceRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), 2));
        recyclerView.setAdapter(raceAdapter);
        raceAdapter.notifyDataSetChanged();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.selectRace);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in ChooseRaceFragment");
                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.nav_char_select);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return root;
    }

    class RaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardViewLeft;
        ImageView raceImgLeft;
        TextView raceNameTVLeft;

        public RaceViewHolder(View charViews) {
            super(charViews);
            cardViewLeft = (CardView) charViews.findViewById(R.id.card_view_left);
            raceImgLeft = (ImageView) charViews.findViewById(R.id.raceChoiceImg);
            raceNameTVLeft = (TextView) charViews.findViewById(R.id.raceChoiceTV);

            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            cardViewLeft.setBackgroundResource(android.R.drawable.list_selector_background);
            cardViewLeft.setOnClickListener(this);

            builder = new AlertDialog.Builder(root.getContext());

        }

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View v){

            SharedPreferences prefs = getDefaultSharedPreferences(root.getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(RACE_ID_SAVESPACE, raceList.get(getAdapterPosition()).getRaceID());
            editor.commit();

            builder.setTitle("Vil du vælge denne seje race");
            View viewInflated = LayoutInflater.from(root.getContext()).inflate(R.layout.alert_race_info, (ViewGroup)root.getRootView(),false);
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            input.setText("Elvere er nogle grimmerter");
            builder.setView(viewInflated);

            int raceID = prefs.getInt(ChooseRaceFragment.RACE_ID_SAVESPACE, 1);



            builder.setPositiveButton("Vælg!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            NavController navController = Navigation.findNavController(root);
                            navController.navigate(R.id.nav_chooseRaceFragment);

                        }
                    });

            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }

            });
            builder.show();



        }

    }

    class RaceAdapter extends RecyclerView.Adapter<RaceViewHolder> {
        @Override
        public int getItemCount() {
            return raceList.size();
        }

        @Override
        public RaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_race_choice_single, parent, false);
            RaceViewHolder vh = new RaceViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(RaceViewHolder vh, int position) {
            vh.raceImgLeft.setImageResource(raceList.get(position).getLeftPicResID());
            vh.raceNameTVLeft.setText(raceList.get(position).getLeftRaceName());
        }

    }

    private class RaceChoiceCard {
        private String leftRaceName;
        private int leftPicResID;
        private int raceID;

        public RaceChoiceCard(String leftRaceName, int leftPicResID, int raceID) {
            this.leftRaceName = leftRaceName;
            this.leftPicResID = leftPicResID;
            this.raceID = raceID;
        }

        public String getLeftRaceName() {
            return leftRaceName;
        }

        public void setLeftRaceName(String leftRaceName) {
            this.leftRaceName = leftRaceName;
        }

        public int getLeftPicResID() {
            return leftPicResID;
        }

        public void setLeftPicResID(int leftPicResID) {
            this.leftPicResID = leftPicResID;
        }

        public int getRaceID() {
            return raceID;
        }

        public void setRaceID(int raceID) {
            this.raceID = raceID;
        }
    }
}