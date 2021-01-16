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

public class EditUserFragment extends Fragment {
    private UserRepository userRepo = UserRepository.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private MagicRepository magicRepo = MagicRepository.getInstance();
    private AbilityRepository abilityRepo = AbilityRepository.getInstance();
    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();
    private RaceDAO raceDAO = new RaceDAO();
    private Handler uiThread = new Handler();
    private PopupHandler popHandler;
    private View root2;
    private int screenWidth = 0;

    private ProfileDTO user;
    private ArrayList<CharacterDTO> characters;
    private ArrayList<String> characterChoices = new ArrayList<>();
    private CharacterDTO chosenCharacter;
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
    private Spinner characterRaceSpin;
    private EditText epEdit;
    private EditText strengthEdit;
    private EditText kpEdit;
    private EditText backgroundEdit;

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
        characterRaceSpin = root2.findViewById(R.id.raceSpinner);
        epEdit = root2.findViewById(R.id.epEdit);
        strengthEdit = root2.findViewById(R.id.strengthEdit);
        kpEdit = root2.findViewById(R.id.kpEdit);
        backgroundEdit = root2.findViewById(R.id.backgroundEdit);

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

    private void setupInventory(){


        loadIntoShownAbilities("Alle typer");

        goldEdit.setText(inventory.get(0).getAmount() + "");
        silverEdit.setText(inventory.get(1).getAmount() + "");
        copperEdit.setText(inventory.get(2).getAmount() + "");

        for (int i = 3; i < inventory.size(); i++){
            items.add(inventory.get(i));
        }

        inventoryLLM = new LinearLayoutManager(root2.getContext());
        inventoryRecycler.setLayoutManager(inventoryLLM);
        inventoryRecycler.setAdapter(inventoryAdapter);
    }

    private void setupAbilities(){
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

    private void loadIntoShownAbilities(String type){
        shownAbilities.clear();
        if (type.equals("Alle typer")){
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
