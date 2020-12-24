package com.rbyte.dragernesdal.data.character;

import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityDAO;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.inventory.InventoryDAO;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterRepository { //Class for getting characters and saving them for use
    private CharacterDAO characterDAO;
    private AbilityDAO abilityDAO;
    private InventoryDAO inventoryDAO;
    private HashMap<Integer, CharacterDTO> charList;
    private HashMap<Integer, List<CharacterDTO>> charListList;
    private HashMap<Integer, List<AbilityDTO>> abilitiesList; //character id to their List of abilities
    private HashMap<Integer, List<InventoryDTO>> inventoryList; //character id to their List of abilities


    private static CharacterRepository instance;

    public static CharacterRepository getInstance(){
        if (instance == null) instance = new CharacterRepository();
        return instance;
    }

    private CharacterRepository(){
        this.characterDAO = new CharacterDAO();
        this.abilityDAO = new AbilityDAO();
        this.inventoryDAO = new InventoryDAO();
        this.charList = new HashMap<>();
        this.charListList = new HashMap<>();
        this.abilitiesList = new HashMap<>();
        this.inventoryList = new HashMap<>();
    }

    public Result<List<CharacterDTO>> getCharactersByUserID(int userID){
        Result<List<CharacterDTO>> result;
        if (charListList.containsKey(userID)){
            result = new Result.Success<List<CharacterDTO>>(charListList.get(userID));
            Log.i("GetCharacters Results", "a");
        } else {
            result = characterDAO.getCharacterByUserID(userID);
            if (result instanceof Result.Success) {
                ArrayList<CharacterDTO> lst = (ArrayList<CharacterDTO>) ((Result.Success) result).getData();
                charListList.put(userID, lst);
                Log.i("GetCharacters Results", "b");
            }
        }
        ArrayList<CharacterDTO> lst = (ArrayList<CharacterDTO>) ((Result.Success) result).getData();
        if (lst.size() == 0) {
            result = characterDAO.getCharacterByUserID(userID);
            if (result instanceof Result.Success) {
                ArrayList<CharacterDTO> lst2 = (ArrayList<CharacterDTO>) ((Result.Success) result).getData();
                charListList.put(userID, lst2);
                Log.i("GetCharacters Results", "b");
            }
        }
        return result;
    }

    public List<CharacterDTO> updateCharacterList(int currUserID){
        for (int userID : charListList.keySet()) {
            Result<List<CharacterDTO>> result = characterDAO.getCharacterByUserID(userID);
            if (result instanceof Result.Success) {
                ArrayList<CharacterDTO> lst = (ArrayList<CharacterDTO>) ((Result.Success) result).getData();
                if (lst == null || lst.size() == 0) {
                    Log.i("GetCharacters Results", "c");
                } else {
                    charListList.put(userID, lst);
                }
            }
        }

        return charListList.get(currUserID);
    }

    public Result<CharacterDTO> getCharacterByID(int characterID){
        if (charList.containsKey(characterID)){
            Result<CharacterDTO> result = new Result.Success<CharacterDTO>(charList.get(characterID));
            return result;
        }
        Result<CharacterDTO> result = characterDAO.getCharacterByID(characterID);
        if (result instanceof Result.Success){
            CharacterDTO character = (CharacterDTO) ((Result.Success) result).getData();
            charList.put(characterID, character) ;
        }
        return result;
    }

    public CharacterDTO updateCharacter(int currCharacterID){
        for (int characterID : charList.keySet()) {
            Result<CharacterDTO> result = characterDAO.getCharacterByID(characterID);
            if (result instanceof Result.Success) {
                CharacterDTO character = (CharacterDTO) ((Result.Success) result).getData();
                //Checking if update is needed
                if (character == null) {
                    Log.d("CharacterRepo", "Character NULL");
                } else if (character.getDate() != charList.get(characterID).getDate() || character.getTimestamp() != charList.get(characterID).getTimestamp()) {
                    charList.put(characterID, character);
                }
            }
        }
        return charList.get(currCharacterID);
    }

    public Result<List<AbilityDTO>> getAbilitiesByCharacterID(int characterID){
        if (abilitiesList.containsKey(characterID)){
            Result<List<AbilityDTO>> result = new Result.Success<List<AbilityDTO>>(abilitiesList.get(characterID));
            return result;
        }
        Result<List<AbilityDTO>> result = abilityDAO.getAbilitiesByCharacterID(characterID);
        if (result instanceof Result.Success){
            ArrayList<AbilityDTO> abilities = (ArrayList<AbilityDTO>) ((Result.Success) result).getData();
            abilitiesList.put(characterID, abilities);
        }
        return result;
    }

    public List<AbilityDTO> updateAbilities(int currCharacterID){
        for (int characterID : abilitiesList.keySet()) {
            Result<List<AbilityDTO>> result = abilityDAO.getAbilitiesByCharacterID(characterID);
            if (result instanceof Result.Success) {
                List<AbilityDTO> abilities = (List<AbilityDTO>) ((Result.Success) result).getData();
                //Checking if update is needed
                abilitiesList.put(characterID, abilities);
            }
        }
        return abilitiesList.get(currCharacterID);
    }

    public Result<List<InventoryDTO>> getInventoryByCharacterID(int characterID){
        if (inventoryList.containsKey(characterID)){
            Result<List<InventoryDTO>> result = new Result.Success<List<InventoryDTO>>(inventoryList.get(characterID));
            return result;
        }
        Result<List<InventoryDTO>> result = inventoryDAO.getInventoryByCharacterID(characterID);
        if (result instanceof Result.Success){
            ArrayList<InventoryDTO> inventory = (ArrayList<InventoryDTO>) ((Result.Success) result).getData();
            inventoryList.put(characterID, inventory);
        }
        return result;
    }

    public List<InventoryDTO> updateInventory(int currCharacterID){
        for (int characterID : inventoryList.keySet()) {
            Result<List<InventoryDTO>> result = inventoryDAO.getInventoryByCharacterID(characterID);
            if (result instanceof Result.Success) {
                List<InventoryDTO> inventory = (List<InventoryDTO>) ((Result.Success) result).getData();
                //Checking if update is needed
                inventoryList.put(characterID, inventory);
            }
        }
        return inventoryList.get(currCharacterID);
    }

}
