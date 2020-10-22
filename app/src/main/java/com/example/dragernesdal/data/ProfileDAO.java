package com.example.dragernesdal.data;


import android.widget.Toast;

import com.example.dragernesdal.data.model.ProfileDTO;
import com.example.dragernesdal.ui.login.LoginActivity;

import java.io.IOException;
import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class ProfileDAO {
    Response<ProfileDTO> resp;

    ProfileDTO getProfileByEmail(String email) throws InterruptedException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.16.234.21:8080/")
                .build();

        StringService service = retrofit.create(StringService.class);

        ProfileDTO dto = new ProfileDTO();
        dto.setEmail("test@gmail.com");
        Call<ProfileDTO> call = service.post(dto);


        call.enqueue(new Callback<ProfileDTO>() {
            @Override
            public void onResponse(Call<ProfileDTO> call, Response<ProfileDTO> response) {
                resp = response;
                return;
            }

            @Override
            public void onFailure(Call<ProfileDTO> call, Throwable t) {
                resp = null;
                return;
            }
        });
        if (resp != null) {
            dto = resp.body();
            System.out.println(call);
            return resp.body();
        }
        return null;
    }


    void getConnected() throws SQLException {
        return;
    }

    public interface StringService {
        @POST("user/getbyemail")
        Call<ProfileDTO> post(@Body ProfileDTO dto);
    }
}


