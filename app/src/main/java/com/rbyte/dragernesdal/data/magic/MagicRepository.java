package com.rbyte.dragernesdal.data.magic;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.magic.magicSchool.MagicSchoolDAO;
import com.rbyte.dragernesdal.data.magic.magicSchool.model.MagicSchoolDTO;
import com.rbyte.dragernesdal.data.magic.magicTier.MagicTierDAO;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.SpellDAO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MagicRepository {
    private static MagicRepository instance;

    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private SpellDAO spellDAO = new SpellDAO();
    private MagicTierDAO tierDAO = new MagicTierDAO();
    private MagicSchoolDAO schoolDAO = new MagicSchoolDAO();

    private ArrayList<MagicSchoolDTO> schools;
    private ArrayList<MagicTierDTO> tiers;
    private HashMap<Integer, Integer> tiercosts = new HashMap<>();
    private ArrayList<SpellDTO> spells;

    public static MagicRepository getInstance(){
        if (instance == null) instance = new MagicRepository();
        return instance;
    }

    private MagicRepository(){
        //initing tiercosts
        tiercosts.put(1, 2);
        tiercosts.put(2, 3);
        tiercosts.put(3, 3);
        tiercosts.put(4, 4);
        tiercosts.put(5, 4);
    }




    public void startGetThread(){
        new GetSpellsThread().run();
    }

    class GetSpellsThread extends Thread {

        public GetSpellsThread() {
        }

        @Override
        public void run() {
            Result<List<SpellDTO>> spellRes = spellDAO.getSpells();
            if (spellRes instanceof Result.Success){
                spells = (ArrayList<SpellDTO>) ((Result.Success<List<SpellDTO>>) spellRes).getData();
            }
        }
    }
}
