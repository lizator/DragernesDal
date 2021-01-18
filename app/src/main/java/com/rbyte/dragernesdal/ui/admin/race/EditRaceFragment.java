package com.rbyte.dragernesdal.ui.admin.race;

import android.app.AlertDialog;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;
import com.rbyte.dragernesdal.ui.character.create.ChooseRaceFragment;
import com.rbyte.dragernesdal.ui.character.skill.SkillViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class EditRaceFragment extends Fragment {
    private ArrayList<RaceChoiceCard> raceList = new ArrayList<>();
    private RaceAdapter raceAdapter = new RaceAdapter();
    private RaceViewModel raceViewModel;
    private ArrayList<RaceDTO> raceDTOList = new ArrayList<>();
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_admin_edit_race, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.raceChoiceRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), 2));
        recyclerView.setAdapter(raceAdapter);
        raceViewModel = RaceViewModel.getInstance();
        raceViewModel.startGetCustomThread();


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in edit race");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        raceViewModel.getRaces().observe(getViewLifecycleOwner(), new Observer<List<RaceDTO>>() {
            @Override
            public void onChanged(List<RaceDTO> raceDTOS) {
                raceList.clear();
                raceDTOList.clear();
                raceDTOS.forEach((n) -> {
                    raceList.add(new RaceChoiceCard(n.getRacename(), R.drawable.rac_menneske, n.getID()));
                    raceDTOList.add(n);
                });
                raceAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }
    class RaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardViewLeft;
        ImageView raceImgLeft;
        TextView raceNameTVLeft;
        private EditText raceName;
        private Spinner startSpin, ep2Spin, ep3Spin, ep4Spin;
        private ArrayList<AbilityDTO> ep0, ep2, ep3, ep4;
        private ArrayList<String> ep0name, ep2name, ep3name, ep4name;
        private boolean textB = true,startB = false,ep2B = false,ep3B = false,ep4B = false;
        private ArrayAdapter<String> ep0adapter,ep2adapter,ep3adapter,ep4adapter;
        private SkillViewModel skillViewModel;
        private RaceDTO raceDTO = new RaceDTO();
        private boolean isValid(){
            if(textB && startB && ep2B && ep3B && ep4B) return true;
            return false;
        }

        public RaceViewHolder(View charViews) {
            super(charViews);
            cardViewLeft = (CardView) charViews.findViewById(R.id.card_view_left);
            raceImgLeft = (ImageView) charViews.findViewById(R.id.raceChoiceImg);
            raceNameTVLeft = (TextView) charViews.findViewById(R.id.raceChoiceTV);

            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            cardViewLeft.setBackgroundResource(android.R.drawable.list_selector_background);
            cardViewLeft.setOnClickListener(this);
            skillViewModel = SkillViewModel.getInstance();
            skillViewModel.updateUncommon();
        }


        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            View viewInflated = LayoutInflater.from(root.getContext()).inflate(R.layout.fragment_admin_create_race, (ViewGroup)root.getRootView(),false);
            AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
            builder.setView(viewInflated);
            startSpin = viewInflated.findViewById(R.id.startAbility);
            ep2Spin = viewInflated.findViewById(R.id.ep2Ability);
            ep3Spin = viewInflated.findViewById(R.id.ep3Ability);
            ep4Spin = viewInflated.findViewById(R.id.ep4Ability);
            raceName = viewInflated.findViewById(R.id.editText_raceName);
            ep0 = new ArrayList<>();
            ep2 = new ArrayList<>();
            ep3 = new ArrayList<>();
            ep4 = new ArrayList<>();
            ep0name = new ArrayList<>();
            ep2name = new ArrayList<>();
            ep3name = new ArrayList<>();
            ep4name = new ArrayList<>();
            final Button create = viewInflated.findViewById(R.id.createRace);
            create.setText("Rediger race");
            raceName.setText(raceDTOList.get(position).getRacename());
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



            ep0adapter = new ArrayAdapter<String>(viewInflated.getContext(), android.R.layout.simple_spinner_item, ep0name);
            ep0adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startSpin.setAdapter(ep0adapter);

            ep2adapter = new ArrayAdapter<String>(viewInflated.getContext(), android.R.layout.simple_spinner_item, ep2name);
            ep2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ep2Spin.setAdapter(ep2adapter);

            ep3adapter = new ArrayAdapter<String>(viewInflated.getContext(), android.R.layout.simple_spinner_item, ep3name);
            ep3adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ep3Spin.setAdapter(ep3adapter);

            ep4adapter = new ArrayAdapter<String>(viewInflated.getContext(), android.R.layout.simple_spinner_item, ep4name);
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
                    for(int i = 0; i < ep0.size(); i++){
                        if(ep0.get(i).getId()==raceDTOList.get(position).getStart()){
                            startSpin.setSelection(i);
                            System.out.println("Start ID: "+ep0.get(i).getId());
                        }
                    }
                    for(int i = 0; i < ep2.size(); i++){
                        if(ep2.get(i).getId()==raceDTOList.get(position).getEp2()){
                            ep2Spin.setSelection(i);
                            System.out.println("ep2 ID: "+ep2.get(i).getId());
                        }
                    }
                    for(int i = 0; i < ep3.size(); i++){
                        if(ep3.get(i).getId()==raceDTOList.get(position).getEp3()){
                            ep3Spin.setSelection(i);
                            System.out.println("ep3 ID: "+ep3.get(i).getId());
                        }
                    }
                    for(int i = 0; i < ep4.size(); i++){
                        if(ep4.get(i).getId()==raceDTOList.get(position).getEp4()){
                            ep4Spin.setSelection(i);
                            System.out.println("ep4 ID: "+ep4.get(i).getId());
                        }
                    }
                    raceDTO.setID(raceList.get(position).getRaceID());
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
            final AlertDialog d = builder.show();
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    raceDTO.setRacename(raceName.getText().toString());
                    raceDTO.setID(raceList.get(position).getRaceID());
                    raceViewModel.updateRace(raceDTO);
                    Toast.makeText(getContext(),"Race rettet",Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(root);
                    navController.popBackStack();
                    d.dismiss();
                }
            });

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
