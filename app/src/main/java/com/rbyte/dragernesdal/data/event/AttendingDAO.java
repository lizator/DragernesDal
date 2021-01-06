package com.rbyte.dragernesdal.data.event;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.character.model.CharacterDTO;
import com.rbyte.dragernesdal.data.event.model.AttendingDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class AttendingDAO {
    private Retrofit retrofit;
    private EventCallService service;

    Response<List<AttendingDTO>> respAttending;

    public AttendingDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(EventCallService.class);
    }


    public Result<AttendingDTO> getAttending(int charID){
        try{
            Call<List<AttendingDTO>> call = service.getAttending(charID);
            respAttending = call.execute();
            return new Result.Success<List<AttendingDTO>>(respAttending.body());
        } catch (IOException e){
            return new Result.Error(new IOException("Error connection to database"));
        }
    }



    public interface EventCallService {
        @GET("/event/attending/{charID}")
        Call<List<AttendingDTO>> getAttending(@Path(value = "charID")int charID);
        }
}
