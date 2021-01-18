package com.rbyte.dragernesdal.data.inventory;

import android.util.Log;

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
            Call<InventoryDTO> call = service.getState(relationid);
            resp = call.execute();
            if (resp.code() == 200) return resp.body().getItemName();
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

    public boolean deny(int charid){
        try {
            Call<InventoryDTO> call = service.deny(charid);
            resp = call.execute();
            if (resp.code() == 200 ){
                if(resp.body() != null) return resp.body().getAmount() == 1;
            }
            Log.d("Inventory", "deny: error in data from backend: " + respBool.message());
            return false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean denyAll(){
        try {
            Call<InventoryDTO> call = service.denyAll();
            resp = call.execute();
            if (resp.code() == 200) return resp.body().getAmount() == 1;
            Log.d("Inventory", "denyAll: error in data from backend: " + respBool.message());
            return false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean confirm(int relationid){
        try {
            Call<InventoryDTO> call = service.confirm(relationid);
            resp = call.execute();
            if (resp.code() == 200){
                if(resp.body() != null)
                return resp.body().getAmount() == 1;
            }
            Log.d("Inventory", "confirm: error in data from backend: ");
            return false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }


    public interface InventoryCallService {
        @GET("/inventory/actualByCharacterID/{characterid}")
        Call<List<InventoryDTO>> getActualByCharacterID(@Path(value = "characterid") int characterid);

        @GET("/inventory/currentByCharacterID/{characterid}")
        Call<List<InventoryDTO>> getCurrentByCharacterID(@Path(value = "characterid") int characterid);

        @GET("/inventory/state/{relationid}")
        Call<InventoryDTO> getState(@Path(value = "relationid") int relationid);

        @POST("/inventory/save/{characterID}")
        Call<List<InventoryDTO>> saveInventory(@Path(value = "characterID") int characterid, @Body ArrayList<InventoryDTO> inventory);

        @GET("/inventory/deny/{charid}")
        Call<InventoryDTO> deny(@Path(value = "charid") int charid);

        @GET("/inventory/denyall")
        Call<InventoryDTO> denyAll();

        @GET("/inventory/confirm/{relationid}")
        Call<InventoryDTO> confirm(@Path(value = "relationid") int relationid);
    }
}
