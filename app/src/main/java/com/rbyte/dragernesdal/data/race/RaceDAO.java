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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    public Result<List<RaceDTO>> getRaceInfoCustom(){
        try {
            Call<List<RaceDTO>> call = service.getRaceInfoCustom();
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

    public Result<RaceDTO> getRaceInfo(int raceID){
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

    public Result<RaceDTO> createRace(RaceDTO dto) {
        try {
            Call<RaceDTO> call = service.createRace(dto);
            resp = call.execute();
            if (resp.code() == 200) {
                return new Result.Success<RaceDTO>(resp.body());
            }
            throw new IOException(resp.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<RaceDTO> updateRace(RaceDTO dto) {
        try {
            Call<RaceDTO> call = service.updateRace(dto);
            resp = call.execute();
            if (resp.code() == 200) {
                return new Result.Success<RaceDTO>(resp.body());
            }
            throw new IOException(resp.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public interface RaceCallService {
        @GET("/race/info/standart")
        Call<List<RaceDTO>> getRaceInfoStandart();

        @GET("/race/info/custom")
        Call<List<RaceDTO>> getRaceInfoCustom();

        @GET("/race/info/single/{raceID}")
        Call<RaceDTO> getRaceInfo(@Path(value = "raceID") int raceID);

        @GET("/race/krys/getCharacterRaces/{characterid}")
        Call<List<RaceDTO>> getKrysRaces(@Path(value = "characterid") int characterid);

        @POST("/race/create")
        Call<RaceDTO> createRace(@Body RaceDTO dto);

        @POST("/race/update")
        Call<RaceDTO> updateRace(@Body RaceDTO dto);
    }

}
