package com.rbyte.dragernesdal.data.inventory;

import android.os.IInterface;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.data.magic.magicSchool.model.MagicSchoolDTO;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;
import com.rbyte.dragernesdal.ui.character.inventory.InventoryViewModel;

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

    public Result<List<InventoryDTO>> getActualInventory(int characterID, boolean save){
        Result<List<InventoryDTO>> inventoryRes = inventoryDAO.getActualInventoryByCharacterID(characterID);
        if (inventoryRes instanceof Result.Success && save) {
            inventory = (ArrayList<InventoryDTO>) ((Result.Success<List<InventoryDTO>>) inventoryRes).getData();
            if (inventory != null && inventory.size() != 0) {
                relationID = inventory.get(0).getIdInventoryRelation();
                state = inventoryDAO.getState(relationID);
            }
        }
        return inventoryRes;
    }

    public Result<List<InventoryDTO>> saveInventory(int characterID, ArrayList<InventoryDTO> inventory){
        return inventoryDAO.saveInventory(characterID, inventory);
    }

    public void startGetThread(){
        new GetInventoryThread().run();
    }

    public String updateState(){
        state = inventoryDAO.getState(relationID);
        return state;
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
                int count = 0;
                while (errorFound && count < 10) {
                    Log.d("InventoryRepository", "getThread: running loop");
                    errorFound = false;

                    getActualInventory(charRepo.getCurrentChar().getIdcharacter());

                    if (inventory == null || inventory.size() == 0 || relationID == -1)
                        errorFound = true; //loop because of error
                    else Log.d("InventoryRepository", "getThread: All inventory data recieved");
                    count++;
                }
                InventoryViewModel.getInstance().updateStatus();
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
