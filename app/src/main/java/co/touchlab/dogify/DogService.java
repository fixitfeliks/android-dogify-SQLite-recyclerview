package co.touchlab.dogify;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DogService
{
    @GET("breeds/list")
    Call<NameResult> getBreeds();

    @GET("breed/{name}/images/random")
    Call<ImageResult> getImage(@Path("name") String name);
}
