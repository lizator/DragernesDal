package com.rbyte.dragernesdal.ui.character.create;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.ui.character.select.SelectViewModel;
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CreateCharacterFragment extends Fragment implements View.OnClickListener {

    private int raceID;
    private Button create;
    private int userID;
    private EditText characterName;
    private EditText characterAge;
    private EditText characterBackground;
    private TextChecker textChecker;
    private Handler uiThread = new Handler();
    private NavController navController;
    private View root;
    private View root2;

    public CreateCharacterFragment(int raceID) {
        this.raceID = raceID;
    }
    public CreateCharacterFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_character_create, container, false);

        create = root.findViewById(R.id.create);
        characterName = root.findViewById(R.id.editText_Title);
        characterAge = root.findViewById(R.id.characterAge);
        characterBackground = root.findViewById(R.id.characterBackground);
        textChecker = new TextChecker("","","");



        create.setOnClickListener(this);
        create.setEnabled(false);
        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        raceID = prefs.getInt(ChooseRaceFragment.RACE_ID_SAVESPACE, 1);
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

        characterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChecker.setName(s.toString());
                create.setEnabled(textChecker.getDataValid());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        characterAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChecker.setAge(s.toString());
                create.setEnabled(textChecker.getDataValid());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        characterBackground.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChecker.setBackground(s.toString());
                create.setEnabled(textChecker.getDataValid());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.opretKarakter);
        Fragment fragment = this;

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("OnBackPress","Back pressed in CreateCharacterFragment");
                navController = Navigation.findNavController(root);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        root2 = root;
        return root;
    }

    //TODO implement SwipeRefreshLayout

    @Override
    public void onClick(View v) {
        CharacterDTO characterDTO = new CharacterDTO();
        characterDTO.setName(characterName.getText()+"");
        characterDTO.setAge(Integer.parseInt(characterAge.getText()+""));
        characterDTO.setBackground(characterBackground.getText()+"");
        characterDTO.setIdrace(raceID);
        characterDTO.setIduser(userID);

        CharacterRepository charRepo = CharacterRepository.getInstance();
        AbilityRepository abilityRepo = AbilityRepository.getInstance();

        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() ->{
            charRepo.createCharacter(characterDTO);
            uiThread.post(()-> {
                Toast.makeText(getActivity(), "Karakter oprettet", Toast.LENGTH_SHORT).show();
                SelectViewModel selectViewModel = SelectViewModel.getInstance();
                selectViewModel.updateCurrentCharacters();
                SharedPreferences prefs = getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                CharacterDTO foundDTO = charRepo.getCurrentChar();
                editor.putInt(HomeFragment.CHARACTER_ID_SAVESPACE, foundDTO.getIdcharacter());
                editor.apply();

                Executor bgThread2 = Executors.newSingleThreadExecutor();
                bgThread2.execute(() ->{
                    int startAbilityID = abilityRepo.getStartAbilityID(characterDTO.getIdrace());
                    uiThread.post(()-> {
                        Executor bgThread3 = Executors.newSingleThreadExecutor();
                        bgThread3.execute(() ->{
                            String commandType = abilityRepo.tryBuy(foundDTO.getIdcharacter(), startAbilityID);
                            uiThread.post(()-> {
                                switch (commandType) {
                                    case "auto": //do nothing
                                        Log.d("CharacterCreation", "correct auto getting ability");
                                        break;
                                    default: //Error
                                        Log.d("CharacterCreation", "error getting ability");
                                        //TODO: handle error
                                        break;
                                }
                                navController = Navigation.findNavController(root2);
                                navController.popBackStack(R.id.nav_home,false);
                            });
                        });
                    });
                });
            });
        });


    }

    class TextChecker{
        private String name;
        private String age;
        private String background;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        TextChecker(String name, String age, String background){
            this.name = name;
            this.age = age;
            this.background = background;
        }

        public boolean getDataValid(){
            return(name.length()!=0 && age.length()!=0 && background.length()!=0);
        }
    }
}
