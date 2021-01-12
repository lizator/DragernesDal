package com.rbyte.dragernesdal.ui.admin.race;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;

import java.util.ArrayList;

public class CreateRaceFragment extends Fragment {
    private Spinner startSpin, ep2Spin, ep3Spin, ep4Spin;
    private ArrayList<AbilityDTO> ep1, ep2, ep3, ep4;
    private ArrayList<String> ep1name, ep2name, ep3name, ep4name;
    private SkillViewModel skillViewModel;
    private ArrayAdapter<String> ep1adapter,ep2adapter,ep3adapter,ep4adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_create_race, container, false);
        skillViewModel = SkillViewModel.getInstance();
        startSpin = root.findViewById(R.id.startAbility);
        ep2Spin = root.findViewById(R.id.ep2Ability);
        ep3Spin = root.findViewById(R.id.ep3Ability);
        ep4Spin = root.findViewById(R.id.ep4Ability);
        ep1 = new ArrayList<>();
        ep2 = new ArrayList<>();
        ep3 = new ArrayList<>();
        ep4 = new ArrayList<>();
        ep1name = new ArrayList<>();
        ep2name = new ArrayList<>();
        ep3name = new ArrayList<>();
        ep4name = new ArrayList<>();

        ep1adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep1name);
        ep1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpin.setAdapter(ep1adapter);

        ep2adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep2name);
        ep2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ep2Spin.setAdapter(ep2adapter);

        ep3adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep3name);
        ep3adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ep3Spin.setAdapter(ep3adapter);

        ep4adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ep4name);
        ep4adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ep4Spin.setAdapter(ep4adapter);
        skillViewModel.getAlleAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                ep1.clear();
                ep2.clear();
                ep3.clear();
                ep4.clear();
                ep1name.clear();
                ep2name.clear();
                ep3name.clear();
                ep4name.clear();
                for (AbilityDTO dtos : abilityDTOS) {
                    System.out.println(dtos.getCost());
                    switch (dtos.getCost()) {
                        case 1:
                            ep1.add(dtos);
                            ep1name.add(dtos.getName());
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
                ep2adapter.notifyDataSetChanged();
            }
        });
        skillViewModel.updateAlle();
        skillViewModel.getAlleAbilities();


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
