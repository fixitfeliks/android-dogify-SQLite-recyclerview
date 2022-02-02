package co.touchlab.dogify.data.retrofit;

import co.touchlab.dogify.data.retrofit.resultmodels.ImageResult;
import co.touchlab.dogify.data.retrofit.resultmodels.NamesResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DogService
{
    @GET("breeds/list/all")
    Call<NamesResult> getBreeds();

    @GET("breed/{name}/images/random")
    Call<ImageResult> getImage(@Path(value = "name", encoded = true) String name);
}
