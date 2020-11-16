package com.example.dragernesdal.data.character;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.ability.AbilityDAO;
import com.example.dragernesdal.data.ability.model.AbilityDTO;
import com.example.dragernesdal.data.character.model.CharacterDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterRepository { //Class for getting characters and saving them for use
    private CharacterDAO characterDAO;
    private AbilityDAO abilityDAO;
    private HashMap<Integer, CharacterDTO> charList;
    private HashMap<Integer, List<AbilityDTO>> abilitiesList; //character id to their List of abilities

    private static CharacterRepository instance;

    public static CharacterRepository getInstance(){
        if (instance == null) instance = new CharacterRepository();
        return instance;
    }

    private CharacterRepository(){
        this.characterDAO = new CharacterDAO();
        this.abilityDAO = new AbilityDAO();
        this.charList = new HashMap<>();
        this.abilitiesList = new HashMap<>();
    }

    public Result<CharacterDTO> getCharacterByID(int characerID){
        if (charList.containsKey(characerID)){
            Result<CharacterDTO> result = new Result.Success<CharacterDTO>(charList.get(characerID));
            return result;//TODO start thread looking to see if character gotten before needs a update.
        }
        Result<CharacterDTO> result = characterDAO.getCharacterByID(characerID);
        if (result instanceof Result.Success){
            CharacterDTO character = (CharacterDTO) ((Result.Success) result).getData();
            charList.put(characerID, character) ;
        }
        return result;
    }

    public Result<List<AbilityDTO>> getAbilitiesByID(int characerID){
        if (abilitiesList.containsKey(characerID)){
            Result<List<AbilityDTO>> result = new Result.Success<List<AbilityDTO>>(abilitiesList.get(characerID));
            return result;//TODO start thread looking to see if character gotten before needs a update.
        }
        Result<List<AbilityDTO>> result = abilityDAO.getAbilitiesByID(characerID);
        if (result instanceof Result.Success){
            ArrayList<AbilityDTO> abilities = (ArrayList<AbilityDTO>) ((Result.Success) result).getData();
            abilitiesList.put(characerID, abilities);
        }
        return result;
    }

}
