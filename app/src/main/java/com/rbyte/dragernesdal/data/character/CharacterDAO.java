package com.rbyte.dragernesdal.data.character;

import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;

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

public class CharacterDAO {

    private Retrofit retrofit;
    private CharacterCallService service;

    Response<CharacterDTO> resp;
    Response<List<CharacterDTO>> respList;

    public CharacterDAO() {
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(CharacterCallService.class);
    }

    public Result<CharacterDTO> createCharacter(CharacterDTO dto) {
        try {
            Call<CharacterDTO> call = service.createCharacter(dto);
            resp = call.execute();
            if (resp.code() == 200) {
                return new Result.Success<CharacterDTO>(resp.body());
            }
            throw new IOException(resp.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<CharacterDTO> getCharacterByID(int characterID) {
        try {
            Call<CharacterDTO> call = service.getByID(characterID);
            resp = call.execute();
            if (resp.code() == 200) {
                return new Result.Success<CharacterDTO>(resp.body());
            }
            throw new IOException(resp.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<CharacterDTO> createKrysling(int characterid, int race1id, int race2id) {
        try {
            Call<CharacterDTO> call = service.createKrysling(characterid, race1id, race2id);
            resp = call.execute();
            if (resp.code() == 200) {
                return new Result.Success<CharacterDTO>(resp.body());
            }
            throw new IOException(resp.message());
        } catch (IOException e) {
            Log.d("CharacterDAO", "updateCharacter: error msg: " + e.getMessage());
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<List<CharacterDTO>> getCharactersByUserID(int userID) {
        try {
            Call<List<CharacterDTO>> call = service.getByUserID(userID);
            respList = call.execute();
            if (respList.code() == 200) {
                return new Result.Success<List<CharacterDTO>>(respList.body());
            }
            throw new IOException(respList.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<List<CharacterDTO>> getCharactersByEventID(int eventID) {
        try {
            Call<List<CharacterDTO>> call = service.getByEventID(eventID);
            respList = call.execute();
            if (respList.code() == 200) {
                return new Result.Success<List<CharacterDTO>>(respList.body());
            }
            throw new IOException(respList.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<CharacterDTO> updateCharacter(CharacterDTO dto) {
        try {
            Call<CharacterDTO> call = service.updateCharacter(dto);
            resp = call.execute();
            return new Result.Success<CharacterDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CharacterDAO", "updateCharacter: error msg: " + e.getMessage());
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface CharacterCallService {
        @GET("/character/byID/{characterid}")
        Call<CharacterDTO> getByID(@Path(value = "characterid") int characterid);

        @GET("/character/byUserID/{userid}")
        Call<List<CharacterDTO>> getByUserID(@Path(value = "userid") int userid);

        @GET("/character/byEventID/{eventid}")
        Call<List<CharacterDTO>> getByEventID(@Path(value = "eventid") int eventid);

        @GET("/character/krys/{characterid}/{race1id}/{race2id}")
        Call<CharacterDTO> createKrysling(@Path(value = "characterid") int characterid, @Path(value = "race1id") int race1id, @Path(value = "race2id") int race2id);

        @POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);

        @POST("/character/update")
        Call<CharacterDTO> updateCharacter(@Body CharacterDTO character);
    }
}
