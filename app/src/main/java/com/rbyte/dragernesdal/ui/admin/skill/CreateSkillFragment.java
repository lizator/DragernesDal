package com.rbyte.dragernesdal.ui.admin.skill;

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
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;

import java.util.ArrayList;

public class CreateSkillFragment extends Fragment {
    private EditText skillType, skillName, ep, description;
    private Spinner spinner;
    private Button create;
    private ArrayAdapter<String> typeAdapter;
    private ArrayList<String> types = new ArrayList<>();
    private SkillViewModel skillViewModel;
    private boolean nameB = false, epB = false, descripB = false, skillB = false, customSkill = false;

    private boolean isValid(){
        if(!skillType.isEnabled()) return (nameB && epB && descripB);
        else return (nameB && epB && descripB && skillB);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_create_skill, container, false);
        skillType = root.findViewById(R.id.SkillType);
        skillName = root.findViewById(R.id.editName);
        spinner = root.findViewById(R.id.spinnerType);
        ep = root.findViewById(R.id.EPCost);
        description = root.findViewById(R.id.skilldescription);
        create = root.findViewById(R.id.create);
        skillType.setEnabled(false);
        skillType.setVisibility(View.INVISIBLE);
        typeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);
        skillViewModel = SkillViewModel.getInstance();
        skillViewModel.getTypes();
        create.setEnabled(false);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbilityDTO abilityDTO = new AbilityDTO();
                abilityDTO.setCost(Integer.parseInt(ep.getText()+""));
                abilityDTO.setName(skillName.getText()+"");
                abilityDTO.setDesc(description.getText()+"");
                abilityDTO.setType(!customSkill ? spinner.getSelectedItem()+"" : skillType.getText()+"");
                skillViewModel.createAbility(abilityDTO);
                Toast.makeText(getContext(),"Evne oprettet",Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(types.get(position).equals("Andet")){
                    skillType.setEnabled(true);
                    skillType.setVisibility(View.VISIBLE);
                    skillType.setText("");
                    customSkill = true;
                } else{
                    skillType.setEnabled(false);
                    skillType.setVisibility(View.INVISIBLE);
                    customSkill = false;
                }
                create.setEnabled(isValid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        skillType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) skillB = true;
                else skillB = false;
                create.setEnabled(isValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        skillName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) nameB = true;
                else nameB = false;
                create.setEnabled(isValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) descripB = true;
                else descripB = false;
                create.setEnabled(isValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) epB = true;
                else epB = false;
                create.setEnabled(isValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        skillViewModel.getAbilityTypes().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                types.clear();
                types.addAll(strings);
                types.add("Andet");
                typeAdapter.notifyDataSetChanged();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in create skill");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }
}
