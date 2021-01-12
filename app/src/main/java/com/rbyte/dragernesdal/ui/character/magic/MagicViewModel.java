package com.rbyte.dragernesdal.ui.character.magic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.magic.MagicRepository;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;

import java.util.HashMap;

public class MagicViewModel extends ViewModel {

    MagicRepository magicRepo = MagicRepository.getInstance();
    HashMap<Integer, Integer> spellLvls = new HashMap<>();

    public MagicViewModel() {
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

}