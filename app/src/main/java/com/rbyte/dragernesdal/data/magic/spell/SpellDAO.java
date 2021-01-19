package com.rbyte.dragernesdal.data.magic.spell;

import android.util.Log;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.inventory.model.InventoryDTO;
import com.rbyte.dragernesdal.data.magic.spell.model.SpellDTO;
import com.rbyte.dragernesdal.data.race.model.RaceDTO;

import java.io.IOException;
import java.util.List;

import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class SpellDAO {
    private Retrofit retrofit;
    private SpellCallService service;

    Response<SpellDTO> resp;
    Response<List<SpellDTO>> resplst;


    public SpellDAO(){
        {
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(WebServerPointer.getServerIP())
                    .build();
            this.service = retrofit.create(SpellCallService.class);
        }
    }

    public Result<List<SpellDTO>> getSpells(){
        try {
            Call<List<SpellDTO>> call = service.getAllSpells();
            resplst = call.execute();
            if (resplst.code() == 200) return new Result.Success<List<SpellDTO>>(resplst.body());
            throw new IOException(resplst.message());
        } catch (IOException e){
            Sentry.captureException(e);
            e.printStackTrace();
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public interface SpellCallService {

        @GET("/magic/spells")
        Call<List<SpellDTO>> getAllSpells();

        /*@GET("/race/info/single/{raceID}")
        Call<RaceDTO> getRaceInfo(@Path(value = "raceID") int raceID);

        /*@GET("/race/krys/getCharacterRaces/{characterid}")
        Call<List<RaceDTO>> getKrysRaces(@Path(value = "characterid") int characterid);

        /*@POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);
        */
    }

}
