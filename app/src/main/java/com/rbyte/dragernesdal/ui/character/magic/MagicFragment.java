package com.rbyte.dragernesdal.ui.character.magic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.R;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;
import com.rbyte.dragernesdal.ui.PopupHandler;

import java.util.ArrayList;

public class MagicFragment extends Fragment {

    private MagicViewModel magicViewModel;
    private View root2;

    private PopupHandler popHandler;
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private FragmentManager fm;
    private int state = 0;

    private SpellAdapter spellAdapter;
    private ArrayList<SpellDTO> characterSpells;
    private ArrayList<Integer> ownedSpellIDs;

    private RecyclerView spellbookRecyclerView;

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
        popHandler = new PopupHandler(getContext());

        ownedSpellIDs = new ArrayList<>();
        characterSpells = new ArrayList<>();
        ArrayList<MagicTierDTO> charTiers = charRepo.getCurrentTierList();
        for (MagicTierDTO tier : charTiers){
            if (tier.getSpell1ID() != 0) addSpellToSpellBook(tier.getSpell1ID());
            if (tier.getSpell2ID() != 0) addSpellToSpellBook(tier.getSpell2ID());
            if (tier.getSpell3ID() != 0) addSpellToSpellBook(tier.getSpell3ID());
            if (tier.getSpell4ID() != 0) addSpellToSpellBook(tier.getSpell4ID());
            if (tier.getSpell5ID() != 0) addSpellToSpellBook(tier.getSpell5ID());
        }

        spellAdapter = new SpellAdapter();

        spellbookRecyclerView = (RecyclerView) root.findViewById(R.id.spellbookRecycler);
        spellbookRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        spellbookRecyclerView.setAdapter(spellAdapter);
        spellAdapter.notifyDataSetChanged();


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


    private class SpellViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView lvl;
        View view;
        public SpellViewHolder(View abilityViews) {
            super(abilityViews);
            view = abilityViews;
            name = abilityViews.findViewById(R.id.magicName);
            lvl = abilityViews.findViewById(R.id.spelllvlTV);
            // Gør listeelementer klikbare og vis det ved at deres baggrunsfarve ændrer sig ved berøring
            name.setBackgroundResource(android.R.drawable.list_selector_background);
            lvl.setBackgroundResource(android.R.drawable.list_selector_background);
        }

    }

    private void addSpellToSpellBook(int spellID){
        ownedSpellIDs.add(spellID);
        characterSpells.add(magicViewModel.getSpell(spellID));
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