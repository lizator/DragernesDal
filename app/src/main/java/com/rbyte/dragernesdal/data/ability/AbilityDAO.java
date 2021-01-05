package com.rbyte.dragernesdal.data.ability;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.ability.model.AbilityDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class AbilityDAO {
    private Retrofit retrofit;
    private AbilityCallService service;

    Response<AbilityDTO> resp;
    Response<List<AbilityDTO>> respList;

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
            return new Result.Success<List<AbilityDTO>>(respList.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getAbilitiesByRaceID(int raceID){
        try {
            Call<List<AbilityDTO>> call = service.getByRaceID(raceID);
            respList = call.execute();
            return new Result.Success<List<AbilityDTO>>(respList.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<AbilityDTO>> getAbilitiesByType(String type){
        try {
            Call<List<AbilityDTO>> call = service.getByType(type);
            respList = call.execute();
            return new Result.Success<List<AbilityDTO>>(respList.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> getAbilityByID(int abilityID){
        try {
            Call<AbilityDTO> call = service.getByID(abilityID);
            resp = call.execute();
            return new Result.Success<AbilityDTO>(resp.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<AbilityDTO> buyAbility(int characterID, int abilityID){
        try {
            Call<AbilityDTO> call = service.buyAbility(characterID, abilityID);
            resp = call.execute();
            return new Result.Success<AbilityDTO>(resp.body());
        } catch (IOException e){
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

        @GET("ability/buy/{characterID}/{abilityID}")
        Call<AbilityDTO> buyAbility(@Path(value = "characterID") int characterID, @Path(value = "abilityID") int abilityID);
    }
}
