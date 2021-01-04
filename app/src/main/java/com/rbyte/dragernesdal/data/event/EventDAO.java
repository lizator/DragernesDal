package com.rbyte.dragernesdal.data.event;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.event.model.EventDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class EventDAO {
    private Retrofit retrofit;
    private EventCallService service;

    Response<EventDTO> resp;
    Response<List<EventDTO>> respList;

    public EventDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(EventCallService.class);
    }

    public Result<List<EventDTO>> getEvents(){
        try {
            Call<List<EventDTO>> call = service.getEvents();
            respList = call.execute();
            return new Result.Success<List<EventDTO>>(respList.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface EventCallService {
        @GET("/event/events")
        Call<List<EventDTO>> getEvents();

    }
}
