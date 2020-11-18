package com.example.dragernesdal.data;




import com.example.dragernesdal.data.ability.model.AbilityDTO;
import com.example.dragernesdal.ui.main.MainActivity;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainDAO {
    private Retrofit retrofit;
    private MainCallService service;

    Response<String> resp;

    public MainDAO(){
        {
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    //.baseUrl("http://10.16.234.21:25572")
                    .baseUrl("http://80.197.112.212:25572")
                    .build();
            this.service = retrofit.create(MainCallService.class);
        }
    }

    public Result<String> getAbilitiesByCharacterID(String tableName){
        try {
            Call<String> call = service.getTableLastModified(tableName);
            resp = call.execute();
            return new Result.Success<String>(resp.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public interface MainCallService {
        @GET("/main/tableupdate/{tableName}")
        Call<String> getTableLastModified(@Path(value = "tableName") String tableName);

        /*@GET("/character/byUserID/{userid}")
        Call<List<CharacterDTO>> getByUserID(@Path(value = "userid") int userid);

        @POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);*/
    }
}
