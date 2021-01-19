package com.rbyte.dragernesdal.ui.admin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityRepository;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.inventory.InventoryDAO;
import com.rbyte.dragernesdal.data.inventory.InventoryRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.data.magic.MagicRepository;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.race.RaceDAO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;
import com.rbyte.dragernesdal.data.user.UserRepository;
import com.rbyte.dragernesdal.data.user.model.ProfileDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.sentry.Sentry;

public class EditUserFragment extends Fragment {
    private UserRepository userRepo = UserRepository.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private MagicRepository magicRepo = MagicRepository.getInstance();
    private AbilityRepository abilityRepo = AbilityRepository.getInstance();
    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();
    private InventoryDAO inventoryDAO = new InventoryDAO();
    private RaceDAO raceDAO = new RaceDAO();
    private Handler uiThread = new Handler();
    private PopupHandler popHandler;
    private View root2;
    private int screenWidth = 0;

    private ProfileDTO user;
    private ArrayList<CharacterDTO> characters;
    private ArrayList<String> characterChoices = new ArrayList<>();
    private CharacterDTO chosenCharacter;
    private ArrayList<RaceDTO> characterKrysRacer = new ArrayList<>();
    private ArrayList<String> krysRacer1Names = new ArrayList<>();
    private ArrayList<String> krysRacer2Names = new ArrayList<>();
    private ArrayList<RaceDTO> standartRaces = new ArrayList<>();
    private ArrayList<RaceDTO> customRaces = new ArrayList<>();
    private ArrayList<RaceDTO> racesCollected = new ArrayList<>();
    private ArrayList<String> raceNames = new ArrayList<>();
    private ArrayList<InventoryDTO> inventory = new ArrayList<>();
    private ArrayList<InventoryDTO> items = new ArrayList<>();
    private LinearLayoutManager inventoryLLM;
    private InventoryAdapter inventoryAdapter = new InventoryAdapter();
    private ArrayList<AbilityDTO> ownedAbilities = new ArrayList<>();
    private OwnedAbilityAdapter ownedAbilityAdapter = new OwnedAbilityAdapter();
    private ArrayList<String> abilityTypes = new ArrayList<>();
    private ArrayList<AbilityDTO> allAbilities = new ArrayList<>();
    private ArrayList<AbilityDTO> shownAbilities = new ArrayList<>();
    private ShownAbilityAdapter shownAbilityAdapter = new ShownAbilityAdapter();
    private ArrayList<MagicTierDTO> ownedTiers = new ArrayList<>();

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
    private Button deleteCharacterbtn;
    private Spinner characterRaceSpin;
    private View krysRacerView;
    private Spinner krysRace1Spin;
    private Spinner krysRace2Spin;
    private EditText epEdit;
    private EditText strengthEdit;
    private EditText kpEdit;
    private EditText backgroundEdit;

    private Button newLinebtn;
    private Button saveInventorybtn;
    private EditText copperEdit;
    private EditText silverEdit;
    private EditText goldEdit;
    private RecyclerView inventoryRecycler;

