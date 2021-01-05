package com.rbyte.dragernesdal.data.event;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.event.model.AttendingDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class AttendingDAO {
    private Retrofit retrofit;
    private EventCallService service;

    Response<List<Boolean>> respAttending;

    public AttendingDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(EventCallService.class);
    }


    public Result<Boolean> getAttending(int charID){
        try{
            Call<List<Boolean>> call = service.getAttending(charID);
            respAttending = call.execute();
            return new Result.Success<List<Boolean>>(respAttending.body());
        } catch (IOException e){
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface EventCallService {
        @GET("/event/attending/{charID}")
        Call<List<Boolean>> getAttending(int charID);
    }
}
