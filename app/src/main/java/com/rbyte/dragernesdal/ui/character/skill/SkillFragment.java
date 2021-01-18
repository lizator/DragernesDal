package com.rbyte.dragernesdal.ui.character.skill;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.background.BackgroundViewModel;
import com.rbyte.dragernesdal.ui.character.skill.alle.AlleFragment;
import com.rbyte.dragernesdal.ui.character.skill.kamp.KampFragment;
import com.rbyte.dragernesdal.ui.character.skill.sniger.SnigerFragment;
import com.rbyte.dragernesdal.ui.character.skill.viden.VidenFragment;
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.util.ArrayList;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SkillFragment extends Fragment {

    private SkillViewModel skillViewModel = SkillViewModel.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private FragmentManager fm;
    private int state = 0;

    RadioButton kampRadio;
    RadioButton snigerRadio;
    RadioButton videnRadio;
    RadioButton alleRadio;

    private KampFragment kampFrag;
    private SnigerFragment snigerFrag;
    private VidenFragment videnFrag;
    private AlleFragment alleFrag;

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
        SharedPreferences prefs = getDefaultSharedPreferences(root.getContext());
        int characterID = prefs.getInt(HomeFragment.CHARACTER_ID_SAVESPACE, -1);
        if (characterID == -1){
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_char_select);
            Toast.makeText(getContext(), "Du skal væge en karakter for at komme her ind", Toast.LENGTH_SHORT).show();
        } else {
            skillViewModel.setRaceAbilities(charRepo.getCurrentChar().getIdrace());

            fm = getActivity().getSupportFragmentManager();

            kampRadio = root.findViewById(R.id.tab_kamp);
            snigerRadio = root.findViewById(R.id.tab_sniger);
            videnRadio = root.findViewById(R.id.tab_viden);
            alleRadio = root.findViewById(R.id.tab_alle);

            skillViewModel.getCurrentEP().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    TextView eptv = root.findViewById(R.id.abilityEPValueTV);
                    eptv.setText(integer + "");
                }
            });

            skillViewModel.getUpdate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        switchFrag(state);
                        skillViewModel.setUpdate(false);
                    }
                }
            });

            skillViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());

            TextView eptv = root.findViewById(R.id.abilityEPValueTV);
            eptv.setText(charRepo.getCurrentChar().getCurrentep() + "");

            RadioGroup abilityRadioGroup = (RadioGroup) root.findViewById(R.id.abilityRadioGroup);
            abilityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.tab_kamp:
                            if (state != 0) {
                                state = 0;
                                switchFrag(state);
                            }

                            break;
                        case R.id.tab_sniger:
                            if (state != 1) {
                                state = 1;
                                switchFrag(state);
                            }
                            ;

                            break;
                        case R.id.tab_viden:
                            if (state != 2) {
                                state = 2;
                                switchFrag(state);
                            }

                            break;
                        case R.id.tab_alle:
                            if (state != 3) {
                                state = 3;
                                switchFrag(state);
                            }
                            break;
                    }
                }
            });


            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                    Log.d("OnBackPress", "Back pressed in SkillFragment");
                    NavController navController = Navigation.findNavController(root);
                    navController.popBackStack();
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
            switchFrag(state);
        }
        return root;
    }

    private void switchFrag(int newState){
        kampFrag = new KampFragment();
        videnFrag = new VidenFragment();
        snigerFrag = new SnigerFragment();
        alleFrag = new AlleFragment();
        switch (newState){
            case 0:
                state = 0;
                if (skillViewModel.getKampAbilities().getValue() == null || skillViewModel.getKampAbilities().getValue().size() == 0) {
                    Log.d("SkillFragment", "kampAbilities was null or size 0");
                    skillViewModel.updateKamp();
                }
                FragmentTransaction transactionk = fm.beginTransaction();
                if (kampFrag == null) kampFrag = new KampFragment();
                transactionk.replace(R.id.innerLinear, kampFrag);
                transactionk.commit();

                kampRadio.setLayoutParams(checkedParam);
                snigerRadio.setLayoutParams(uncheckedParam);
                videnRadio.setLayoutParams(uncheckedParam);
                alleRadio.setLayoutParams(uncheckedParam);
                break;
            case 1:
                state = 1;
                if (skillViewModel.getSnigerAbilities().getValue() == null || skillViewModel.getSnigerAbilities().getValue().size() == 0) {
                    Log.d("SkillFragment", "snigerAbilities was null or size 0");
                    skillViewModel.updateSniger();
                }
                FragmentTransaction transactions = fm.beginTransaction();
                if (snigerFrag == null) snigerFrag = new SnigerFragment();
                transactions.replace(R.id.innerLinear, snigerFrag);
                transactions.commit();

                kampRadio.setLayoutParams(uncheckedParam);
                snigerRadio.setLayoutParams(checkedParam);
                videnRadio.setLayoutParams(uncheckedParam);
                alleRadio.setLayoutParams(uncheckedParam);
                break;
            case 2:
                state = 2;
                if (skillViewModel.getVidenAbilities().getValue() == null || skillViewModel.getVidenAbilities().getValue().size() == 0) {
                    Log.d("SkillFragment", "videnAbilities was null or size 0");
                    skillViewModel.updateViden();
                }
                FragmentTransaction transactionv = fm.beginTransaction();
                if (videnFrag == null) videnFrag = new VidenFragment();
                transactionv.replace(R.id.innerLinear, videnFrag);
                transactionv.commit();

                kampRadio.setLayoutParams(uncheckedParam);
                snigerRadio.setLayoutParams(uncheckedParam);
                videnRadio.setLayoutParams(checkedParam);
                alleRadio.setLayoutParams(uncheckedParam);
                break;
            case 3:
                state = 3;
                if (skillViewModel.getAlleAbilities().getValue() == null || skillViewModel.getAlleAbilities().getValue().size() == 0) {
                    Log.d("SkillFragment", "alleAbilities was null or size 0");
                    skillViewModel.updateAlle();
                }
                if (skillViewModel.getRaceAbilities().getValue() == null || skillViewModel.getRaceAbilities().getValue().size() == 0) {
                    Log.d("SkillFragment", "RaceAbilities was null or size 0");
                    skillViewModel.setRaceAbilities(charRepo.getCurrentChar().getIdrace());
                }
                FragmentTransaction transactiona = fm.beginTransaction();
                if (alleFrag == null) alleFrag = new AlleFragment();
                transactiona.replace(R.id.innerLinear, alleFrag);
                transactiona.commit();

                kampRadio.setLayoutParams(uncheckedParam);
                snigerRadio.setLayoutParams(uncheckedParam);
                videnRadio.setLayoutParams(uncheckedParam);
                alleRadio.setLayoutParams(checkedParam);
                break;

        }
    }


}