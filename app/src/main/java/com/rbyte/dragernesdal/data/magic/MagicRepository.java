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
    private HashMap<Integer, Integer> tiercosts = new HashMap<>();
    private ArrayList<SpellDTO> spells = new ArrayList<>();

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
}
