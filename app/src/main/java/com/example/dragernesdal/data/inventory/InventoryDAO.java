package com.example.dragernesdal.data.inventory;

import com.example.dragernesdal.data.Result;
import com.example.dragernesdal.data.WebServerPointer;
import com.example.dragernesdal.data.inventory.model.InventoryDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class InventoryDAO {
    private Retrofit retrofit;
    private InventoryCallService service;

    Response<InventoryDTO> resp;
    Response<List<InventoryDTO>> respList;

    public InventoryDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(InventoryCallService.class);
    }

    public Result<List<InventoryDTO>> getInventoryByCharacterID(int characterID){
        try {
            Call<List<InventoryDTO>> call = service.getByCharacterID(characterID);
            respList = call.execute();
            return new Result.Success<List<InventoryDTO>>(respList.body());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface InventoryCallService {
        @GET("/inventory/byCharacterID/{characterid}")
        Call<List<InventoryDTO>> getByCharacterID(@Path(value = "characterid") int characterid);

        /*@GET("/character/byUserID/{userid}")
        Call<List<CharacterDTO>> getByUserID(@Path(value = "userid") int userid);

        @POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);*/
    }
}
