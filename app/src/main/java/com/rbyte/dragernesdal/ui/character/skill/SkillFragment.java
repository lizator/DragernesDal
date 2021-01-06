package com.rbyte.dragernesdal.ui.character.skill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.ui.character.skill.alle.AlleFragment;
import com.rbyte.dragernesdal.ui.character.skill.kamp.KampFragment;
import com.rbyte.dragernesdal.ui.character.skill.sniger.SnigerFragment;
import com.rbyte.dragernesdal.ui.character.skill.viden.VidenFragment;

public class SkillFragment extends Fragment {

    private SkillViewModel skillViewModel;
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
        fm = getActivity().getSupportFragmentManager();
        kampRadio = root.findViewById(R.id.tab_kamp);
        snigerRadio = root.findViewById(R.id.tab_sniger);
        videnRadio = root.findViewById(R.id.tab_viden);
        alleRadio = root.findViewById(R.id.tab_alle);

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
                            transaction.replace(R.id.innerFragment, kampFrag);
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
                            transaction.replace(R.id.innerFragment, snigerFrag);
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
                            transaction.replace(R.id.innerFragment, videnFrag);
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
                            transaction.replace(R.id.innerFragment, alleFrag);
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

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(3000);
        v.startAnimation(anim);
    }


}