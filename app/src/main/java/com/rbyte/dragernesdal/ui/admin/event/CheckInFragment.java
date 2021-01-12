package com.rbyte.dragernesdal.ui.admin.event;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.event.model.CheckInDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.select.SelectViewModel;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.home.HomeFragment;
import com.rbyte.dragernesdal.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CheckInFragment extends Fragment {
    private CheckInViewModel checkInViewModel;
    private ArrayList<CharacterDTO> characterList;
    private CharacterAdapter characterAdapter;
    private RecyclerView recyclerView;
    private NavController navController;
    private View root2;
    public static final String EVENT_ID_ARGUMENT = "eventIDArgument";
    private static final String EVENT_SELECTED_NAME = "eventName";
    private int checkin;
    private int eventID;
    private FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_checkin, container, false);
        characterList = new ArrayList<CharacterDTO>();
        checkInViewModel = CheckInViewModel.getInstance();
        checkin = 0;
        fab = root.findViewById(R.id.checkedIn);
        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        eventID = prefs.getInt(EVENT_ID_ARGUMENT, -1);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Check in: "+prefs.getString(EVENT_SELECTED_NAME, ""));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkin == 1){
                    checkin = 0;
                    checkInViewModel.startGetThread(eventID, checkin);
                    characterAdapter.notifyDataSetChanged();
                    fab.setImageResource(R.drawable.ic_baseline_save_alt_24);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Check in: "+prefs.getString(EVENT_SELECTED_NAME, ""));
                } else {
                    checkin = 1;
                    checkInViewModel.startGetThread(eventID, checkin);
                    characterAdapter.notifyDataSetChanged();
                    fab.setImageResource(R.drawable.ic_plus_menu);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Checked in: "+prefs.getString(EVENT_SELECTED_NAME, ""));
                }
            }
        });
        Log.i("CheckInFrag", "EventID Found: " + eventID);
        checkInViewModel.startGetThread(eventID, checkin);
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
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        checkInViewModel.getCharacters().observe(getViewLifecycleOwner(), new Observer<List<CharacterDTO>>() {
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
            name = charViews.findViewById(R.id.editText_Title);
            img = charViews.findViewById(R.id.characterRecyclerImageView);
            /*// Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);*/
            charViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Log.i("test", "Running");
                    final int position = getAdapterPosition(); // listeelementets position
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    if(checkin == 0) alertDialog.setTitle(getResources().getString(R.string.checkin)+" "+ characterList.get(position).getName());
                    else alertDialog.setTitle(getResources().getString(R.string.checkout)+" "+ characterList.get(position).getName());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            CheckInDTO dto = new CheckInDTO();
                            if(checkin == 0) dto.setCheckin(1);
                            else dto.setCheckin(0);
                            dto.setIdEvent(eventID);
                            dto.setIdChar(characterList.get(position).getIdcharacter());
                            checkInViewModel.startSetThread(dto);
                            if(checkin == 1) Toast.makeText(getContext(),characterList.get(position).getName()+": checked ud",Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getContext(),characterList.get(position).getName()+": checked ind",Toast.LENGTH_SHORT).show();
                            characterList.remove(position);
                            characterAdapter.notifyDataSetChanged();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Annuller", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.show();
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
            vh.name.setSingleLine(false);
            vh.name.setText(characterList.get(position).getName()+"\nBrugerID: "+characterList.get(position).getIduser());
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
