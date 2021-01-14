package com.rbyte.dragernesdal.data.inventory;

import android.os.IInterface;
import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.data.magic.magicSchool.model.MagicSchoolDTO;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InventoryRepository {
    private static InventoryRepository instance;
    private InventoryDAO inventoryDAO = new InventoryDAO();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private ArrayList<InventoryDTO> inventory = new ArrayList<>();
    private int relationID = -1;
    private String state = "";

    public static InventoryRepository getInstance(){
        if (instance == null) instance = new InventoryRepository();
        return instance;
    }

    private InventoryRepository(){
    }

    public Result<List<InventoryDTO>> getActualInventory(int characterID){
        Result<List<InventoryDTO>> inventoryRes = inventoryDAO.getActualInventoryByCharacterID(characterID);
        if (inventoryRes instanceof Result.Success) {
            inventory = (ArrayList<InventoryDTO>) ((Result.Success<List<InventoryDTO>>) inventoryRes).getData();
            if (inventory != null && inventory.size() != 0) {
                relationID = inventory.get(0).getIdInventoryRelation();
                state = inventoryDAO.getState(relationID);
            }
        }
        return inventoryRes;
    }

    public void startGetThread(){
        new GetInventoryThread().run();
    }

    class GetInventoryThread extends Thread {

        public GetInventoryThread() {
        }

        @Override
        public void run() {
            Executor bgThread = Executors.newSingleThreadExecutor();
            bgThread.execute(() -> {
                inventory.clear();
                boolean errorFound = true;
                while (errorFound) {
                    Log.d("InventoryRepository", "getThread: running loop");
                    errorFound = false;

                    getActualInventory(charRepo.getCurrentChar().getIdcharacter());

                    if (inventory == null || inventory.size() == 0 || relationID == -1)
                        errorFound = true; //loop because of error
                    else Log.d("InventoryRepository", "getThread: All inventory data recieved");
                }
            });
        }
    }

    public ArrayList<InventoryDTO> getInventory() {
        return inventory;
    }

    public int getRelationID() {
        return relationID;
    }

    public String getState() {
        return state;
    }
}
