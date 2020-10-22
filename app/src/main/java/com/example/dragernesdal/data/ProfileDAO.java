package com.example.dragernesdal.data;


import com.example.dragernesdal.data.model.ProfileDTO;

import java.io.IOException;
import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class ProfileDAO {

    ProfileDTO getProfileByEmail(String email) throws InterruptedException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.16.234.21:8080/")
                .build();

        StringService service = retrofit.create(StringService.class);

        ProfileDTO dto = new ProfileDTO();
        dto.setEmail("test@gmail.com");
        Call<ProfileDTO> call = service.post(dto);
        Response<ProfileDTO> resp;
        try {
            resp = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            resp = null;
        }
        dto = resp.body();
        System.out.println(call);
        return resp.body();
    }


    void getConnected() throws SQLException {
        return;
    }

    public interface StringService {
        @POST("user/getbyemail")
        Call<ProfileDTO> post(@Body ProfileDTO dto);
    }
}


