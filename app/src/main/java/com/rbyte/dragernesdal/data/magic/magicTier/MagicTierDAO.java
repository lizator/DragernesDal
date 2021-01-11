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

    public interface TierCallService {

        @GET("/magic/tiers")
        Call<List<MagicTierDTO>> getAllTiers();

        /*@GET("/race/info/single/{raceID}")
        Call<RaceDTO> getRaceInfo(@Path(value = "raceID") int raceID);

        /*@GET("/race/krys/getCharacterRaces/{characterid}")
        Call<List<RaceDTO>> getKrysRaces(@Path(value = "characterid") int characterid);

        /*@POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);
        */
    }

}
