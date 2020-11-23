package com.example.dragernesdal.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.ability.model.AbilityDTO;
import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.data.inventory.model.InventoryDTO;
import com.example.dragernesdal.ui.character.select.SelectFragment;
import com.example.dragernesdal.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private ArrayList<AbilityDTO> abilityList = new ArrayList<AbilityDTO>();
    private RecyclerView recyclerView;

    public static final String CHARACTER_ID_SAVESPACE = "currCharacterID";
    //TODO maybe make some animation thing for when logging to to have data loaded and setup made?

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        //Start testing
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CHARACTER_ID_SAVESPACE, 2);
        editor.commit();
        //End testing
        int characterID = prefs.getInt(CHARACTER_ID_SAVESPACE, -1);
        if (characterID == -1){
            Fragment mFragment = new SelectFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, mFragment).commit();
        } else {
            homeViewModel = HomeViewModel.getInstance();
            homeViewModel.startGetThread(characterID);


            //Finding recyclerview to input abilities
            ImageView imgView = (ImageView) root.findViewById(R.id.characterPicView);
            recyclerView = (RecyclerView) root.findViewById(R.id.abilityRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            recyclerView.setAdapter(abilityAdapter);

            root.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    ViewGroup.LayoutParams paramsImg = imgView.getLayoutParams();
                    int h = (int) Math.floor(root.getMeasuredHeight() * 5 / 9 - 200);
                    int w = (int) Math.floor(root.getMeasuredWidth() * 5 / 12 + 15);
                    params.height = h;
                    params.width = w;
                    paramsImg.height = h;
                    paramsImg.width = w;
                    recyclerView.setLayoutParams(params);
                    imgView.setLayoutParams(paramsImg);
                    imgView.setImageResource(R.drawable.rac_menneske);
                }
            });

            homeViewModel.getCharacter().observe(getViewLifecycleOwner(), new Observer<CharacterDTO>() {
                @Override
                public void onChanged(CharacterDTO character) {
                    EditText characterNameEdit = (EditText) root.findViewById(R.id.characterNameEdit);
                    TextView raceTV = (TextView) root.findViewById(R.id.raceTV);
                    EditText yearEdit = (EditText) root.findViewById(R.id.yearEdit);
                    TextView strengthTV = (TextView) root.findViewById(R.id.strengthTV); //Insert J, JJ, JJJ, JJJJ, JJJJJ
                    TextView kpTV = (TextView) root.findViewById(R.id.kpTV); //Insert A, AA, AAA, AAA\nA, AAA\nAA

                    characterNameEdit.setText(character.getName());
                    raceTV.setText(character.getRaceName());
                    yearEdit.setText(String.valueOf(character.getAge()));
                    String strength = "";
                    for (int i = 0; i < character.getStrength(); i++) strength += "J";
                    strengthTV.setText(strength);
                    String kp = "";
                    for (int i = 0; i < character.getHealth(); i++) {
                        if (i == 4) kp += "\n";
                        kp += "A";
                    }
                    kpTV.setText(kp);

                    //TODO change picture from where its saved
                }
            });

            homeViewModel.getAbilities().observe(getViewLifecycleOwner(), new Observer<List<AbilityDTO>>() {
                @Override
                public void onChanged(List<AbilityDTO> abilityDTOS) {
                    abilityList.clear();
                    abilityList = (ArrayList<AbilityDTO>) abilityDTOS;
                    abilityAdapter.notifyDataSetChanged();
                }
            });

            homeViewModel.getMoney().observe(getViewLifecycleOwner(), new Observer<List<InventoryDTO>>() {
                @Override
                public void onChanged(List<InventoryDTO> inventoryDTOS) {
                    ArrayList<InventoryDTO> moneyList = (ArrayList<InventoryDTO>) inventoryDTOS;
                    TextView goldTV = (TextView) root.findViewById(R.id.goldTV);
                    TextView silverTV = (TextView) root.findViewById(R.id.silverTV);
                    TextView kobberTV = (TextView) root.findViewById(R.id.kobberTV);

                    goldTV.setText(moneyList.get(0).getAmount() + "");
                    silverTV.setText(moneyList.get(1).getAmount() + "");
                    kobberTV.setText(moneyList.get(2).getAmount() + "");

                }
            });
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in HomeFragment");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Log ud?")
                        .setMessage("Er du sikker på at du vil logge ud?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                loginIntent.putExtra(getString(R.string.logout_command), true);
                                startActivity(loginIntent);
                                getActivity().finish();
                            }})
                        .setNegativeButton("Nej", null).show();
            }
        };requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


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

    class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> { //TODO make use onclick
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