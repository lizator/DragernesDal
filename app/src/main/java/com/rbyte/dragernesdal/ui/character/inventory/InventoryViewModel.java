package com.rbyte.dragernesdal.ui.character.inventory;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.inventory.InventoryRepository;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InventoryViewModel extends ViewModel {
    private static InventoryViewModel instance;
    private MutableLiveData<String> status;
    private MutableLiveData<ArrayList<InventoryDTO>> inventory;
    private InventoryRepository inventoryRepository = InventoryRepository.getInstance();
    private CharacterRepository charRepo = CharacterRepository.getInstance();
    private Handler uithread = new Handler();

    public static InventoryViewModel getInstance(){
        if (instance == null) instance = new InventoryViewModel();
        return instance;
    }

    private InventoryViewModel() {
        status = new MutableLiveData<>("notFoundYet");
        inventory = new MutableLiveData<>();
    }

    public void updateInventory(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            Result<List<InventoryDTO>> inventoryRes = new Result<>();
            int count = 0;
            while (count < 10) {
                inventoryRes = inventoryRepository.getActualInventory(charRepo.getCurrentChar().getIdcharacter());
                if (inventoryRes instanceof Result.Success) break;
                count++;
            }
            Result<List<InventoryDTO>> finRes = inventoryRes;
            uithread.post(() -> {
                ArrayList<InventoryDTO> inventory = (ArrayList<InventoryDTO>) ((Result.Success) finRes).getData();
                postInventory(inventory);
            });
        });
    }

    public void updateInventory(CharacterDTO dto){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            Result<List<InventoryDTO>> inventoryRes = new Result<>();
            int count = 0;
            while (count < 10) {
                inventoryRes = inventoryRepository.getActualInventory(dto.getIdcharacter());
                if (inventoryRes instanceof Result.Success) break;
                count++;
            }
            Result<List<InventoryDTO>> finRes = inventoryRes;
            uithread.post(() -> {
                try{
                    ArrayList<InventoryDTO> inventory = (ArrayList<InventoryDTO>) ((Result.Success) finRes).getData();
                    postInventory(inventory);
                }
                catch (ClassCastException c){
                    c.printStackTrace();
                }
            });
        });
    }




    public void updateStatus(){
        Executor bgThread = Executors.newSingleThreadExecutor();
        bgThread.execute(() -> {
            String state = inventoryRepository.updateState();
            uithread.post(() -> {
               status.postValue(state);
            });
        });
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status.setValue(status);
    }

    public void postStatus(String status) {
        this.status.postValue(status);
    }

    public MutableLiveData<ArrayList<InventoryDTO>> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<InventoryDTO> inventory) {
        this.inventory.setValue(inventory);
    }

    public void postInventory(ArrayList<InventoryDTO> inventory) {
        this.inventory.postValue(inventory);
    }
}