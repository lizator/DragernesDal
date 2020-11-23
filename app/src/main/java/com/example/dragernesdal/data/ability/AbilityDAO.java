package com.example.dragernesdal.data.ability;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.ability.model.AbilityDTO;

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
                //.baseUrl("http://192.168.0.101:25572")
                .baseUrl("http://80.197.112.212:25572")
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



    public interface AbilityCallService {
        @GET("/ability/byCharacterID/{characterid}")
        Call<List<AbilityDTO>> getByCharacterID(@Path(value = "characterid") int characterid);

        /*@GET("/character/byUserID/{userid}")
        Call<List<CharacterDTO>> getByUserID(@Path(value = "userid") int userid);

        @POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);*/
    }
}
