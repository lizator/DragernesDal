package com.rbyte.dragernesdal.data.ability;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.character.CharacterRepository;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;
import com.rbyte.dragernesdal.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class AbilityRepository {
    private static AbilityRepository instance;
    private AbilityDAO abilityDAO;
    private CharacterRepository characterRepo;
    private ArrayList<AbilityDTO> starterAbilities = new ArrayList<>();
    private ArrayList<AbilityDTO> getAllUnCommonAbilities = new ArrayList<>();

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
            ArrayList<AbilityDTO> raceAbilities = (ArrayList<AbilityDTO>) ((Result.Success<List<AbilityDTO>>) raceAbilitiesRes).getData();
            return raceAbilities.get(0).getId();
        }
        return -1; //TODO: handle errors
    }
    
    public Result<List<AbilityDTO>> getRaceAbilities(int raceID){
        return abilityDAO.getAbilitiesByRaceID(raceID);
    }

    public Result<List<AbilityDTO>> getTypeAbilities(String type){
        return abilityDAO.getAbilitiesByType(type);
    }

    public Result<List<AbilityDTO>> getStarters(){
        Result<List<AbilityDTO>> res = abilityDAO.getStarters();
        if (res instanceof Result.Success){
            starterAbilities = (ArrayList<AbilityDTO>) ((Result.Success<List<AbilityDTO>>) res).getData();
        }
        return res;
    }

    public Result<List<AbilityDTO>> getAllUnCommonAbilities(){
        Result<List<AbilityDTO>> res = abilityDAO.getAllUnCommonAbilities();
        if (res instanceof Result.Success){
            starterAbilities = (ArrayList<AbilityDTO>) ((Result.Success<List<AbilityDTO>>) res).getData();
        }
        return res;
    }

    public String tryBuy(int characterID, int abilityID){
        Result<AbilityDTO> res = abilityDAO.getAbilityByID(abilityID);
        if (res instanceof Result.Error){
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
                case "NKP":
                    Result<CharacterDTO> KPresDTO2 = characterRepo.getCharacterByID(characterID);
                    if (KPresDTO2 instanceof Result.Error){
                        //TODO: Handle error
                    }
                    CharacterDTO KPcharacterDTO2 = ((Result.Success<CharacterDTO>) KPresDTO2).getData();
                    KPcharacterDTO2.setHealth(KPcharacterDTO2.getHealth() - 1);
                    KPresDTO = characterRepo.updateCharacter(KPcharacterDTO2);
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
                case "NSTYRKE":
                    Result<CharacterDTO> STresDTO2 = characterRepo.getCharacterByID(characterID);
                    if (STresDTO2 instanceof Result.Error){
                        //TODO: Handle error
                        System.out.println("Error in STYRKE");
                    }
                    CharacterDTO STcharacterDTO2 = ((Result.Success<CharacterDTO>) STresDTO2).getData();
                    STcharacterDTO2.setStrength(STcharacterDTO2.getStrength() - 1);
                    STresDTO = characterRepo.updateCharacter(STcharacterDTO2);
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
                    return "HÅNDVÆRK";
                case "VALG": //should return a string to init a popup
                    return dto.getCommand().split(",")[1];
                case "KRYS3EP":
                    ArrayList<RaceDTO> races3ep = (ArrayList<RaceDTO>) HomeViewModel.getInstance().getRaces().getValue();
                    confirmBuy(characterID, abilityID);
                    String fst3ep = tryBuy(characterID, races3ep.get(0).getStart(), true);
                    String scd3ep = tryBuy(characterID, races3ep.get(1).getStart(), true);
                    return "KRYS," + fst3ep + "," + scd3ep;
                case "KRYS4EP":
                    ArrayList<RaceDTO> races4ep = (ArrayList<RaceDTO>) HomeViewModel.getInstance().getRaces().getValue();
                    confirmBuy(characterID, abilityID);
                    String fst4ep = tryBuy(characterID, races4ep.get(0).getEp2(), true);
                    String scd4ep = tryBuy(characterID, races4ep.get(1).getEp2(), true);
                    return "KRYS," + fst4ep + "," + scd4ep;
                case "STARTEVNE":
                    return "STARTEVNE";
                default: // NULL or new
                    confirmBuy(characterID, abilityID);
                    break;
            }
        }

        return "auto";
    }

    public String tryBuy(int characterID, int abilityID, boolean free){
        Result<AbilityDTO> res = abilityDAO.getAbilityByID(abilityID);
        if (res instanceof Result.Error){
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
                    freeGet(characterID, abilityID);
                    break;
                case "NKP":
                    Result<CharacterDTO> KPresDTO2 = characterRepo.getCharacterByID(characterID);
                    if (KPresDTO2 instanceof Result.Error){
                        //TODO: Handle error
                    }
                    CharacterDTO KPcharacterDTO2 = ((Result.Success<CharacterDTO>) KPresDTO2).getData();
                    KPcharacterDTO2.setHealth(KPcharacterDTO2.getHealth() - 1);
                    KPresDTO = characterRepo.updateCharacter(KPcharacterDTO2);
                    if (KPresDTO instanceof Result.Error){
                        //TODO: Handle error
                    }
                    freeGet(characterID, abilityID);
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
                    freeGet(characterID, abilityID);
                    break;
                case "NSTYRKE":
                    Result<CharacterDTO> STresDTO2 = characterRepo.getCharacterByID(characterID);
                    if (STresDTO2 instanceof Result.Error){
                        //TODO: Handle error
                        System.out.println("Error in STYRKE");
                    }
                    CharacterDTO STcharacterDTO2 = ((Result.Success<CharacterDTO>) STresDTO2).getData();
                    STcharacterDTO2.setStrength(STcharacterDTO2.getStrength() - 1);
                    STresDTO = characterRepo.updateCharacter(STcharacterDTO2);
                    if (STresDTO instanceof Result.Error){
                        //TODO: Handle error
                    }
                    freeGet(characterID, abilityID);
                    break;
                case "REGEN":
                    freeGet(characterID, abilityID); //first ability
                    freeGet(characterID, 90); //Regen
                    break;
                case "HÅNDVÆRK": //should return a string to init a popup
                    return "HÅNDVÆRK";
                case "VALG": //should return a string to init a popup
                    return dto.getCommand().split(",")[1];
                case "KRYS3EP":
                    ArrayList<RaceDTO> races3ep = (ArrayList<RaceDTO>) HomeViewModel.getInstance().getRaces().getValue();
                    confirmBuy(characterID, abilityID);
                    String fst3ep = tryBuy(characterID, races3ep.get(0).getStart(), true);
                    String scd3ep = tryBuy(characterID, races3ep.get(1).getStart(), true);
                    return "KRYS," + fst3ep + "," + scd3ep;
                case "KRYS4EP":
                    ArrayList<RaceDTO> races4ep = (ArrayList<RaceDTO>) HomeViewModel.getInstance().getRaces().getValue();
                    confirmBuy(characterID, abilityID);
                    String fst4ep = tryBuy(characterID, races4ep.get(0).getEp2(), true);
                    String scd4ep = tryBuy(characterID, races4ep.get(1).getEp2(), true);
                    return "KRYS," + fst4ep + "," + scd4ep;
                case "STARTEVNE":
                    return "STARTEVNE";
                default: // NULL or new
                    freeGet(characterID, abilityID);
                    break;
            }
        }

        return "auto";
    }

    public Result confirmBuy(int characterID, int abilityID){
        Result res = abilityDAO.buyAbility(characterID, abilityID);
        return res;
    }

    public Result<List<AbilityDTO>> confirmBuyWithFree(int characterID, int abilityID, int freeAbilityID){
        Result res = abilityDAO.buyAndGetFreeAbility(characterID, abilityID, freeAbilityID);
        return res;
    }

    public Result<List<AbilityDTO>> confirmBuyWithFree(int characterID, int abilityID, int freeAbilityID, boolean free){
        Result res = abilityDAO.buyAndGetFreeAbility(characterID, abilityID, freeAbilityID, free);
        return res;
    }

    public Result<AbilityDTO> craftBuy(int characterID, String craft){
        Result res = abilityDAO.addCraft(characterID, craft);
        return res;
    }

    public Result<AbilityDTO> freeGet(int characterID, int freeAbilityID){
        Result res = abilityDAO.getFreeAbility(characterID,freeAbilityID);
        return res;
    }

    public ArrayList<AbilityDTO> getStarterAbilities() {
        return starterAbilities;
    }
}
