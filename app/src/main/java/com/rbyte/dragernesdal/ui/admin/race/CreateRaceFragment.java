package com.rbyte.dragernesdal.ui.admin.race;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;

import java.util.ArrayList;

public class CreateRaceFragment extends Fragment {
    private Spinner startSpin, ep2Spin, ep3Spin, ep4Spin;
    private EditText raceName;
    private Button create;
    private ArrayList<AbilityDTO> ep0, ep2, ep3, ep4;
    private ArrayList<String> ep0name, ep2name, ep3name, ep4name;
    private SkillViewModel skillViewModel;
    private RaceViewModel raceViewModel;
    private ArrayAdapter<String> ep0adapter,ep2adapter,ep3adapter,ep4adapter;
    private RaceDTO raceDTO = new RaceDTO();
    private boolean textB = false,startB = false,ep2B = false,ep3B = false,ep4B = false;

    private boolean isValid(){
        if(textB && startB && ep2B && ep3B && ep4B) return true;
        return false;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_create_race, container, false);
        skillViewModel = SkillViewModel.getInstance();
        raceViewModel = RaceViewModel.getInstance();
        startSpin = root.findViewById(R.id.startAbility);
        ep2Spin = root.findViewById(R.id.ep2Ability);
        ep3Spin = root.findViewById(R.id.ep3Ability);
        ep4Spin = root.findViewById(R.id.ep4Ability);
        create = root.findViewById(R.id.createRace);
        raceName = root.findViewById(R.id.editText_raceName);
        ep0 = new ArrayList<>();
        ep2 = new ArrayList<>();
        ep3 = new ArrayList<>();
        ep4 = new ArrayList<>();
        ep0name = new ArrayList<>();
        ep2name = new ArrayList<>();
        ep3name = new ArrayList<>();
        ep4name = new ArrayList<>();
        create.setEnabled(false);
        raceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count <= 0) textB = false;
                else textB = true;
                create.setEnabled(isValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        ep0adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep0name);
        ep0adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpin.setAdapter(ep0adapter);

        ep2adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep2name);
        ep2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ep2Spin.setAdapter(ep2adapter);

        ep3adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep3name);
        ep3adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ep3Spin.setAdapter(ep3adapter);

        ep4adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep4name);
        ep4adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ep4Spin.setAdapter(ep4adapter);
        skillViewModel.getUncommonAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                ep0.clear();
                ep2.clear();
                ep3.clear();
                ep4.clear();
                ep0name.clear();
                ep2name.clear();
                ep3name.clear();
                ep4name.clear();
                for (AbilityDTO dtos : abilityDTOS) {
                    switch (dtos.getCost()) {
                        case 0:
                            ep0.add(dtos);
                            ep0name.add(dtos.getName());
                            break;
                        case 2:
                            ep2.add(dtos);
                            ep2name.add(dtos.getName());
                            break;
                        case 3:
                            ep3.add(dtos);
                            ep3name.add(dtos.getName());
                            break;
                        case 4:
                            ep4.add(dtos);
                            ep4name.add(dtos.getName());
                            break;
                        default:
                            break;
                    }
                }
                ep0adapter.notifyDataSetChanged();
                ep2adapter.notifyDataSetChanged();
                ep3adapter.notifyDataSetChanged();
                ep4adapter.notifyDataSetChanged();
            }
        });
        skillViewModel.getUncommonAbilities();

        startSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                raceDTO.setStart(ep0.get(position).getId());
                startB = true;
                create.setEnabled(isValid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ep2Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                raceDTO.setEp2(ep2.get(position).getId());
                ep2B = true;
                create.setEnabled(isValid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ep3Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                raceDTO.setEp3(ep3.get(position).getId());
                ep3B = true;
                create.setEnabled(isValid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ep4Spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                raceDTO.setEp4(ep4.get(position).getId());
                ep4B = true;
                create.setEnabled(isValid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raceDTO.setRacename(raceName.getText().toString());
                raceViewModel.createRace(raceDTO);
                Toast.makeText(getContext(),"Race oprettet",Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress", "Back pressed in create race");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }
}
