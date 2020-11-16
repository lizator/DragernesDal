package com.example.dragernesdal.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.ability.model.Ability;
import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.ui.character.select.SelectFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private ArrayList<Ability> abilityList = new ArrayList<Ability>();
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
            //TODO send to create character activity
            SelectFragment nextFrag= new SelectFragment();
            System.out.println(root.getId());
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup)root).getId(), nextFrag, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
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

        homeViewModel.getCharacter().observe(getViewLifecycleOwner(), new Observer<CharacterDTO>() {
            @Override
            public void onChanged(CharacterDTO character) {
                EditText characterNameEdit = (EditText) root.findViewById(R.id.characterNameEdit);
                TextView raceTV = (TextView) root.findViewById(R.id.raceTV);
                EditText yearEdit = (EditText) root.findViewById(R.id.yearEdit);
                TextView strengthTV = (TextView) root.findViewById(R.id.strengthTV); //Insert J, JJ, JJJ, JJJJ, JJJJJ
                TextView kpTV = (TextView) root.findViewById(R.id.kpTV); //Insert A, AA, AAA, AAA\nA, AAA\nAA
                TextView kobberTV = (TextView) root.findViewById(R.id.kobberTV);
                TextView silverTV = (TextView) root.findViewById(R.id.silverTV);
                TextView goldTV = (TextView) root.findViewById(R.id.goldTV);

                characterNameEdit.setText(character.getName());
                raceTV.setText(character.getRaceName());
                yearEdit.setText(String.valueOf(character.getAge()));
                String strength = "";
                for (int i = 0; i < character.getStrength(); i++) strength += "J";
                strengthTV.setText(strength);
                String kp = "";
                for (int i = 0; i < character.getHealth(); i++){
                    if(i == 4) kp += "\n";
                    kp += "A";
                }
                kpTV.setText(kp);

                //TODO change picture from where its saved
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