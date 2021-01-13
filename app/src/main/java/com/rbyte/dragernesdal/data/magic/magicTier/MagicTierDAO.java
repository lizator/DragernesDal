package com.rbyte.dragernesdal.data.magic.magicTier;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.magic.magicTier.model.MagicTierDTO;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MagicTierDAO {
    private Retrofit retrofit;
    private TierCallService service;

    Response<MagicTierDTO> resp;
    Response<List<MagicTierDTO>> resplst;


    public MagicTierDAO(){
        {
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(WebServerPointer.getServerIP())
                    .build();
            this.service = retrofit.create(TierCallService.class);
        }
    }

    public Result<List<MagicTierDTO>> getTiers(){
        try {
            Call<List<MagicTierDTO>> call = service.getAllTiers();
            resplst = call.execute();
            if (resplst.code() == 200) return new Result.Success<List<MagicTierDTO>>(resplst.body());
            throw new IOException(resplst.message());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public Result<List<MagicTierDTO>> getTiersByCharacterID(int characterID){
        try {
            Call<List<MagicTierDTO>> call = service.getTiersByCharacterID(characterID);
            resplst = call.execute();
            if (resplst.code() == 200) return new Result.Success<List<MagicTierDTO>>(resplst.body());
            throw new IOException(resplst.message());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public Result<MagicTierDTO> buyTier(int characterID, int tierID, int cost){
        try {
            Call<MagicTierDTO> call = service.buyTier(characterID, tierID, cost);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<MagicTierDTO>(resp.body());
            throw new IOException(resplst.message());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public Result<MagicTierDTO> getFreeTier(int characterID, int tierID){
        try {
            Call<MagicTierDTO> call = service.getFreeTier(characterID, tierID);
            resp = call.execute();
            if (resp.code() == 200) return new Result.Success<MagicTierDTO>(resp.body());
            throw new IOException(resplst.message());
        } catch (IOException e){
            e.printStackTrace();
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public interface TierCallService {

        @GET("/magic/tiers")
        Call<List<MagicTierDTO>> getAllTiers();

        @GET("/magic/bycharid/{characterID}")
        Call<List<MagicTierDTO>> getTiersByCharacterID(@Path(value = "characterID") int characterID);

        @GET("/magic/buy/{characterid}/{tierid}/{cost}")
        Call<MagicTierDTO> buyTier(@Path(value = "characterid") int characterid, @Path(value = "tierid") int tierid, @Path(value = "cost") int cost);

        @GET("/magic/getfree/{characterid}/{tierid}")
        Call<MagicTierDTO> getFreeTier(@Path(value = "characterid") int characterid, @Path(value = "tierid") int tierid);

        /*@POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);
        */
    }

}
