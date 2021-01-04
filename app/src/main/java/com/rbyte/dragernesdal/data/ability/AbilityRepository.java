package com.rbyte.dragernesdal.data.ability;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;

import java.util.ArrayList;
import java.util.List;

public class AbilityRepository {
    private static AbilityRepository instance;
    private AbilityDAO abilityDAO;
    private CharacterRepository characterRepo;

    public static AbilityRepository getInstance(){
        if (instance == null) instance = new AbilityRepository();
        return instance;
    }

    private AbilityRepository(){
        this.abilityDAO = new AbilityDAO();
        this.characterRepo = CharacterRepository.getInstance();
    }

    public int getStartAbilityID(int raceID){
        Result<List<AbilityDTO>> raceAbilitiesRes = getRaceAbilities(raceID);
        if (raceAbilitiesRes instanceof Result.Success){
            ArrayList<AbilityDTO> raceAbilities = (ArrayList<AbilityDTO>) ((Result.Success) raceAbilitiesRes).getData();
            return raceAbilities.get(0).getId();
        }
        return -1; //TODO: handle errors
    }
    
    public Result<List<AbilityDTO>> getRaceAbilities(int raceID){
        return abilityDAO.getAbilitiesByRaceID(raceID);
    }

    public String tryBuy(int characterID, int abilityID){
        Result<AbilityDTO> res = abilityDAO.getAbilityByID(abilityID);
        if (res instanceof Result.Error){
            //TODO: handle errors with error msg to frontend
            return null;
        } else {
            AbilityDTO dto = ((Result.Success<AbilityDTO>) res).getData();
            switch (dto.getCommand().split(",")[0]){
                case "KP":
                    Result<CharacterDTO> KPresDTO = characterRepo.getCharacterByID(characterID);
                    if (KPresDTO instanceof Result.Error){
                        //TODO: Handle error
                    }
                    CharacterDTO KPcharacterDTO = ((Result.Success<CharacterDTO>) KPresDTO).getData();
                    KPcharacterDTO.setHealth(KPcharacterDTO.getHealth() + 1);
                    KPresDTO = characterRepo.updateCharacter(KPcharacterDTO);
                    if (KPresDTO instanceof Result.Error){
                        //TODO: Handle error
                    }
                    confirmBuy(characterID, abilityID);
                    break;
                case "STYRKE":
                    Result<CharacterDTO> STresDTO = characterRepo.getCharacterByID(characterID);
                    if (STresDTO instanceof Result.Error){
                        //TODO: Handle error
                        System.out.println("Error in STYRKE");
                    }
                    CharacterDTO STcharacterDTO = ((Result.Success<CharacterDTO>) STresDTO).getData();
                    STcharacterDTO.setStrength(STcharacterDTO.getStrength() + 1);
                    STresDTO = characterRepo.updateCharacter(STcharacterDTO);
                    if (STresDTO instanceof Result.Error){
                        //TODO: Handle error
                    }
                    confirmBuy(characterID, abilityID);
                    break;
                case "REGEN":
                    confirmBuy(characterID, abilityID); //first ability
                    confirmBuy(characterID, 90); //Regen
                    break;
                case "HÅNDVÆRK": //should return a string to init a popup
                    //TODO: make popup with Håndværk choice
                    break;
                case "VALG": //should return a string to init a popup
                    //TODO: make popup with appropriate choice
                case "KRYS3EP":
                    //TODO: make get both start abilities of races
                case "KRYS4EP":
                    //TODO: make get both 2ep abilities of races
                default: // NULL or new
                    confirmBuy(characterID, abilityID);
                    break;
            }
        }

        return "auto";
    }

    public void confirmBuy(int characterID, int abilityID){
        Result res = abilityDAO.buyAbility(characterID, abilityID);

    }
}
