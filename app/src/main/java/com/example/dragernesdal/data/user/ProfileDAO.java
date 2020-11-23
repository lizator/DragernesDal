package com.example.dragernesdal.data.user;


import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.user.model.ProfileDTO;

import java.io.IOException;
import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class ProfileDAO {

    private Retrofit retrofit;
    private profileCallService service;

    Response<ProfileDTO> resp;

    public ProfileDAO() {
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                //.baseUrl("http://192.168.0.101:25572")
                .baseUrl("http://80.197.112.212:25572")
                .build();
        this.service = retrofit.create(profileCallService.class);
    }

    ProfileDTO getProfileByEmail(String email) throws IOException {

        ProfileDTO dto = new ProfileDTO();
        dto.setEmail(email);
        Call<ProfileDTO> call = service.getByEmail(dto);
        resp = call.execute();
        return resp.body();
    }

    Result<ProfileDTO> createUser(ProfileDTO dto){
        try {
            Call<ProfileDTO> call = service.createUser(dto);
            resp = call.execute();
            return new Result.Success<ProfileDTO>(resp.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    void getConnected() throws SQLException {
        return;
    }

    public interface profileCallService {
        @POST("user/getbyemail")
        Call<ProfileDTO> getByEmail(@Body ProfileDTO dto);

        @POST("user/create")
        Call<ProfileDTO> createUser(@Body ProfileDTO dto);
    }
}


