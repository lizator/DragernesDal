package com.rbyte.dragernesdal.data.event;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.event.model.CheckInDTO;

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

public class CheckInDAO {
    private Retrofit retrofit;
    private EventCallService service;

    Response<CheckInDTO> resp;

    public CheckInDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(EventCallService.class);
    }

    public Result<CheckInDTO> setAttending(CheckInDTO dto){
        try {
            Call<CheckInDTO> call = service.setAttending(dto);
            resp = call.execute();
            return new Result.Success<CheckInDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface EventCallService {
        @POST("/event/checkin/set")
        Call<CheckInDTO> setAttending(@Body CheckInDTO CheckInDTO);
    }
}
