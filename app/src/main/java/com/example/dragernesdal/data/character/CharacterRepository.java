package com.example.dragernesdal.data.character;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.ability.model.Ability;
import com.example.dragernesdal.data.character.model.CharacterDTO;

import java.util.HashMap;
import java.util.List;

public class CharacterRepository { //Class for getting characters and saving them for use
    private CharacterDAO dao;
    private HashMap<Integer, CharacterDTO> charList;
    private HashMap<Integer, List<Ability>> abilitiesList; //character id to their List of abilities

    private static CharacterRepository instance;

    public static CharacterRepository getInstance(){
        if (instance == null) instance = new CharacterRepository();
        return instance;
    }

    private CharacterRepository(){
        this.dao = new CharacterDAO();
        this.charList = new HashMap<>();
    }

    public Result<CharacterDTO> getCharacterByID(int characerID){
        if (charList.containsKey(characerID)){
            Result<CharacterDTO> result = new Result.Success<CharacterDTO>(charList.get(characerID));
            return result;//TODO start thread looking to see if character gotten before needs a update.
        }
        Result<CharacterDTO> result = dao.getCharacterByID(characerID);
        if (result instanceof Result.Success){
            CharacterDTO character = (CharacterDTO) ((Result.Success) result).getData();
            charList.put(characerID, character) ;
        }
        return result;

    }

}
