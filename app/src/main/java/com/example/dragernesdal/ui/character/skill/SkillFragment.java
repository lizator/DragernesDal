package com.example.dragernesdal.ui.character.skill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dragernesdal.R;
import com.example.dragernesdal.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;

public class SkillFragment extends Fragment {

    private SkillViewModel skillViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        skillViewModel =
                new ViewModelProvider(this).get(SkillViewModel.class);
        View root = inflater.inflate(R.layout.fragment_character_skill, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        skillViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in SkillFragment");
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                toolbar.setTitle(getString(R.string.menu_home));
                navigationView.setCheckedItem(R.id.nav_home);
                Fragment mFragment = new HomeFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, mFragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }
}