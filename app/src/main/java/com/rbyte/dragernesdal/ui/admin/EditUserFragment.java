package com.rbyte.dragernesdal.ui.admin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.user.UserRepository;
import com.rbyte.dragernesdal.data.user.model.ProfileDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditUserFragment extends Fragment {
    private UserRepository userRepo = UserRepository.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private Handler uiThread = new Handler();
    private PopupHandler popHandler;
    private View root2;

    private ProfileDTO user;
    private ArrayList<CharacterDTO> characters;
    private ArrayList<String> characterChoices = new ArrayList<>();

    private Button chooseUserbtn;
    private EditText chooseUserEmailEdit;
    private View userChosenView;

    private Button saveUserbtn;
    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private CheckBox adminCheckBox;

    private Button changePassbtn;
    private EditText pass1;
    private EditText pass2;

    private Spinner chooseCharacterSpin;
    private Button chooseCharacterbtn;
    private View characterChosenView;

    private Button saveCharacterbtn;
    private Spinner characerRaceSpin;
    private EditText epEdit;
    private EditText strengthEdit;
    private EditText kpEdit;
    private EditText backgroundEdit;

    private Button saveAbilitiesbtn;
    private RecyclerView ownedRecycler;
    private RecyclerView allRecycler;

    private Button saveMagicbtn;
    private int[] checkBoxIDs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_edit_user, container, false);
        root2 = root;
        popHandler = new PopupHandler(getContext());
        setupViews();

        chooseUserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userChosenView.setVisibility(View.GONE);
                characterChosenView.setVisibility(View.GONE);
                String email = chooseUserEmailEdit.getText().toString();
                if (email == null || email.length() == 0){
                    popHandler.getInfoAlert(root2, "Fejl", "Der er ikke indtasten nogen mail").show();
                } else {
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<ProfileDTO> gottenRes = userRepo.getUserByEmail(email);
                        uiThread.post(() -> {
                            if (gottenRes instanceof Result.Success) {
                                user = ((Result.Success<ProfileDTO>) gottenRes).getData();
                                setupUser();
                            } else {
                                popHandler.getInfoAlert(root2, "Fejl", "Emailen findes ikke i databasen").show();
                            }
                        });
                    });
                }
            }
        });








        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in edit user");
                NavController navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return root;
    }

    private void setupViews(){
        chooseUserbtn = root2.findViewById(R.id.chooseUserbtn);
        chooseUserEmailEdit = root2.findViewById(R.id.chooseUserEmail);
        userChosenView = root2.findViewById(R.id.emailChosenView);

        saveUserbtn = root2.findViewById(R.id.saveUserbtn);
        firstNameEdit = root2.findViewById(R.id.userFirstnameEdit);
        lastNameEdit = root2.findViewById(R.id.userLastnameEdit);
        emailEdit = root2.findViewById(R.id.userEmailEdit);
        phoneEdit = root2.findViewById(R.id.userPhoneEdit);
        adminCheckBox = root2.findViewById(R.id.adminCheckBox);

        changePassbtn = root2.findViewById(R.id.changePassbtn);
        pass1 = root2.findViewById(R.id.passwordChange);
        pass2 = root2.findViewById(R.id.passwordConfirm);

        chooseCharacterSpin = root2.findViewById(R.id.characterSpinner);
        chooseCharacterbtn = root2.findViewById(R.id.chooseCharacterbtn);
        characterChosenView = root2.findViewById(R.id.characterChosenView);

        saveCharacterbtn = root2.findViewById(R.id.saveCharacterbtn);
        characerRaceSpin = root2.findViewById(R.id.raceSpinner);
        epEdit = root2.findViewById(R.id.epEdit);
        strengthEdit = root2.findViewById(R.id.strengthEdit);
        kpEdit = root2.findViewById(R.id.kpEdit);
        backgroundEdit = root2.findViewById(R.id.backgroundEdit);

        saveAbilitiesbtn = root2.findViewById(R.id.saveAbilitiesbtn);
        ownedRecycler = root2.findViewById(R.id.owningRecycler);
        allRecycler = root2.findViewById(R.id.allRecycler);

        saveMagicbtn = root2.findViewById(R.id.saveMagicbtn);
        checkBoxIDs = new int[]{
                R.id.elem1, R.id.elem2, R.id.elem3, R.id.elem4, R.id.elem5,
                R.id.divi1, R.id.divi2, R.id.divi3, R.id.divi4, R.id.divi5,
                R.id.necro1, R.id.necro2, R.id.necro3, R.id.necro4, R.id.necro5,
                R.id.demon1, R.id.demon2, R.id.demon3, R.id.demon4, R.id.demon5,
                R.id.transform1, R.id.transform2, R.id.transform3, R.id.transform4, R.id.transform5
        };
    }

    private void setupUser(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            Result<List<CharacterDTO>> charactersRes = charRepo.getCharactersByUserID(user.getId(), true);
            uiThread.post(() -> {
                if (charactersRes instanceof Result.Success) {
                    userChosenView.setVisibility(View.VISIBLE);
                    characters = (ArrayList<CharacterDTO>) ((Result.Success<List<CharacterDTO>>) charactersRes).getData();
                    firstNameEdit.setText(user.getFirstName());
                    lastNameEdit.setText(user.getLastName());
                    emailEdit.setText(user.getEmail());
                    phoneEdit.setText(user.getPhone() + "");
                    if (user.isAdmin()) {
                        adminCheckBox.setChecked(true);
                    }
                    characterChoices.clear();
                    characterChoices.add("Vælg karakter!");
                    for (CharacterDTO dto : characters) {
                        characterChoices.add(dto.getName() + "(" + dto.getRaceName() + ")");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, characterChoices);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chooseCharacterSpin.setAdapter(adapter);

                } else {
                    popHandler.getInfoAlert(root2, "Fejl", "Karakterene kunne ikke blive hentet").show();
                    userChosenView.setVisibility(View.GONE);
                }
            });
        });
    }
}
