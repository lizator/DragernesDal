package com.rbyte.dragernesdal.data.race;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.main.MainDAO;
import com.rbyte.dragernesdal.data.main.model.MainDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RaceDAO {
    private Retrofit retrofit;
    private RaceCallService service;

    Response<RaceDTO> resp;
    Response<List<RaceDTO>> resplst;


    public RaceDAO(){
        {
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(WebServerPointer.getServerIP())
                    .build();
            this.service = retrofit.create(RaceCallService.class);
        }
    }

    public Result<List<RaceDTO>> getRaceInfoStandart(){
        try {
            Call<List<RaceDTO>> call = service.getRaceInfoStandart();
            resplst = call.execute();
            if (resplst.code() == 200) {
                return new Result.Success<List<RaceDTO>>(resplst.body());
            }
            throw new IOException("error for getting raceinfo");
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<RaceDTO>> getRaceInfo(int raceID){
        try {
            Call<RaceDTO> call = service.getRaceInfo(raceID);
            resp = call.execute();
            if (resp.code() == 200) {
                return new Result.Success<RaceDTO>(resp.body());
            }
            throw new IOException("error for getting raceinfo");
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<RaceDTO>> getKrysRaces(int characterID){
        try {
            Call<List<RaceDTO>> call = service.getKrysRaces(characterID);
            resplst = call.execute();
            if (resplst.code() == 200) {
                return new Result.Success<List<RaceDTO>>(resplst.body());
            }
            throw new IOException(resplst.message());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public interface RaceCallService {
        @GET("/race/info/standart")
        Call<List<RaceDTO>> getRaceInfoStandart();

        @GET("/race/info/single/{raceID}")
        Call<RaceDTO> getRaceInfo(@Path(value = "raceID") int raceID);

        @GET("/race/krys/getCharacterRaces/{characterid}")
        Call<List<RaceDTO>> getKrysRaces(@Path(value = "characterid") int characterid);

        /*@POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);*/
    }

}
