package com.rbyte.dragernesdal.data.magic.magicSchool;

import com.rbyte.dragernesdal.data.Result;
import com.rbyte.dragernesdal.data.WebServerPointer;
import com.rbyte.dragernesdal.data.magic.magicSchool.model.MagicSchoolDTO;

import java.io.IOException;
import java.util.List;

import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MagicSchoolDAO {
    private Retrofit retrofit;
    private SchoolCallService service;

    Response<MagicSchoolDTO> resp;
    Response<List<MagicSchoolDTO>> resplst;


    public MagicSchoolDAO(){
        {
            this.retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(WebServerPointer.getServerIP())
                    .build();
            this.service = retrofit.create(SchoolCallService.class);
        }
    }

    public Result<List<MagicSchoolDTO>> getSchools(){
        try {
            Call<List<MagicSchoolDTO>> call = service.getAllShools();
            resplst = call.execute();
            if (resplst.code() == 200) return new Result.Success<List<MagicSchoolDTO>>(resplst.body());
            throw new IOException(resplst.message());
        } catch (IOException e){
            e.printStackTrace();
            Sentry.captureException(e);
            return new Result.Error(new IOException(e.getMessage()));
        }
    }

    public interface SchoolCallService {

        @GET("/magic/schools")
        Call<List<MagicSchoolDTO>> getAllShools();

        /*@GET("/race/info/single/{raceID}")
        Call<RaceDTO> getRaceInfo(@Path(value = "raceID") int raceID);

        /*@GET("/race/krys/getCharacterRaces/{characterid}")
        Call<List<RaceDTO>> getKrysRaces(@Path(value = "characterid") int characterid);

        /*@POST("/character/create")
        Call<CharacterDTO> createCharacter(@Body CharacterDTO character);
        */
    }

}
