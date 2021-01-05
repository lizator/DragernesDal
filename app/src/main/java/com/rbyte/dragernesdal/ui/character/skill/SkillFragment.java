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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;

public class SkillFragment extends Fragment {

    private SkillViewModel skillViewModel;

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
                        kampRadio.setLayoutParams(checkedParam);
                        snigerRadio.setLayoutParams(uncheckedParam);
                        videnRadio.setLayoutParams(uncheckedParam);
                        alleRadio.setLayoutParams(uncheckedParam);

                        break;
                    case R.id.tab_sniger:
                        kampRadio.setLayoutParams(uncheckedParam);
                        snigerRadio.setLayoutParams(checkedParam);
                        videnRadio.setLayoutParams(uncheckedParam);
                        alleRadio.setLayoutParams(uncheckedParam);

                        break;
                    case R.id.tab_viden:
                        kampRadio.setLayoutParams(uncheckedParam);
                        snigerRadio.setLayoutParams(uncheckedParam);
                        videnRadio.setLayoutParams(checkedParam);
                        alleRadio.setLayoutParams(uncheckedParam);

                        break;
                    case R.id.tab_alle:
                        kampRadio.setLayoutParams(uncheckedParam);
                        snigerRadio.setLayoutParams(uncheckedParam);
                        videnRadio.setLayoutParams(uncheckedParam);
                        alleRadio.setLayoutParams(checkedParam);

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