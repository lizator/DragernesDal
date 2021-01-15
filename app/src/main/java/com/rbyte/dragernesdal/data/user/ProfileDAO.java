package com.rbyte.dragernesdal.data.user;


import androidx.recyclerview.widget.RecyclerView;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.user.model.ProfileDTO;

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
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(profileCallService.class);
    }

    public Result<ProfileDTO> getByEmail(String email){
        try {
            ProfileDTO dto = new ProfileDTO();
            dto.setEmail(email);
            Call<ProfileDTO> call = service.getByEmail(dto);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<ProfileDTO>(resp.body());
            return new Result.Error( new IOException(resp.message()));
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }

    public Result<ProfileDTO> updateUser(ProfileDTO user){
        try {
            Call<ProfileDTO> call = service.updateUser(user);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<ProfileDTO>(resp.body());
            return new Result.Error( new IOException(resp.message()));
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }


    public Result<ProfileDTO> login(String email, String password) throws IOException {
        ProfileDTO dto = new ProfileDTO();
        dto.setEmail(email);
        dto.setPassHash(password);
        Call<ProfileDTO> call = service.login(dto);
        resp = call.execute();
        if (resp.body() == null) throw new IOException("ERROR: no return body/error in BE");
        return new Result.Success<ProfileDTO>(resp.body());
    }

    public Result<ProfileDTO> autoLogin(String email, String passHash) throws IOException {
        ProfileDTO dto = new ProfileDTO();
        dto.setEmail(email);
        dto.setPassHash(passHash);
        Call<ProfileDTO> call = service.autoLogin(dto);
        resp = call.execute();
        if (resp.body() == null) throw new IOException("ERROR: no return body/error in BE");
        return new Result.Success<ProfileDTO>(resp.body());
    }

    public void logout() throws IOException {
        Call<ProfileDTO> call = service.logout();
        resp = call.execute();
        if (resp.body() == null) throw new IOException("ERROR: no return body/error in BE");
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

        @POST("user/update")
        Call<ProfileDTO> updateUser(@Body ProfileDTO dto);

        @POST("/user/updatePass")
        Call<ProfileDTO> updatePassword(@Body ProfileDTO dto); //Actual password in passhash place

        @POST("user/login")
        Call<ProfileDTO> login(@Body ProfileDTO dto);

        @POST("user/autologin")
        Call<ProfileDTO> autoLogin(@Body ProfileDTO dto);

        @GET("user/logout") //TODO kill session
        Call<ProfileDTO> logout();

        @POST("user/create")
        Call<ProfileDTO> createUser(@Body ProfileDTO dto);
    }
}


