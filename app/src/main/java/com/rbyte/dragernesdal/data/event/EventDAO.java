package com.rbyte.dragernesdal.data.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.event.model.EventDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class EventDAO {
    private Retrofit retrofit;
    private EventCallService service;

    Response<List<EventDTO>> respList;
    Response<EventDTO> resp;

    public EventDAO(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
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


    public Result<List<EventDTO>> createEvent(EventDTO eventDTO){
        try {
            Call<EventDTO> call = service.createEvent(eventDTO);
            resp = call.execute();
            return new Result.Success<EventDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<EventDTO>> editEvent(EventDTO eventDTO){
        try {
            Call<EventDTO> call = service.editEvent(eventDTO);
            resp = call.execute();
            return new Result.Success<EventDTO>(resp.body());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public interface EventCallService {
        @GET("/event/events")
        Call<List<EventDTO>> getEvents();
        @POST("/event/create")
        Call<EventDTO> createEvent(@Body EventDTO event);
        @POST("/event/edit")
        Call<EventDTO> editEvent(@Body EventDTO event);
    }

}
