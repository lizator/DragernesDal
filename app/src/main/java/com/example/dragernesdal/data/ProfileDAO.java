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

    ProfileDTO getProfileByEmail(String email) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.16.162.242:8080/")
                .build();

        StringService service = retrofit.create(StringService.class);

        ProfileDTO dto = new ProfileDTO();
        dto.setEmail("test@gmail.com");
        Call<ProfileDTO> call = service.post(dto);
        resp = call.execute();
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


