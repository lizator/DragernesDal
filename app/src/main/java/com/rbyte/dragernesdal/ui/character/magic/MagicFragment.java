package com.rbyte.dragernesdal.ui.character.magic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.CharacterRepository;

public class MagicFragment extends Fragment {

    private MagicViewModel magicViewModel;
    private View root2;

    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private FragmentManager fm;
    private int state = 0;

    private RadioButton elementRadio;
    private RadioButton divineRadio;
    private RadioButton necroRadio;
    private RadioButton demonoRadio;
    private RadioButton transformRadio;

    private LinearLayout.LayoutParams checkedParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            0.85f
    );

    private LinearLayout.LayoutParams uncheckedParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1.0f
    );



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        magicViewModel = new ViewModelProvider(this).get(MagicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_character_magic, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in MagicFragment");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        root2 = root;
        return root;
    }
}