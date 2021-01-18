package com.rbyte.dragernesdal.ui.character.magic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;
import com.rbyte.dragernesdal.ui.character.magic.demon.DemonFragment;
import com.rbyte.dragernesdal.ui.character.magic.divine.DivineFragment;
import com.rbyte.dragernesdal.ui.character.magic.elemental.ElemtalFragment;
import com.rbyte.dragernesdal.ui.character.magic.necro.NecroFragment;
import com.rbyte.dragernesdal.ui.character.magic.transform.TransformFragment;
import com.rbyte.dragernesdal.ui.home.HomeFragment;

import java.util.ArrayList;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MagicFragment extends Fragment {

    private MagicViewModel magicViewModel;
    private View root2;

    private PopupHandler popHandler;
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private FragmentManager fm;
    private int state = 0;
    private int screenWidth = 0;

    private SpellAdapter spellAdapter;
    private ArrayList<SpellDTO> characterSpells;
    private ArrayList<Integer> ownedSpellIDs;

    private RecyclerView spellbookRecyclerView;

    private RadioButton elementRadio;
    private RadioButton divineRadio;
    private RadioButton necroRadio;
    private RadioButton demonRadio;
    private RadioButton transformRadio;

    private ElemtalFragment elemtalFragment;
    private DivineFragment divineFragment;
    private NecroFragment necroFragment;
    private DemonFragment demonFragment;
    private TransformFragment transformFragment;

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
        magicViewModel = MagicViewModel.getInstance();
        View root = inflater.inflate(R.layout.fragment_character_magic, container, false);
        SharedPreferences prefs = getDefaultSharedPreferences(root.getContext());
        int characterID = prefs.getInt(HomeFragment.CHARACTER_ID_SAVESPACE, -1);
        if (characterID == -1){
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_char_select);
            Toast.makeText(getContext(), "Du skal væge en karakter for at komme her ind", Toast.LENGTH_SHORT).show();
        } else {
            popHandler = new PopupHandler(getContext());
            fm = getActivity().getSupportFragmentManager();

            ownedSpellIDs = magicViewModel.getOwnedSpellIDs();
            characterSpells = magicViewModel.getCharacterSpells();

            spellAdapter = new SpellAdapter();

            spellbookRecyclerView = (RecyclerView) root.findViewById(R.id.spellbookRecycler);
            spellbookRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            spellbookRecyclerView.getLayoutParams().height = (int) (getScreenWidth(root.getContext()) / 3);
            spellbookRecyclerView.setAdapter(spellAdapter);
            spellAdapter.notifyDataSetChanged();

            magicViewModel.getCurrentEP().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    TextView eptv = root.findViewById(R.id.magicEPValueTV);
                    eptv.setText(integer + "");
                }
            });

            magicViewModel.setCurrentEP(charRepo.getCurrentChar().getCurrentep());

            magicViewModel.getUpdate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean update) {
                    if (update) {
                        switchFrag(state);
                        ownedSpellIDs = magicViewModel.getOwnedSpellIDs();
                        characterSpells = magicViewModel.getCharacterSpells();
                        spellAdapter.notifyDataSetChanged();
                        magicViewModel.setUpdate(false);
                    }
                }
            });

            elementRadio = root.findViewById(R.id.tab_elementalism);
            divineRadio = root.findViewById(R.id.tab_divination);
            necroRadio = root.findViewById(R.id.tab_necromancy);
            demonRadio = root.findViewById(R.id.tab_demonology);
            transformRadio = root.findViewById(R.id.tab_transformation);

            RadioGroup abilityRadioGroup = (RadioGroup) root.findViewById(R.id.magicRadioGroup);
            abilityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.tab_elementalism:
                            if (state != 0) {
                                state = 0;
                                switchFrag(state);
                            }

                            break;
                        case R.id.tab_divination:
                            if (state != 1) {
                                state = 1;
                                switchFrag(state);
                            }
                            ;

                            break;
                        case R.id.tab_necromancy:
                            if (state != 2) {
                                state = 2;
                                switchFrag(state);
                            }

                            break;
                        case R.id.tab_demonology:
                            if (state != 3) {
                                state = 3;
                                switchFrag(state);
                            }
                            break;
                        case R.id.tab_transformation:
                            if (state != 4) {
                                state = 4;
                                switchFrag(state);
                            }
                            break;
                    }
                }
            });


            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                    Log.d("OnBackPress", "Back pressed in MagicFragment");
                    NavController navController = Navigation.findNavController(root);
                    navController.popBackStack();
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
            switchFrag(state);
            root2 = root;
        }
        return root;
    }


    private class SpellViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView lvl;
        View view;
        public SpellViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.lineName);
            lvl = abilityViews.findViewById(R.id.spelllvlTV);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            lvl.setBackgroundResource(android.R.drawable.list_selector_background);
        }

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

    private void switchFrag(int newState){
        elemtalFragment = new ElemtalFragment();
        divineFragment = new DivineFragment();
        necroFragment = new NecroFragment();
        demonFragment = new DemonFragment();
        transformFragment = new TransformFragment();
        switch (newState){
            case 0:
                FragmentTransaction transactionk = fm.beginTransaction();
                transactionk.replace(R.id.innerLinear, elemtalFragment);
                transactionk.commit();

                elementRadio.setLayoutParams(checkedParam);
                divineRadio.setLayoutParams(uncheckedParam);
                necroRadio.setLayoutParams(uncheckedParam);
                demonRadio.setLayoutParams(uncheckedParam);
                transformRadio.setLayoutParams(uncheckedParam);
                break;
            case 1:
                FragmentTransaction transactiondi = fm.beginTransaction();
                transactiondi.replace(R.id.innerLinear, divineFragment);
                transactiondi.commit();

                elementRadio.setLayoutParams(uncheckedParam);
                divineRadio.setLayoutParams(checkedParam);
                necroRadio.setLayoutParams(uncheckedParam);
                demonRadio.setLayoutParams(uncheckedParam);
                transformRadio.setLayoutParams(uncheckedParam);
                break;
            case 2:
                FragmentTransaction transactionn = fm.beginTransaction();
                transactionn.replace(R.id.innerLinear, necroFragment);
                transactionn.commit();

                elementRadio.setLayoutParams(uncheckedParam);
                divineRadio.setLayoutParams(uncheckedParam);
                necroRadio.setLayoutParams(checkedParam);
                demonRadio.setLayoutParams(uncheckedParam);
                transformRadio.setLayoutParams(uncheckedParam);
                break;
            case 3:
                FragmentTransaction transactionde = fm.beginTransaction();
                transactionde.replace(R.id.innerLinear, demonFragment);
                transactionde.commit();

                elementRadio.setLayoutParams(uncheckedParam);
                divineRadio.setLayoutParams(uncheckedParam);
                necroRadio.setLayoutParams(uncheckedParam);
                demonRadio.setLayoutParams(checkedParam);
                transformRadio.setLayoutParams(uncheckedParam);
                break;
            case 4:
                FragmentTransaction transactiont = fm.beginTransaction();
                transactiont.replace(R.id.innerLinear, transformFragment);
                transactiont.commit();

                elementRadio.setLayoutParams(uncheckedParam);
                divineRadio.setLayoutParams(uncheckedParam);
                necroRadio.setLayoutParams(uncheckedParam);
                demonRadio.setLayoutParams(uncheckedParam);
                transformRadio.setLayoutParams(checkedParam);
                break;

        }
    }

    private class SpellAdapter extends RecyclerView.Adapter<SpellViewHolder> {
        @Override
        public int getItemCount() {
            if (characterSpells != null) return characterSpells.size();
            return 0;
        }

        @Override
        public SpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listElementViews = getLayoutInflater().inflate(R.layout.recycler_magic_spellbook_line, parent, false);
            SpellViewHolder vh = new SpellViewHolder(listElementViews);
            return vh;
        }

        @Override
        public void onBindViewHolder(SpellViewHolder vh, int position) {
            vh.name.setText(characterSpells.get(position).getSpellname());
            vh.lvl.setText("" + magicViewModel.getLvl(characterSpells.get(position).getId()));
            if (characterSpells.get(position).getId() != -1) {
                vh.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popHandler.getSpellInfoAlert(root2, characterSpells.get(position).getSpellname(), characterSpells.get(position).getItem(), characterSpells.get(position).getDuration(), characterSpells.get(position).getDesc()).show();
                    }
                });
            }
            if (position % 2 == 1) vh.view.setBackgroundColor(getResources().getColor(R.color.colorTableLine1));
            /*for (int id : ownedSpellIDs){
                if (characterSpells.get(position).getId() == id){
                    vh.checkimg.setVisibility(View.VISIBLE);
                    break;
                }
            }*/


        }
    }
}