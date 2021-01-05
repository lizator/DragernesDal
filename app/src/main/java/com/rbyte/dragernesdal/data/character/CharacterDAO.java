package com.rbyte.dragernesdal.data.character;

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
            return new Result.Success<CharacterDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<CharacterDTO> getCharacterByID(int characterID) {
        try {
            Call<CharacterDTO> call = service.getByID(characterID);
            resp = call.execute();
            return new Result.Success<CharacterDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    Result<List<CharacterDTO>> getCharactersByUserID(int userID) {
        try {
            Call<List<CharacterDTO>> call = service.getByUserID(userID);
            respList = call.execute();
            return new Result.Success<List<CharacterDTO>>(respList.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface CharacterCallService {
        @GET("/character/byID/{characterid}")
        Call<CharacterDTO> getByID(@Path(value = "characterid") int characterid);

        @GET("/character/byUserID/{userid}")
        Call<List<CharacterDTO>> getByUserID(@Path(value = "userid") int userid);

        @POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);
    }
}