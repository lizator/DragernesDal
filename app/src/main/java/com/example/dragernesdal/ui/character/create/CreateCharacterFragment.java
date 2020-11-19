package com.example.dragernesdal.ui.character.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dragernesdal.R;
import com.example.dragernesdal.data.character.CharacterDAO;
import com.example.dragernesdal.data.character.model.CharacterDTO;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CreateCharacterFragment extends Fragment implements View.OnClickListener {

    private CreateCharacterViewModel createCharacterViewModel;
    private int raceID;
    private Button create;
    private int userID;
    private EditText characterName;
    private EditText characterAge;
    private EditText characterBackground;

    public CreateCharacterFragment(int raceID) {
        this.raceID = raceID;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createCharacterViewModel =
                new ViewModelProvider(this).get(CreateCharacterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_character_create, container, false);

        create = root.findViewById(R.id.create);
        characterName = root.findViewById(R.id.characterName);
        characterAge = root.findViewById(R.id.characterAge);
        characterBackground = root.findViewById(R.id.characterBackground);


        create.setOnClickListener(this);
        create.setEnabled(true);
        userID = Integer.parseInt(getActivity().getIntent().getStringExtra("id"));

        ImageView raceImageView = (ImageView) root.findViewById(R.id.raceImageView);

        switch (raceID) {
            case 1:
                raceImageView.setImageResource(R.drawable.rac_dvaerg);
                break;
            case 2:
                raceImageView.setImageResource(R.drawable.rac_elver);
                break;
            case 3:
                raceImageView.setImageResource(R.drawable.rac_gobliner);
                break;
            case 4:
                raceImageView.setImageResource(R.drawable.rac_granitaner);
                break;
            case 5:
                raceImageView.setImageResource(R.drawable.rac_havfolk);
                break;
            case 6:
                raceImageView.setImageResource(R.drawable.rac_krysling);
                break;
            case 7:
                raceImageView.setImageResource(R.drawable.rac_menneske);
                break;
            case 8:
                raceImageView.setImageResource(R.drawable.rac_moerkskabt);
                break;
            case 9:
                raceImageView.setImageResource(R.drawable.rac_orker);
                break;
            case 10:
                raceImageView.setImageResource(R.drawable.rac_sortelver);
                break;
            default:
                raceImageView.setImageResource(R.drawable.rac_menneske);
                break;
        } //Switch for setting image resource

        createCharacterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }


    @Override
    public void onClick(View v) {
        CharacterDTO characterDTO = new CharacterDTO();
        characterDTO.setName(characterName.getText()+"");
        characterDTO.setAge(Integer.parseInt(characterAge.getText()+""));
        characterDTO.setBackground(characterBackground.getText()+"");
        characterDTO.setIdrace(raceID);
        characterDTO.setIduser(userID);

        CharacterDAO characterDAO = new CharacterDAO();

        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() ->{
            characterDAO.createCharacter(characterDTO);
        });

    }
}
