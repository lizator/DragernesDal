package com.example.dragernesdal.data.character;

import com.example.dragernesdal.data.character.model.Character;
import com.example.dragernesdal.data.user.ProfileDAO;
import com.example.dragernesdal.data.user.model.ProfileDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class CharacterDAO {

    private Retrofit retrofit;
    private CharacterCallService service;

    Response<Character> resp;

    public CharacterDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.0.108:28010")
                //.baseUrl("http://80.197.112.212:25572")
                .build();
        this.service = retrofit.create(CharacterCallService.class);
    }


    public interface CharacterCallService {
        @GET("/character/byID/{characterid}")
        Call<ProfileDTO> getByID(@Body ProfileDTO dto);

        @GET("/character/byUserID/{userid}")
        Call<List<ProfileDTO>> getByUserID(@Body ProfileDTO dto);
    }
}
