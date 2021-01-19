package com.rbyte.dragernesdal.data.character;

import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
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

    Result<List<RaceDTO>> updateKrysling(int characterid, int race1id, int race2id) {
        try {
            Call<List<RaceDTO>> call = service.updateKrysling(characterid, race1id, race2id);
            Response<List<RaceDTO>> krysResp = call.execute();
            if (krysResp.code() == 200) {
                return new Result.Success<List<RaceDTO>>(krysResp.body());
            }
            throw new IOException(resp.message());
        } catch (IOException e) {
            Log.d("CharacterDAO", "updateCharacter: error msg: " + e.getMessage());
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<CharacterDTO> deleteKrysling(int characterid) {
        try {
            Call<CharacterDTO> call = service.deleteKrysling(characterid);
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

    Result<List<CharacterDTO>> getCharactersByEventID(int eventID, int checkin) {
        try {
            Call<List<CharacterDTO>> call = service.getByEventID(eventID, checkin);
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

    Result<CharacterDTO> setCharacterByEventID(int eventID, int charid, int checkin) {
            service.setByEventID(eventID, charid, checkin);
            return new Result<CharacterDTO>();

    }

    Result<CharacterDTO> updateCharacter(CharacterDTO dto) {
        try {
            Call<CharacterDTO> call = service.updateCharacter(dto);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<CharacterDTO>(resp.body());
            else return new Result.Error(new IOException(resp.message()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CharacterDAO", "updateCharacter: error msg: " + e.getMessage());
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<CharacterDTO> deleteCharacter(int characterid) {
        try {
            Call<CharacterDTO> call = service.deleteCharacter(characterid);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<CharacterDTO>(resp.body());
            else return new Result.Error(new IOException(resp.message()));
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

        @GET("/character/byEventID/{eventid}/{checkin}")
        Call<List<CharacterDTO>> getByEventID(@Path(value = "eventid") int eventid, @Path(value = "checkin") int checkin);

        @GET("/character/krys/{characterid}/{race1id}/{race2id}")
        Call<CharacterDTO> createKrysling(@Path(value = "characterid") int characterid, @Path(value = "race1id") int race1id, @Path(value = "race2id") int race2id);

        @GET("/character/updatekrys/{characterid}/{race1id}/{race2id}")
        Call<List<RaceDTO>> updateKrysling(@Path(value = "characterid") int characterid, @Path(value = "race1id") int race1id, @Path(value = "race2id") int race2id);

        @GET("/character/deletekrys/{characterid}")
        Call<CharacterDTO> deleteKrysling(@Path(value = "characterid") int characterid);

        @GET("/character/setByEventID/{eventid}/{checkin}/{charid}")
        Call<CharacterDTO> setByEventID(@Path(value = "eventid") int eventid, @Path(value = "checkin") int checkin, @Path(value = "charid") int charid);

        @POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);

        @POST("/character/update")
        Call<CharacterDTO> updateCharacter(@Body CharacterDTO character);

        @GET("/character/delete/{characterid}")
        Call<CharacterDTO> deleteCharacter(@Path(value = "characterid") int characterid);
    }
}
