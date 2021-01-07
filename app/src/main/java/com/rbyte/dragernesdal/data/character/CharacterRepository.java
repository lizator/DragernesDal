package com.rbyte.dragernesdal.data.character;

import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.AbilityDAO;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.inventory.InventoryDAO;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;

import java.util.ArrayList;
import java.util.List;

public class CharacterRepository { //Class for getting characters and saving them for use
    private CharacterDAO characterDAO;
    private AbilityDAO abilityDAO;
    private InventoryDAO inventoryDAO;
    private CharacterDTO currentChar;
    private ArrayList<CharacterDTO> currentCharList;
    private ArrayList<AbilityDTO> abilitiesList; //current characters List of abilities
    private ArrayList<InventoryDTO> inventoryList; //current characters List of inventory
    private boolean updateNeeded = true;
    private int userID = -1;
    private int characterID = -1;

    private static CharacterRepository instance;

    public static CharacterRepository getInstance(){
        if (instance == null) instance = new CharacterRepository();
        return instance;
    }

    private CharacterRepository(){
        this.characterDAO = new CharacterDAO();
        this.abilityDAO = new AbilityDAO();
        this.inventoryDAO = new InventoryDAO();
        this.currentCharList = new ArrayList<>();
        this.abilitiesList = new ArrayList<>();
        this.inventoryList = new ArrayList<>();
    }

    public CharacterDTO getCurrentChar(){
        return currentChar;
    }

    public ArrayList<AbilityDTO> getCurrentAbilitiesList() {
        return abilitiesList;
    }

    public Result<List<CharacterDTO>> getCharactersByUserID(int userID){
        if (updateNeeded || userID != this.userID) {
            Result<List<CharacterDTO>> result;
            result = characterDAO.getCharactersByUserID(userID);
            if (result instanceof Result.Success) {
                this.currentCharList = (ArrayList<CharacterDTO>) ((Result.Success) result).getData();
                Log.i("GetCharacters Results", "b");
            }
            return result;
        }
        return new Result.Success<List<CharacterDTO>>(this.currentCharList);
    }


    public Result<CharacterDTO> updateCharacter(CharacterDTO dto){
        Result<CharacterDTO> result = characterDAO.updateCharacter(dto);
        return result;
    }



    public Result<CharacterDTO> getCharacterByID(int characterID){
        Result<CharacterDTO> result = characterDAO.getCharacterByID(characterID);
        getAbilitiesByCharacterID(characterID);
        if (result instanceof Result.Success) {
            CharacterDTO character = (CharacterDTO) ((Result.Success) result).getData();
            currentChar = character;
        }
        return result;
    }

    public Result<List<AbilityDTO>> getAbilitiesByCharacterID(int characterID){
        Result<List<AbilityDTO>> result = abilityDAO.getAbilitiesByCharacterID(characterID);
        if (result instanceof Result.Success) {
            ArrayList<AbilityDTO> abilities = (ArrayList<AbilityDTO>) ((Result.Success) result).getData();
            abilitiesList = abilities;
        }
        return result;
    }

    public Result<CharacterDTO> createCharacter(CharacterDTO dto){
        Result<CharacterDTO> resp = characterDAO.createCharacter(dto);
        if (resp instanceof Result.Success){
            currentChar = ((Result.Success<CharacterDTO>) resp).getData();
        }
        return resp;
    }

    public Result<List<InventoryDTO>> getInventoryByCharacterID(int characterID){
        if (updateNeeded || characterID != this.characterID) {
            Result<List<InventoryDTO>> result = inventoryDAO.getInventoryByCharacterID(characterID);
            if (result instanceof Result.Success) {
                ArrayList<InventoryDTO> inventory = (ArrayList<InventoryDTO>) ((Result.Success) result).getData();
                inventoryList = inventory;
            }
            return result;
        }
        return new Result.Success<List<InventoryDTO>>(this.inventoryList);
    }

}
