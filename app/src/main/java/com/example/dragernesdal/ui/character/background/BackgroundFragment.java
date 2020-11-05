package com.example.dragernesdal.ui.character.background;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dragernesdal.R;

public class BackgroundFragment extends Fragment {

    private BackgroundViewModel backgroundViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        backgroundViewModel =
                new ViewModelProvider(this).get(BackgroundViewModel.class);
        View root = inflater.inflate(R.layout.fragment_character_background, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        backgroundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}