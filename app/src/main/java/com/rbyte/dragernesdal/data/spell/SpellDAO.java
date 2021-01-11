package com.rbyte.dragernesdal.data.spell;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class SpellDAO {
    private Retrofit retrofit;
    private RaceCallService service;

    Response<RaceDTO> resp;
    Response<List<RaceDTO>> resplst;


    public SpellDAO(){
        {
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(WebServerPointer.getServerIP())
                    .build();
            this.service = retrofit.create(RaceCallService.class);
        }
    }



    public interface RaceCallService {

        /*@GET("/race/info/standart")
        Call<List<RaceDTO>> getRaceInfoStandart();

        /*@GET("/race/info/single/{raceID}")
        Call<RaceDTO> getRaceInfo(@Path(value = "raceID") int raceID);

        /*@GET("/race/krys/getCharacterRaces/{characterid}")
        Call<List<RaceDTO>> getKrysRaces(@Path(value = "characterid") int characterid);

        /*@POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);
        */
    }

}
