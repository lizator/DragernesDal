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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.rbyte.dragernesdal.data.inventory.InventoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class CheckOutFragment extends Fragment {
    public static final String EVENT_ID_ARGUMENT = "eventIDArgument";
    private static final String EVENT_SELECTED_NAME = "eventName";
    private int eventID;
    private View root;
    private CharacterAdapter characterAdapter;
    private ArrayList<CharacterDTO> characterList;
    private RecyclerView recyclerView;
    private CheckInViewModel checkInViewModel;
    private FloatingActionButton fab, fabDeny;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_checkout, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        characterList = new ArrayList<CharacterDTO>();
        fab = root.findViewById(R.id.checkedIn);
        fabDeny = root.findViewById(R.id.denyAll);
        eventID = prefs.getInt(EVENT_ID_ARGUMENT, -1);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Check ud: " + prefs.getString(EVENT_SELECTED_NAME, ""));
        checkInViewModel = CheckInViewModel.getInstance();
        checkInViewModel.startGetThread(eventID, 1);
        fabDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Vil du afvise alle inventories?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(()->{
                            InventoryRepository inventoryRepository = InventoryRepository.getInstance();
                            inventoryRepository.denyAll();
                        });
                        Toast.makeText(getContext(),"Alle inventories afvist",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Vil du give alle: \"+1 EP?\"");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        checkInViewModel.startEPThread(eventID);
                        for(CharacterDTO character:characterList){
                            character.setCurrentep(character.getCurrentep()+1);
                        }
                        characterAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(),"1 EP tilføjet til alle!",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Nej", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress", "Back pressed in check out");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        recyclerView = (RecyclerView) root.findViewById(R.id.charRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        characterAdapter = new CharacterAdapter();
        characterAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(characterAdapter);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        checkInViewModel.getCharacters().observe(getViewLifecycleOwner(), new Observer<List<CharacterDTO>>() {
            @Override
            public void onChanged(List<CharacterDTO> characterDTOS) {
                characterList.clear();
                characterList = (ArrayList<CharacterDTO>) characterDTOS;
                characterAdapter.notifyDataSetChanged();
            }
        });
        this.root = root;
        return root;
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder {
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
                public void onClick(View v) {
                    Log.i("test", "Running");
                    final int position = getAdapterPosition(); // listeelementets position
                    InventoryCheckout inventoryCheckout = new InventoryCheckout(getContext(), root, getCharacter(position), getViewLifecycleOwner());
                    //inventoryCheckout.showInventory();

                }
            });
        }

        private CharacterDTO getCharacter(int position){
            return characterList.get(position);
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
            vh.name.setText(characterList.get(position).getName() + "\nBrugerID: " + characterList.get(position).getIduser()+"\nEP: "+ characterList.get(position).getCurrentep());
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
