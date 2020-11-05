package com.example.dragernesdal.ui.character;

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

public class CharFragment extends Fragment {

    private CharViewModel charViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        charViewModel =
                new ViewModelProvider(this).get(CharViewModel.class);
        View root = inflater.inflate(R.layout.fragment_character, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        charViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}