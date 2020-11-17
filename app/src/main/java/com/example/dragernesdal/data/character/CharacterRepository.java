package com.example.dragernesdal.data.character;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.ability.AbilityDAO;
import com.example.dragernesdal.data.ability.model.AbilityDTO;
import com.example.dragernesdal.data.character.model.CharacterDTO;
import com.example.dragernesdal.data.inventory.InventoryDAO;
import com.example.dragernesdal.data.inventory.model.InventoryDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterRepository { //Class for getting characters and saving them for use
    private CharacterDAO characterDAO;
    private AbilityDAO abilityDAO;
    private InventoryDAO inventoryDAO;
    private HashMap<Integer, CharacterDTO> charList;
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
        this.abilitiesList = new HashMap<>();
        this.inventoryList = new HashMap<>();
    }

    public Result<CharacterDTO> getCharacterByID(int characterID){
        if (charList.containsKey(characterID)){
            Result<CharacterDTO> result = new Result.Success<CharacterDTO>(charList.get(characterID));
            return result;//TODO start thread looking to see if character gotten before needs a update.
        }
        Result<CharacterDTO> result = characterDAO.getCharacterByID(characterID);
        if (result instanceof Result.Success){
            CharacterDTO character = (CharacterDTO) ((Result.Success) result).getData();
            charList.put(characterID, character) ;
        }
        return result;
    }

    public Result<List<AbilityDTO>> getAbilitiesByCharacterID(int characterID){
        if (abilitiesList.containsKey(characterID)){
            Result<List<AbilityDTO>> result = new Result.Success<List<AbilityDTO>>(abilitiesList.get(characterID));
            return result;//TODO start thread looking to see if character gotten before needs a update.
        }
        Result<List<AbilityDTO>> result = abilityDAO.getAbilitiesByCharacterID(characterID);
        if (result instanceof Result.Success){
            ArrayList<AbilityDTO> abilities = (ArrayList<AbilityDTO>) ((Result.Success) result).getData();
            abilitiesList.put(characterID, abilities);
        }
        return result;
    }

    public Result<List<InventoryDTO>> getInventoryByCharacterID(int characterID){
        if (inventoryList.containsKey(characterID)){
            Result<List<InventoryDTO>> result = new Result.Success<List<InventoryDTO>>(inventoryList.get(characterID));
            return result;//TODO start thread looking to see if character gotten before needs a update.
        }
        Result<List<InventoryDTO>> result = inventoryDAO.getInventoryByCharacterID(characterID);
        if (result instanceof Result.Success){
            ArrayList<InventoryDTO> inventory = (ArrayList<InventoryDTO>) ((Result.Success) result).getData();
            inventoryList.put(characterID, inventory);
        }
        return result;
    }

}
