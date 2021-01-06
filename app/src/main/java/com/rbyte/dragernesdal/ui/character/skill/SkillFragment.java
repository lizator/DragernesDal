package com.rbyte.dragernesdal.ui.character.skill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.ui.character.skill.alle.AlleFragment;
import com.rbyte.dragernesdal.ui.character.skill.kamp.KampFragment;
import com.rbyte.dragernesdal.ui.character.skill.sniger.SnigerFragment;
import com.rbyte.dragernesdal.ui.character.skill.viden.VidenFragment;

import java.util.ArrayList;

public class SkillFragment extends Fragment {

    private SkillViewModel skillViewModel = SkillViewModel.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private FragmentManager fm;
    private int state = 0;

    RadioButton kampRadio;
    RadioButton snigerRadio;
    RadioButton videnRadio;
    RadioButton alleRadio;

    LayoutParams checkedParam = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            0.85f
    );

    LayoutParams uncheckedParam = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            1.0f
    );


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_character_skill, container, false);

        skillViewModel.setRaceAbilities(charRepo.getCurrentChar().getIdrace());

        fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment kampFrag = new KampFragment();
        transaction.replace(R.id.innerLinear, kampFrag);
        transaction.commit();


        kampRadio = root.findViewById(R.id.tab_kamp);
        snigerRadio = root.findViewById(R.id.tab_sniger);
        videnRadio = root.findViewById(R.id.tab_viden);
        alleRadio = root.findViewById(R.id.tab_alle);

        skillViewModel.getCurrentEP().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                TextView eptv = root.findViewById(R.id.abilityEPValueTV);
                eptv.setText(integer+ "");
            }
        });

        skillViewModel.getUpdate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    switchFrag(state);
                    skillViewModel.setUpdate(false);
                }
            }
        });

        skillViewModel.getKampAbilities().observe(getViewLifecycleOwner(), new Observer<ArrayList<AbilityDTO>>() {
            @Override
            public void onChanged(ArrayList<AbilityDTO> abilityDTOS) {
                switchFrag(state);
            }
        });

        skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());

        TextView eptv = root.findViewById(R.id.abilityEPValueTV);
        eptv.setText(charRepo.getCurrentChar().getCurrentep() + "");

        RadioGroup abilityRadioGroup = (RadioGroup) root.findViewById(R.id.abilityRadioGroup);
        abilityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.tab_kamp:
                        if (state != 0) {
                            state = 0;
                            kampRadio.setLayoutParams(checkedParam);
                            snigerRadio.setLayoutParams(uncheckedParam);
                            videnRadio.setLayoutParams(uncheckedParam);
                            alleRadio.setLayoutParams(uncheckedParam);

                            FragmentTransaction transaction = fm.beginTransaction();
                            Fragment kampFrag = new KampFragment();
                            transaction.replace(R.id.innerLinear, kampFrag);
                            transaction.commit();
                        }

                        break;
                    case R.id.tab_sniger:
                        if (state != 1) {
                            state = 1;
                            kampRadio.setLayoutParams(uncheckedParam);
                            snigerRadio.setLayoutParams(checkedParam);
                            videnRadio.setLayoutParams(uncheckedParam);
                            alleRadio.setLayoutParams(uncheckedParam);

                            FragmentTransaction transaction = fm.beginTransaction();
                            Fragment snigerFrag = new SnigerFragment();
                            transaction.replace(R.id.innerLinear, snigerFrag);
                            transaction.commit();
                        };

                        break;
                    case R.id.tab_viden:
                        if (state != 2) {
                            state = 2;
                            kampRadio.setLayoutParams(uncheckedParam);
                            snigerRadio.setLayoutParams(uncheckedParam);
                            videnRadio.setLayoutParams(checkedParam);
                            alleRadio.setLayoutParams(uncheckedParam);

                            FragmentTransaction transaction = fm.beginTransaction();
                            Fragment videnFrag = new VidenFragment();
                            transaction.replace(R.id.innerLinear, videnFrag);
                            transaction.commit();
                        }

                        break;
                    case R.id.tab_alle:
                        if (state != 3) {
                            state = 3;
                            kampRadio.setLayoutParams(uncheckedParam);
                            snigerRadio.setLayoutParams(uncheckedParam);
                            videnRadio.setLayoutParams(uncheckedParam);
                            alleRadio.setLayoutParams(checkedParam);

                            FragmentTransaction transaction = fm.beginTransaction();
                            Fragment alleFrag = new AlleFragment();
                            transaction.replace(R.id.innerLinear, alleFrag);
                            transaction.commit();
                        }

                        break;
                }
            }
        });



        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in SkillFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }

    private void switchFrag(int newState){
        switch (newState){
            case 0:
                state = 0;
                kampRadio.setLayoutParams(checkedParam);
                snigerRadio.setLayoutParams(uncheckedParam);
                videnRadio.setLayoutParams(uncheckedParam);
                alleRadio.setLayoutParams(uncheckedParam);

                FragmentTransaction transactionk = fm.beginTransaction();
                Fragment kampFrag = new KampFragment();
                transactionk.replace(R.id.innerLinear, kampFrag);
                transactionk.commit();
                break;
            case 1:
                state = 1;
                kampRadio.setLayoutParams(uncheckedParam);
                snigerRadio.setLayoutParams(checkedParam);
                videnRadio.setLayoutParams(uncheckedParam);
                alleRadio.setLayoutParams(uncheckedParam);

                FragmentTransaction transactions = fm.beginTransaction();
                Fragment snigerFrag = new SnigerFragment();
                transactions.replace(R.id.innerLinear, snigerFrag);
                transactions.commit();
                break;
            case 2:
                state = 2;
                kampRadio.setLayoutParams(uncheckedParam);
                snigerRadio.setLayoutParams(uncheckedParam);
                videnRadio.setLayoutParams(checkedParam);
                alleRadio.setLayoutParams(uncheckedParam);

                FragmentTransaction transactionv = fm.beginTransaction();
                Fragment videnFrag = new VidenFragment();
                transactionv.replace(R.id.innerLinear, videnFrag);
                transactionv.commit();
                break;
            case 3:
                state = 3;
                kampRadio.setLayoutParams(uncheckedParam);
                snigerRadio.setLayoutParams(uncheckedParam);
                videnRadio.setLayoutParams(uncheckedParam);
                alleRadio.setLayoutParams(checkedParam);

                FragmentTransaction transactiona = fm.beginTransaction();
                Fragment alleFrag = new AlleFragment();
                transactiona.replace(R.id.innerLinear, alleFrag);
                transactiona.commit();
                break;

        }
    }


}