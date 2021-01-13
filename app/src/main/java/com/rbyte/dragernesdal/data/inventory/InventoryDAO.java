package com.rbyte.dragernesdal.data.inventory;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class InventoryDAO {
    private Retrofit retrofit;
    private InventoryCallService service;

    Response<InventoryDTO> resp;
    Response<String> respString;
    Response<Boolean> respBool;
    Response<List<InventoryDTO>> respList;

    public InventoryDAO(){
        this.retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WebServerPointer.getServerIP())
                .build();
        this.service = retrofit.create(InventoryCallService.class);
    }

    public Result<List<InventoryDTO>> getActualInventoryByCharacterID(int characterID){
        try {
            Call<List<InventoryDTO>> call = service.getActualByCharacterID(characterID);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<InventoryDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public Result<List<InventoryDTO>> getCurrentInventoryByCharacterID(int characterID){
        try {
            Call<List<InventoryDTO>> call = service.getCurrentByCharacterID(characterID);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<InventoryDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }

    public String getState(int relationid){
        try {
            Call<String> call = service.getState(relationid);
            respString = call.execute();
            if (respString.code() == 200) return respString.body();
            return "Error";
        } catch (IOException e){
            e.printStackTrace();
            return "Error";
        }
    }

    public Result<List<InventoryDTO>> saveInventory(int characterID, ArrayList<InventoryDTO> inventory){
        try {
            Call<List<InventoryDTO>> call = service.saveInventory(characterID, inventory);
            respList = call.execute();
            if (respList.code() == 200) return new Result.Success<List<InventoryDTO>>(respList.body());
            return new Result.Error(new IOException(respList.message()));
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException("Error connection to database"));
        }
    }


    public interface InventoryCallService {
        @GET("/inventory/actualByCharacterID/{characterid}")
        Call<List<InventoryDTO>> getActualByCharacterID(@Path(value = "characterid") int characterid);

        @GET("/inventory/currentByCharacterID/{characterid}")
        Call<List<InventoryDTO>> getCurrentByCharacterID(@Path(value = "characterid") int characterid);

        @GET("/inventory/state/{relationid}")
        Call<String> getState(@Path(value = "relationid") int relationid);

        @POST("/inventory/save/{characterID}")
        Call<List<InventoryDTO>> saveInventory(@Path(value = "characterID") int characterid, @Body ArrayList<InventoryDTO> inventory);

        @GET("/inventory/deny/{relationid}")
        Call<Boolean> deny(@Path(value = "relationid") int relationid);

        @GET("/inventory/denyall")
        Call<Boolean> denyAll();

        @GET("/inventory/confirm/{characterid}")
        Call<Boolean> confirm(@Path(value = "characterid") int characterid);
    }
}
