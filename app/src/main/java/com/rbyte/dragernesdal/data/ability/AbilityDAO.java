package com.rbyte.dragernesdal.data.ability;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class AbilityDAO {
    private Retrofit retrofit;
    private AbilityCallService service;

    Response<AbilityDTO> resp;
    Response<List<AbilityDTO>> respList;
    Response<List<String>> respStrings;

    public AbilityDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(AbilityCallService.class);
    }


    public Result<List<AbilityDTO>> getAbilitiesByCharacterID(int characterID){
        try {
            Call<List<AbilityDTO>> call = service.getByCharacterID(characterID);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getAbilitiesByRaceID(int raceID){
        try {
            Call<List<AbilityDTO>> call = service.getByRaceID(raceID);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getAbilitiesByType(String type){
        try {
            Call<List<AbilityDTO>> call = service.getByType(type);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getStarters(){
        try {
            Call<List<AbilityDTO>> call = service.getStarters();
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getAllUnCommonAbilities(){
        try {
            Call<List<AbilityDTO>> call = service.getAllUnCommonAbilities();
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<String>> getTypes(){
        try {
            Call<List<String>> call = service.getTypes();
            respStrings = call.execute();
            if (respStrings.code() == 200) return new Result.Success<List<String>>(respStrings.body());
            return new Result.Error(new IOException(respStrings.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getAll(){
        try {
            Call<List<AbilityDTO>> call = service.getAll();
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public Result<AbilityDTO> getAbilityByID(int abilityID){
        try {
            Call<AbilityDTO> call = service.getByID(abilityID);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<AbilityDTO>(resp.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> updateAbility(AbilityDTO dto){
        try {
            Call<AbilityDTO> call = service.updateAbility(dto);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<AbilityDTO>(resp.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> buyAbility(int characterID, int abilityID){
        try {
            Call<AbilityDTO> call = service.buyAbility(characterID, abilityID);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<AbilityDTO>(resp.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> getFreeAbility(int characterID, int abilityID){
        try {
            Call<AbilityDTO> call = service.getFreeAbility(characterID, abilityID);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<AbilityDTO>(resp.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> buyAndGetFreeAbility(int characterID, int abilityID, int freeID){
        try {
            Call<List<AbilityDTO>> call = service.buyAndGetFreeAbility(characterID, abilityID, freeID);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> buyAndGetFreeAbility(int characterID, int abilityID, int freeID, boolean free){
        try {
            Call<List<AbilityDTO>> call = service.freeAndGetFreeAbility(characterID, abilityID, freeID);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public Result<AbilityDTO> addCraft(int characterID, String craft){
        AbilityDTO newCraft = new AbilityDTO();
        newCraft.setName(craft);
        try {
            Call<AbilityDTO> call = service.addCraft(characterID, newCraft);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<AbilityDTO>(resp.body());
            return new Result.Error(new IOException(resp.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> createAbility(AbilityDTO dto){
        try {
            Call<AbilityDTO> call = service.createAbility(dto);
            resp = call.execute();
            return new Result.Success<AbilityDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> setAbilities(int characterid, ArrayList<AbilityDTO> abilities){
        try {
            Call<List<AbilityDTO>> call = service.setAbilities(characterid, abilities);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<AbilityDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface AbilityCallService {
        @GET("/ability/byCharacterID/{characterID}")
        Call<List<AbilityDTO>> getByCharacterID(@Path(value = "characterID") int characterID);

        @GET("/ability/byRaceID/{raceID}")
        Call<List<AbilityDTO>> getByRaceID(@Path(value = "raceID") int raceID);

        @GET("/ability/byID/{abilityID}")
        Call<AbilityDTO> getByID(@Path(value = "abilityID") int abilityID);

        @GET("/ability/byType/{type}")
        Call<List<AbilityDTO>> getByType(@Path(value = "type") String type);

        @GET("/ability/raceStaters")
        Call<List<AbilityDTO>> getStarters();

        @GET("/ability/getTypes")
        Call<List<String>> getTypes();

        @POST("/ability/edit")
        Call<AbilityDTO> updateAbility(@Body AbilityDTO abilityDTO);

        @POST("/ability/set/{characterid}")
        Call<List<AbilityDTO>> setAbilities(@Path(value = "characterid") int characterid, @Body ArrayList<AbilityDTO> abilityDTO);

        @GET("/ability/all")
        Call<List<AbilityDTO>> getAll();

        @GET("/ability/allUnCommonAbilities")
        Call<List<AbilityDTO>> getAllUnCommonAbilities();

        @GET("/ability/buy/{characterID}/{abilityID}")
        Call<AbilityDTO> buyAbility(@Path(value = "characterID") int characterID, @Path(value = "abilityID") int abilityID);

        @GET("/ability/getfree/{characterID}/{abilityID}")
        Call<AbilityDTO> getFreeAbility(@Path(value = "characterID") int characterID, @Path(value = "abilityID") int abilityID);

        @GET("/ability/buyAndGetFree/{characterID}/{abilityID}/{freeAbilityID}")
        Call<List<AbilityDTO>> buyAndGetFreeAbility(@Path(value = "characterID") int characterID, @Path(value = "abilityID") int abilityID, @Path(value = "freeAbilityID") int freeAbilityID);

        @GET("/ability/freeAndGetFree/{characterID}/{abilityID}/{freeAbilityID}")
        Call<List<AbilityDTO>> freeAndGetFreeAbility(@Path(value = "characterID") int characterID, @Path(value = "abilityID") int abilityID, @Path(value = "freeAbilityID") int freeAbilityID);

        @POST("/ability/craft/{characterID}")
        Call<AbilityDTO> addCraft(@Path(value = "characterID") int characterID, @Body AbilityDTO craft);

        @POST("/ability/create")
        Call<AbilityDTO> createAbility(@Body AbilityDTO abilityDTO);
    }
}
