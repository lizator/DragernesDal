package com.rbyte.dragernesdal.ui.admin.skill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;
import com.rbyte.dragernesdal.ui.character.skill.alle.AlleFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditSkillFragment extends Fragment {
    private RecyclerView recyclerView;
    private SkillViewModel skillViewModel = SkillViewModel.getInstance();
    private ArrayList<AbilityDTO> abilityList = new ArrayList<>();
    private AbilityRepository abilityrepo;
    private AbilityAdapter abilityAdapter = new AbilityAdapter();
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_skill_admin, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        abilityrepo = AbilityRepository.getInstance();
        recyclerView = (RecyclerView) root.findViewById(R.id.adminRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(abilityAdapter);
        if(abilityList.size()==0)skillViewModel.getAll();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress", "Back pressed in edit skill");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };


        skillViewModel.getAllAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                abilityList.clear();
                abilityList.addAll(abilityDTOS);
                Collections.sort(abilityList, new Comparator<AbilityDTO>() {
                    public int compare(AbilityDTO left, AbilityDTO right)  {
                        return left.getName().compareTo(right.getName()); // The order depends on the direction of sorting.
                    }
                });
                abilityAdapter.notifyDataSetChanged();
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }


    private class AbilityViewHolder extends RecyclerView.ViewHolder {
        TextView name, cost;
        ImageView checkimg;
        View view;

        public AbilityViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.lineName);
            cost = abilityViews.findViewById(R.id.abilityCostTv);
            checkimg = abilityViews.findViewById(R.id.checkImage);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    private class AbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        private EditText skillType, skillName, ep, description;
        private Spinner spinner;
        private Button create;
        private ArrayAdapter<String> typeAdapter;
        private ArrayList<String> types = new ArrayList<>();
        private boolean nameB = false, epB = false, descripB = false, skillB = false, customSkill = false;


        private boolean isValid(){
            if(!skillType.isEnabled()) return (nameB && epB && descripB);
            else return (nameB && epB && descripB && skillB);
        }

        @Override
        public int getItemCount() {
            if (abilityList != null) return abilityList.size();
            return 0;
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_skill_line_admin, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(abilityList.get(position).getName());
            vh.cost.setText(abilityList.get(position).getCost()+"");
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked on:" + abilityList.get(position).getName());
                    AbilityDTO dto;
                    dto = abilityList.get(position);
                    AlertDialog.Builder dialog = dialog(getContext(), dto);
                    final AlertDialog d = dialog.show();
                    editSkill(dto, d);
                }
            });

        }

        private void editSkill(AbilityDTO abilityDTO, AlertDialog dialog){
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abilityDTO.setCost(Integer.parseInt(ep.getText()+""));
                    abilityDTO.setName(skillName.getText()+"");
                    abilityDTO.setDesc(description.getText()+"");
                    abilityDTO.setType(!customSkill ? spinner.getSelectedItem()+"" : skillType.getText()+"");
                    skillViewModel.updateAbility(abilityDTO);
                    Toast.makeText(getContext(),"Evne rettet",Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(root);
                    navController.popBackStack();
                    dialog.dismiss();
                }
            });
        }

        private void initElements(View view) {
            skillType = view.findViewById(R.id.SkillType);
            skillName = view.findViewById(R.id.editName);
            spinner = view.findViewById(R.id.spinnerType);
            ep = view.findViewById(R.id.EPCost);
            description = view.findViewById(R.id.skilldescription);
            create = view.findViewById(R.id.create);
            skillType.setEnabled(false);
            skillType.setVisibility(View.INVISIBLE);
        }

        private void initElementData(AbilityDTO dto){
            skillType.setText(dto.getType());
            skillName.setText(dto.getName());
            ep.setText(dto.getCost()+"");
            description.setText(dto.getDesc());
            create.setText("Rediger evne");
        }

        private AlertDialog.Builder dialog(Context context, AbilityDTO dto){
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(root.getContext()).inflate(R.layout.fragment_admin_create_skill, (ViewGroup)root.getRootView(),false);
            alert.setView(view);
            setup(view, dto, context);
            alert.setTitle("Rediger: "+dto.getName());
            return alert;
        }

        private void setup(View view, AbilityDTO dto, Context context){
            initElements(view);
            initElementData(dto);
            setSpinner(context);
            loadSpinnerData(dto);
            initTextListeners();
            initElementData(dto);
        }



        private void initTextListeners(){
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
        }

        private void setSpinner(Context context){
            typeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, types);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(typeAdapter);

        }

        private void loadSpinnerData(AbilityDTO dto){
            skillViewModel.getTypes();
            skillViewModel.getAbilityTypes().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
                @Override
                public void onChanged(ArrayList<String> strings) {
                    types.clear();
                    types.addAll(strings);
                    types.add("Andet");
                    typeAdapter.notifyDataSetChanged();
                    for(int i = 0; i < types.size(); i++){
                        if(types.get(i).equals(dto.getType())) spinner.setSelection(i);
                    }
                }
            });
            initSpinnerOnSelect();
        }

        private void initSpinnerOnSelect(){
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
        }
    }




}
