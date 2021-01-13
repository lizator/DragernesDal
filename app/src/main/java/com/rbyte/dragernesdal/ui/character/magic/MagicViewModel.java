package com.rbyte.dragernesdal.ui.character.magic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.magic.MagicRepository;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;

import java.util.ArrayList;
import java.util.HashMap;

public class MagicViewModel extends ViewModel {
    private static MagicViewModel instance;
    private MagicRepository magicRepo = MagicRepository.getInstance();
    private HashMap<Integer, Integer> spellLvls = new HashMap<>();
    private ArrayList<SpellDTO> characterSpells = new ArrayList<>();
    private ArrayList<Integer> ownedSpellIDs = new ArrayList<>();
    private CharacterRepository charRepo = CharacterRepository.getInstance();

    private MutableLiveData<Integer> currentEP;
    private MutableLiveData<Boolean> update;

    public static MagicViewModel getInstance(){
        if (instance == null) instance = new MagicViewModel();
        return instance;
    }

    private MagicViewModel() {
        currentEP = new MutableLiveData<>();
        update = new MutableLiveData<>();

        for(MagicTierDTO tier : magicRepo.getTiers()){
            if (tier.getSpell1ID() != 0) spellLvls.put(tier.getSpell1ID(), tier.getLvl());
            if (tier.getSpell2ID() != 0) spellLvls.put(tier.getSpell2ID(), tier.getLvl());
            if (tier.getSpell3ID() != 0) spellLvls.put(tier.getSpell3ID(), tier.getLvl());
            if (tier.getSpell4ID() != 0) spellLvls.put(tier.getSpell4ID(), tier.getLvl());
            if (tier.getSpell5ID() != 0) spellLvls.put(tier.getSpell5ID(), tier.getLvl());
        }
    }

    public int getLvl(int spellID){
        return spellLvls.get(spellID);
    }

    public SpellDTO getSpell(int spellID){
        return magicRepo.getSpell(spellID);
    }

    public ArrayList<SpellDTO> getCharacterSpells() {
        getOwnedSpellIDs();
        return characterSpells;
    }

    public ArrayList<Integer> getOwnedSpellIDs() {
        ownedSpellIDs.clear();
        characterSpells.clear();
        ArrayList<MagicTierDTO> charTiers = charRepo.getCurrentTierList();
        for (MagicTierDTO tier : charTiers){
            if (tier.getSpell1ID() != 0) addSpellToSpellBook(tier.getSpell1ID());
            if (tier.getSpell2ID() != 0) addSpellToSpellBook(tier.getSpell2ID());
            if (tier.getSpell3ID() != 0) addSpellToSpellBook(tier.getSpell3ID());
            if (tier.getSpell4ID() != 0) addSpellToSpellBook(tier.getSpell4ID());
            if (tier.getSpell5ID() != 0) addSpellToSpellBook(tier.getSpell5ID());
        }
        return ownedSpellIDs;
    }

    private void addSpellToSpellBook(int spellID){
        ownedSpellIDs.add(spellID);
        characterSpells.add(getSpell(spellID));
    }

    public MutableLiveData<Integer> getCurrentEP() {
        return currentEP;
    }

    public void setCurrentEP(int ep) {
        currentEP.setValue(ep);
    }

    public MutableLiveData<Boolean> getUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update.setValue(update);
    }

    public void postUpdate(boolean update) {
        this.update.postValue(update);
    }
}