package com.rbyte.dragernesdal.data.magic;

import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.magic.magicSchool.MagicSchoolDAO;
import com.rbyte.dragernesdal.data.magic.magicSchool.model.MagicSchoolDTO;
import com.rbyte.dragernesdal.data.magic.magicTier.MagicTierDAO;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.SpellDAO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MagicRepository {
    private static MagicRepository instance;

    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private SpellDAO spellDAO = new SpellDAO();
    private MagicTierDAO tierDAO = new MagicTierDAO();
    private MagicSchoolDAO schoolDAO = new MagicSchoolDAO();

    private ArrayList<MagicSchoolDTO> schools = new ArrayList<>();
    private ArrayList<MagicTierDTO> tiers = new ArrayList<>();
    private HashMap<Integer, MagicTierDTO> tierMap = new HashMap<>();
    private HashMap<Integer, Integer> tiercosts = new HashMap<>();
    private ArrayList<SpellDTO> spells = new ArrayList<>();
    private HashMap<Integer, SpellDTO> spellMap;

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

        startGetThread();
    }

    public ArrayList<SpellDTO> getSpellsBySchool(int schoolID){
        MagicSchoolDTO school = new MagicSchoolDTO();
        for (MagicSchoolDTO dto : schools){
            if (dto.getId() == schoolID){
                school = dto;
                break;
            }
        }

        ArrayList<Integer> teirs = new ArrayList<>();
        teirs.add(school.getLvl1ID());
        teirs.add(school.getLvl2ID());
        teirs.add(school.getLvl3ID());
        teirs.add(school.getLvl4ID());
        teirs.add(school.getLvl5ID());

        ArrayList<SpellDTO> schoolSpells = new ArrayList<>();
        for (int tierID : teirs){
            MagicTierDTO tier = tierMap.get(tierID);
            if (tier.getSpell1ID() != 0) schoolSpells.add(getSpell(tier.getSpell1ID()));
            if (tier.getSpell2ID() != 0) schoolSpells.add(getSpell(tier.getSpell2ID()));
            if (tier.getSpell3ID() != 0) schoolSpells.add(getSpell(tier.getSpell3ID()));
            if (tier.getSpell4ID() != 0) schoolSpells.add(getSpell(tier.getSpell4ID()));
            if (tier.getSpell5ID() != 0) schoolSpells.add(getSpell(tier.getSpell5ID()));
        }

        return schoolSpells;
    }


    public void startGetThread(){
        new GetSpellsThread().run();
    }

    class GetSpellsThread extends Thread {

        public GetSpellsThread() {
        }

        @Override
        public void run() {
            Executor bgThread = Executors.newSingleThreadExecutor();
            bgThread.execute(() -> {
                boolean errorFound = true;
                while (errorFound) {
                    errorFound = false;
                    Result<List<SpellDTO>> spellRes = spellDAO.getSpells();
                    if (spellRes instanceof Result.Success) {
                        spells = (ArrayList<SpellDTO>) ((Result.Success<List<SpellDTO>>) spellRes).getData();
                    }

                    Result<List<MagicTierDTO>> tierRes = tierDAO.getTiers();
                    if (tierRes instanceof Result.Success && tiers.size() == 0) {
                        tiers = (ArrayList<MagicTierDTO>) ((Result.Success<List<MagicTierDTO>>) tierRes).getData();
                        for (MagicTierDTO dto : tiers){
                            tierMap.put(dto.getId(), dto);
                        }
                    }

                    Result<List<MagicSchoolDTO>> schoolRes = schoolDAO.getSchools();
                    if (schoolRes instanceof Result.Success && schools.size() == 0) {
                        schools = (ArrayList<MagicSchoolDTO>) ((Result.Success<List<MagicSchoolDTO>>) schoolRes).getData();
                    }

                    if (spells.size() == 0 || tiers.size() == 0 || schools.size() == 0)
                        errorFound = true; //loop cause error
                    else Log.d("MagicRepository", "getThread: All magic data recieved");
                }
            });
        }
    }



    public ArrayList<MagicSchoolDTO> getSchools() {
        return schools;
    }

    public ArrayList<MagicTierDTO> getTiers() {
        return tiers;
    }

    public HashMap<Integer, Integer> getTiercosts() {
        return tiercosts;
    }

    public ArrayList<SpellDTO> getSpells() {
        return spells;
    }

    public SpellDTO getSpell(int spellID) {
        if (spellMap == null){
            spellMap = new HashMap<>();
            for (SpellDTO dto : spells){
                spellMap.put(dto.getId(), dto);
            }
        }
        return spellMap.get(spellID);
    }
}