    private Button saveAbilitiesbtn;
    private RecyclerView ownedAbilitiesRecycler;
    private Spinner typeSpin;
    private RecyclerView shownAbilitiesRecycler;

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
                loadUser();
            }
        });

        saveUserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = firstNameEdit.getText().toString();
                String last = lastNameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String phone = phoneEdit.getText().toString();

                if (first.length() != 0 && last.length() != 0 && email.length() != 0 && phone.length() == 8) {
                    user.setFirstName(first);
                    user.setLastName(last);
                    user.setEmail(email);
                    user.setPhone(Integer.parseInt(phone));
                    user.setAdmin(adminCheckBox.isChecked());

                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result res = userRepo.updateUser(user);
                        uiThread.post(() -> {
                            if (res instanceof Result.Success){
                                Toast.makeText(getContext(), "Brugeren blev gemt!", Toast.LENGTH_SHORT).show();
                            } else {
                                Result.Error fail = (Result.Error) res;
                                popHandler.getInfoAlert(root2, "Fejl", fail.getError().getMessage()).show();
                            }
                        });
                    });

                } else {
                    popHandler.getInfoAlert(root2, "Fejl", "Nogle af felterne er ikke udfyldt korrekt").show();
                }
            }
        });

        changePassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p1 = pass1.getText().toString();
                String p2 = pass2.getText().toString();

                if (p1.length() > 5 && p1.equals(p2)){
                    user.setPassHash(p1);
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<ProfileDTO> res = userRepo.updatePassword(user);
                        uiThread.post(() -> {
                           if (res instanceof Result.Success){
                               user = ((Result.Success<ProfileDTO>) res).getData();
                               Toast.makeText(getContext(), "Password ændret", Toast.LENGTH_SHORT).show();
                               pass1.setText("");
                               pass2.setText("");
                           }
                        });

                    });
                } else {
                    popHandler.getInfoAlert(root2, "Fejl", "Indtastet kodeord er ikke stort nok (6 tegn) eller ikke ens").show();
                }
            }
        });

        chooseCharacterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = chooseCharacterSpin.getSelectedItemPosition() - 1;
                if (pos != -1) {
                    chosenCharacter = characters.get(pos);
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result<List<RaceDTO>> standartRacesRes = raceDAO.getRaceInfoStandart();
                        Result<List<RaceDTO>> customRacesRes = raceDAO.getRaceInfoCustom();
                        Result<List<InventoryDTO>> inventoryRes = inventoryRepo.getActualInventory(chosenCharacter.getIdcharacter(), false);
                        Result<List<AbilityDTO>> abilityRes = charRepo.getAbilitiesByCharacterID(chosenCharacter.getIdcharacter(), false);
                        Result<List<AbilityDTO>> allAbilityRes = abilityRepo.getAll();
                        Result<List<String>> abilityTypesRes = abilityRepo.getTypes();
                        Result<List<MagicTierDTO>> magicRes = charRepo.getmagicTiers(chosenCharacter.getIdcharacter(), false);
                        uiThread.post(() -> {
                            if (magicRes instanceof Result.Success && abilityRes instanceof Result.Success &&
                                    allAbilityRes instanceof Result.Success && standartRacesRes instanceof Result.Success &&
                                    customRacesRes instanceof Result.Success && abilityTypesRes instanceof Result.Success &&
                                    inventoryRes instanceof Result.Success)
                            {
                                characterChosenView.setVisibility(View.VISIBLE);
                                ownedTiers = ((Result.Success<ArrayList<MagicTierDTO>>) magicRes).getData();
                                ownedAbilities = ((Result.Success<ArrayList<AbilityDTO>>) abilityRes).getData();
                                allAbilities = ((Result.Success<ArrayList<AbilityDTO>>) allAbilityRes).getData();
                                inventory = ((Result.Success<ArrayList<InventoryDTO>>) inventoryRes).getData();
                                abilityTypes.clear();
                                abilityTypes.add("Alle Typer");
                                abilityTypes.addAll(((Result.Success<ArrayList<String>>) abilityTypesRes).getData());
                                standartRaces = ((Result.Success<ArrayList<RaceDTO>>) standartRacesRes).getData();
                                customRaces = ((Result.Success<ArrayList<RaceDTO>>) customRacesRes).getData();
                                racesCollected.addAll(standartRaces);
                                racesCollected.addAll(customRaces);
                                if (chosenCharacter.getIdrace() == 6) {
                                    Executor bgThread2 = Executors.newSingleThreadExecutor();
                                    bgThread2.execute(() -> {
                                        Result<List<RaceDTO>> krysRacerRes = charRepo.getKrydsRaces(chosenCharacter.getIdcharacter(), false);
                                        uiThread.post(() -> {
                                           if (krysRacerRes instanceof Result.Success){
                                               characterKrysRacer = (ArrayList<RaceDTO>) ((Result.Success<List<RaceDTO>>) krysRacerRes).getData();
                                               setupKrysling();
                                           }
                                        });
                                    });
                                }
                                else {
                                    krysRacerView.setVisibility(View.GONE);
                                }
                                setupCharacter();
                            } else {
                                popHandler.getInfoAlert(root2, "Fejl", "Kunne ikke hente alt data fra karakteren").show();
                            }
                        });
                    });
                } else {
                    popHandler.getInfoAlert(root2, "Fejl", "Ingen karakter valgt").show();
                }
            }
        });

        saveCharacterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dealing with races
                boolean wasKryslin = chosenCharacter.getIdrace() == 6;
                int racePos = characterRaceSpin.getSelectedItemPosition() - 1;
                boolean isSetToKrysling = wasKryslin;
                if (racePos > -1) {
                    isSetToKrysling = racesCollected.get(racePos).getID() == 6;
                    chosenCharacter.setIdrace(racesCollected.get(racePos).getID());
                    chosenCharacter.setRaceName(racesCollected.get(racePos).getRacename());
                }
                ArrayList<Integer> krysRaces = new ArrayList<>();
                if (isSetToKrysling) {
                    krysRaces = getKrysRaces(wasKryslin);
                    if (krysRaces.get(0) == krysRaces.get(1)){
                        popHandler.getInfoAlert(root2, "Fejl", "Kryslinger kan ikke have den samme race 2 gange!").show();
                        return;
                    }
                }


                String ep = epEdit.getText().toString();
                String strength = strengthEdit.getText().toString();
                String kp = kpEdit.getText().toString();
                if (ep.length() != 0) chosenCharacter.setCurrentep(Integer.parseInt(ep));
                else chosenCharacter.setCurrentep(0);
                if (strength.length() != 0) chosenCharacter.setStrength(Integer.parseInt(strength));
                else chosenCharacter.setStrength(0);
                if (kp.length() != 0) chosenCharacter.setHealth(Integer.parseInt(kp));
                else chosenCharacter.setHealth(0);

                chosenCharacter.setBackground(backgroundEdit.getText().toString());
                ArrayList<Integer> newkrysRaces = krysRaces;
                if (wasKryslin && isSetToKrysling){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result charRes = charRepo.updateCharacter(chosenCharacter);
                        uiThread.post(() -> {
                            if (charRes instanceof Result.Success) {
                                Executor bgThread2 = Executors.newSingleThreadExecutor();
                                bgThread2.execute(() -> {
                                    charRepo.updateKrysling(chosenCharacter.getIdcharacter(), newkrysRaces.get(0), newkrysRaces.get(1));
                                    Result<List<RaceDTO>> krysRacerRes = charRepo.getKrydsRaces(chosenCharacter.getIdcharacter(), false);
                                    uiThread.post(() -> {
                                        if (krysRacerRes instanceof Result.Success) {
                                            Toast.makeText(getContext(), "Karakteren blev opdateret", Toast.LENGTH_SHORT).show();
                                            characterKrysRacer = (ArrayList<RaceDTO>) ((Result.Success<List<RaceDTO>>) krysRacerRes).getData();
                                            setupCharacter();
                                            setupKrysling();
                                        } else popHandler.getInfoAlert(root2, "Fejl", "Der skete en fejl da krys-racerne skulle gemmes. Prøv at gemme igen.").show();
                                    });
                                });
                            } else {
                                popHandler.getInfoAlert(root2, "Fejl", "Karakteren kunne ikke gemmes.").show();
                            }
                        });

                    });
                } else if (!wasKryslin && isSetToKrysling){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result charRes = charRepo.updateCharacter(chosenCharacter);
                        uiThread.post(() -> {
                            if (charRes instanceof Result.Success) {
                                Executor bgThread2 = Executors.newSingleThreadExecutor();
                                bgThread2.execute(() -> {
                                    charRepo.createKrysling(chosenCharacter.getIdcharacter(), newkrysRaces.get(0), newkrysRaces.get(1));
                                    Result<List<RaceDTO>> krysRacerRes = charRepo.getKrydsRaces(chosenCharacter.getIdcharacter(), false);
                                    uiThread.post(() -> {
                                        if (krysRacerRes instanceof Result.Success) {
                                            Toast.makeText(getContext(), "Karakteren blev opdateret", Toast.LENGTH_SHORT).show();
                                            characterKrysRacer = (ArrayList<RaceDTO>) ((Result.Success<List<RaceDTO>>) krysRacerRes).getData();
                                            setupCharacter();
                                            setupKrysling();
                                        } else popHandler.getInfoAlert(root2, "Fejl", "Der skete en fejl da krys-racerne skulle gemmes. Prøv at gemme igen.").show();
                                    });
                                });
                            } else {
                                popHandler.getInfoAlert(root2, "Fejl", "Karakteren kunne ikke gemmes.").show();
                            }
                        });

                    });
                } else if (wasKryslin && !isSetToKrysling){
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result charRes = charRepo.updateCharacter(chosenCharacter);
                        uiThread.post(() -> {
                            if (charRes instanceof Result.Success) {
                                Executor bgThread2 = Executors.newSingleThreadExecutor();
                                bgThread2.execute(() -> {
                                    charRepo.deleteKrysling(chosenCharacter.getIdcharacter());
                                    uiThread.post(() -> {
                                        Toast.makeText(getContext(), "Karakteren blev opdateret", Toast.LENGTH_SHORT).show();
                                        characterKrysRacer.clear();
                                        setupCharacter();
                                    });
                                });
                            } else {
                                popHandler.getInfoAlert(root2, "Fejl", "Karakteren kunne ikke gemmes.").show();
                            }
                        });

                    });
                } else {
                    Executor bgThread = Executors.newSingleThreadExecutor();
                    bgThread.execute(() -> {
                        Result charRes = charRepo.updateCharacter(chosenCharacter);
                        uiThread.post(() -> {
                            if (charRes instanceof Result.Success) {
                                Toast.makeText(getContext(), "Karakteren blev opdateret", Toast.LENGTH_SHORT).show();
                                setupCharacter();
                            } else {
                                popHandler.getInfoAlert(root2, "Fejl", "Karakteren kunne ikke gemmes.").show();
                            }
                        });

                    });
                }

            }
        });

        deleteCharacterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popHandler.getConfirmDeleteCharacterAlert(root2, chosenCharacter.getName(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Executor bgThread = Executors.newSingleThreadExecutor();
                        bgThread.execute(() -> {
                            Result res = charRepo.deleteCharacter(chosenCharacter.getIdcharacter());
                            uiThread.post(() -> {
                                if (res instanceof Result.Success){
                                    Toast.makeText(getContext(), "Karakteren blev slettet", Toast.LENGTH_SHORT).show();
                                    loadUser();
                                } else {
                                    popHandler.getInfoAlert(root2, "Fejl", "Karakteren kunne ikke blive slettet").show();
                                }
                            });
                        });
                    }
                }).show();
            }
        });

        characterRaceSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String choice = raceNames.get(position);
                if (choice.equals("Krysling") || (position == 0 && chosenCharacter.getIdrace() == 6)){
                    krysRacerView.setVisibility(View.VISIBLE);
                    setupKrysling();
                    //TODO: fix
                } else {
                    krysRacerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        newLinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InventoryDTO dto = new InventoryDTO();
                if (items.size() > 0) dto.setIdItem(items.get(items.size()-1).getIdItem()+1);
                else  dto.setIdItem(3);
                dto.setIdInventoryRelation(inventory.get(0).getIdInventoryRelation());
                dto.setItemName("");
                dto.setAmount(0);
                items.add(dto);
                inventoryAdapter.notifyDataSetChanged();
            }
        });

        saveInventorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() -> {
                    ArrayList<InventoryDTO> newInventory = new ArrayList<>();
                    InventoryDTO gold = new InventoryDTO();
                    InventoryDTO silver = new InventoryDTO();
                    InventoryDTO copper = new InventoryDTO();
                    gold.setIdItem(0);
                    silver.setIdItem(1);
                    copper.setIdItem(2);
                    gold.setItemName("Guld");
                    silver.setItemName("Sølv");
                    copper.setItemName("Kobber");
                    gold.setAmount(Integer.parseInt(goldEdit.getText().toString()));
                    silver.setAmount(Integer.parseInt(silverEdit.getText().toString()));
                    copper.setAmount(Integer.parseInt(copperEdit.getText().toString()));
                    newInventory.add(gold);
                    newInventory.add(silver);
                    newInventory.add(copper);
                    newInventory.addAll(items);

                    Result<List<InventoryDTO>> inventoryRes =  inventoryRepo.saveInventory(chosenCharacter.getIdcharacter(), newInventory);
                    uiThread.post(() -> {
                        if (inventoryRes instanceof Result.Success){
                            Toast.makeText(getContext(), "Inventar gemt!", Toast.LENGTH_SHORT).show();
                            Executor bgThread2 = Executors.newSingleThreadExecutor();
                            bgThread2.execute(() -> {
                                inventoryDAO.confirm(chosenCharacter.getIdcharacter());
                            });
                        } else {
                            popHandler.getInfoAlert(root2, "Fejl", "Invertaren kunne ikke blive gemt").show();
                        }
                    });

                });
            }
        });

        saveAbilitiesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() -> {
                    Result<List<AbilityDTO>> abilitiesRes = charRepo.setAbilities(chosenCharacter.getIdcharacter(), ownedAbilities);
                    uiThread.post(() -> {
                        if (abilitiesRes instanceof Result.Success){
                            Toast.makeText(getContext(), "Evnerne blev gemt!", Toast.LENGTH_SHORT).show();
                        } else {
                            popHandler.getInfoAlert(root2, "Fejl", "Evnerne kunne ikke blive gemt").show();
                        }
                    });
                });
            }
        });

        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String choice = abilityTypes.get(position);
                loadIntoShownAbilities(choice);
                updateAbilityAdapters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        saveMagicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executor bgThread = Executors.newSingleThreadExecutor();
                bgThread.execute(() -> {
                    ArrayList<MagicTierDTO> tierList = new ArrayList<>();
                    for (int i = 0; i < checkBoxIDs.length; i++){
                        if (((CheckBox) root2.findViewById(checkBoxIDs[i])).isChecked()){
                            MagicTierDTO dto = new MagicTierDTO();
                            dto.setId(i+1);
                            tierList.add(dto);
                        }
                    }
                    Result<List<MagicTierDTO>> magicTierRes = magicRepo.setCharacterMagic(chosenCharacter.getIdcharacter(), tierList);
                    uiThread.post(() -> {
                        if (magicTierRes instanceof Result.Success){
                            Toast.makeText(getContext(), "Magierne blev gemt!", Toast.LENGTH_SHORT).show();
                        } else {
                            popHandler.getInfoAlert(root2, "Fejl", "Magierne kunne ikke blive gemt!").show();
                        }

                    });
                });
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
        deleteCharacterbtn = root2.findViewById(R.id.deleteCharacterbtn);
        characterRaceSpin = root2.findViewById(R.id.raceSpinner);
        krysRacerView = root2.findViewById(R.id.kryslingRacerView);
        krysRace1Spin = root2.findViewById(R.id.krysRace1);
        krysRace2Spin = root2.findViewById(R.id.krysRace2);
        epEdit = root2.findViewById(R.id.epEdit);
        strengthEdit = root2.findViewById(R.id.strengthEdit);
        kpEdit = root2.findViewById(R.id.kpEdit);
        backgroundEdit = root2.findViewById(R.id.backgroundEdit);

        newLinebtn = root2.findViewById(R.id.newLinebtn);
        saveInventorybtn = root2.findViewById(R.id.saveInventorybtn);
        copperEdit = root2.findViewById(R.id.copperEdit);
        silverEdit = root2.findViewById(R.id.silverEdit);
        goldEdit = root2.findViewById(R.id.goldEdit);
        inventoryRecycler = root2.findViewById(R.id.inventoryRecycler);
        inventoryRecycler.getLayoutParams().height = (int) (getScreenWidth(root2.getContext()) / 2);

        saveAbilitiesbtn = root2.findViewById(R.id.saveAbilitiesbtn);
        ownedAbilitiesRecycler = root2.findViewById(R.id.owningRecycler);
        ownedAbilitiesRecycler.getLayoutParams().height = (int) (getScreenWidth(root2.getContext()) / 2);
        typeSpin = root2.findViewById(R.id.typeSpinner);
        shownAbilitiesRecycler = root2.findViewById(R.id.allRecycler);
        shownAbilitiesRecycler.getLayoutParams().height = (int) (getScreenWidth(root2.getContext()) / 2);

        saveMagicbtn = root2.findViewById(R.id.saveMagicbtn);
        checkBoxIDs = new int[]{
                R.id.divi1, R.id.divi2, R.id.divi3, R.id.divi4, R.id.divi5,
                R.id.demon1, R.id.demon2, R.id.demon3, R.id.demon4, R.id.demon5,
                R.id.elem1, R.id.elem2, R.id.elem3, R.id.elem4, R.id.elem5,
                R.id.necro1, R.id.necro2, R.id.necro3, R.id.necro4, R.id.necro5,
                R.id.transform1, R.id.transform2, R.id.transform3, R.id.transform4, R.id.transform5
        };
    }

    private void loadUser(){
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
                        characterChoices.add(dto.getName() + " (" + dto.getRaceName() + ")");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, characterChoices);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chooseCharacterSpin.setAdapter(adapter);

                    hideKeyboard(getActivity());

                } else {
                    popHandler.getInfoAlert(root2, "Fejl", "Karakterene kunne ikke blive hentet").show();
                    userChosenView.setVisibility(View.GONE);
                }
            });
        });
    }

    private void setupCharacter(){

        //setting up racespinner
        raceNames.clear();
        raceNames.add(chosenCharacter.getRaceName() + " (Uændret)");
        for (RaceDTO dto : racesCollected){
            raceNames.add(dto.getRacename());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, raceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        characterRaceSpin.setAdapter(adapter);

        //setting up other Character Stuff
        epEdit.setText(chosenCharacter.getCurrentep() + "");
        strengthEdit.setText(chosenCharacter.getStrength() + "");
        kpEdit.setText(chosenCharacter.getHealth() + "");
        backgroundEdit.setText(chosenCharacter.getBackground());

        setupInventory();
        setupAbilities();
        setupMagic();
    }

    private void setupKrysling(){
        krysRacer1Names.clear();
        krysRacer2Names.clear();
        krysRacerView.setVisibility(View.VISIBLE);
        if (characterKrysRacer.size() > 1) {
            krysRacer1Names.add(characterKrysRacer.get(0).getRacename() + " (Uændret)");
            krysRacer2Names.add(characterKrysRacer.get(1).getRacename() + " (Uændret)");
        }
        for (RaceDTO dto : racesCollected){
            krysRacer1Names.add(dto.getRacename());
            krysRacer2Names.add(dto.getRacename());
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, krysRacer1Names);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        krysRace1Spin.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, krysRacer2Names);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        krysRace2Spin.setAdapter(adapter2);
    }

    private void setupInventory(){

        goldEdit.setText(inventory.get(0).getAmount() + "");
        silverEdit.setText(inventory.get(1).getAmount() + "");
        copperEdit.setText(inventory.get(2).getAmount() + "");

        items.clear();
        for (int i = 3; i < inventory.size(); i++){
            items.add(inventory.get(i));
        }

        inventoryLLM = new LinearLayoutManager(root2.getContext());
        inventoryRecycler.setLayoutManager(inventoryLLM);
        inventoryRecycler.setAdapter(inventoryAdapter);
    }

    private void setupAbilities(){
        loadIntoShownAbilities("Alle typer");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, abilityTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpin.setAdapter(adapter);

        ownedAbilitiesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ownedAbilitiesRecycler.setAdapter(ownedAbilityAdapter);
        shownAbilitiesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        shownAbilitiesRecycler.setAdapter(shownAbilityAdapter);



    }

    private void setupMagic(){
        for (int i = 0; i < checkBoxIDs.length; i++) {
            for (MagicTierDTO dto : ownedTiers){
                if (i + 1 == dto.getId()){
                    CheckBox checkBox =  root2.findViewById(checkBoxIDs[i]);
                    checkBox.setChecked(true);
                    break;
                }
            }
        }
    }

    public ArrayList<Integer> getKrysRaces(boolean wasKrysling){
        int pos1 = krysRace1Spin.getSelectedItemPosition();
        int pos2 = krysRace2Spin.getSelectedItemPosition();
        ArrayList<Integer> racesFound = new ArrayList<>();

        if (wasKrysling){
            pos1 -= 1;
            pos2 -= 1;
        }

        if (pos1 > -1) racesFound.add(racesCollected.get(pos1).getID());
        else racesFound.add(characterKrysRacer.get(0).getID());
        if (pos2 > -1) racesFound.add(racesCollected.get(pos2).getID());
        else racesFound.add(characterKrysRacer.get(1).getID());
        return racesFound;
    }

    private void loadIntoShownAbilities(String type){
        shownAbilities.clear();
        if (type.equals("Alle Typer")){
            for (AbilityDTO newDto : allAbilities) {
                boolean owned = false;
                for (AbilityDTO ownDto : ownedAbilities){
                    if (newDto.getId() == ownDto.getId()){
                        owned = true;
                        break;
                    }
                }
                if (!owned) shownAbilities.add(newDto);
            }
        } else {
            for (AbilityDTO newDto : allAbilities) {
                boolean owned = false;
                for (AbilityDTO ownDto : ownedAbilities) {
                    if (newDto.getId() == ownDto.getId()) {
                        owned = true;
                        break;
                    }
                }
                if (!owned && newDto.getType().equals(type)) shownAbilities.add(newDto);
            }
        }
    }

    private void updateAbilityAdapters() {
        ownedAbilityAdapter.notifyDataSetChanged();
        shownAbilityAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private int getScreenWidth(Context context) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }
        return screenWidth;
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder{
        EditText name;
        EditText amount;
        ImageView closeImg;
        View view;
        public InventoryViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.itemNameEdit);
            amount = abilityViews.findViewById(R.id.itemAmountEdit);
            closeImg = abilityViews.findViewById(R.id.closeLine);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
        }

    }

    class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {
        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_inventory_line, parent, false);
            InventoryViewHolder vh = new InventoryViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(InventoryViewHolder vh, int position) {
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));

            TextWatcher lineChangeWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    for (int i = 0; i < items.size(); i++){
                        View view = inventoryLLM.findViewByPosition(i);
                        if (view != null) {
                            String itemName = ((EditText) view.findViewById(R.id.itemNameEdit)).getText().toString();
                            String number = ((EditText) view.findViewById(R.id.itemAmountEdit)).getText().toString();
                            items.get(i).setItemName(itemName);
                            try {
                                int amount = Integer.parseInt(number);
                                items.get(i).setAmount(amount);
                            } catch (NumberFormatException e){
                                Sentry.captureException(e);
                                Log.d("InventoryAdapter", "editUserFragment: amount not found error");
                            }

                        }
                    }
                }
            };

            vh.name.setText(items.get(position).getItemName());
            vh.amount.setText(items.get(position).getAmount() + "");
            vh.name.addTextChangedListener(lineChangeWatcher);
            vh.amount.addTextChangedListener(lineChangeWatcher);

            vh.closeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getConfirmRemoveInventoryAlert(root2, items.get(position).getItemName(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            items.remove(position);
                            inventoryAdapter.notifyDataSetChanged();
                        }
                    }).show();
                }
            });
        }
    }

    private class AbilityViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView cost;
        Button btn;
        ImageView checkimg;
        View view;
        public AbilityViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.lineName);
            cost = abilityViews.findViewById(R.id.abilityCostTv);
            btn = abilityViews.findViewById(R.id.Abilitybtn);
            checkimg = abilityViews.findViewById(R.id.checkImage);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            cost.setBackgroundResource(android.R.drawable.list_selector_background);
            btn.setBackgroundResource(android.R.drawable.list_selector_background);
            checkimg.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    private class OwnedAbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (ownedAbilities != null) return ownedAbilities.size();
            return 0;
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_skill_owned_line, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(ownedAbilities.get(position).getName());
            vh.cost.setText(ownedAbilities.get(position).getCost() + "");
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getInfoAlert(root2, ownedAbilities.get(position).getName(), ownedAbilities.get(position).getDesc()).show();
                }
            });
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));

            vh.btn.setClickable(true);
            vh.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shownAbilities.add(ownedAbilities.get(position));
                    ownedAbilities.remove(position);
                    updateAbilityAdapters();
                }
            });
        }
    }

    private class ShownAbilityAdapter extends RecyclerView.Adapter<AbilityViewHolder> {
        @Override
        public int getItemCount() {
            if (shownAbilities != null) return shownAbilities.size();
            return 0;
        }

        @Override
        public AbilityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_skill_line, parent, false);
            AbilityViewHolder vh = new AbilityViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(AbilityViewHolder vh, int position) {
            vh.name.setText(shownAbilities.get(position).getName());
            vh.cost.setText(shownAbilities.get(position).getCost() + "");
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popHandler.getInfoAlert(root2, shownAbilities.get(position).getName(), shownAbilities.get(position).getDesc()).show();
                }
            });
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
            vh.btn.setClickable(true);
            vh.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ownedAbilities.add(shownAbilities.get(position));
                    shownAbilities.remove(position);
                    updateAbilityAdapters();
                }
            });

        }
    }
}
