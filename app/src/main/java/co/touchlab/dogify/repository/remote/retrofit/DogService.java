package co.touchlab.dogify.repository.remote.retrofit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DogService
{
    @GET("breeds/list/all")
    Call<NameResult> getBreeds();

    @GET("breed/{name}/images/random")
    Call<ImageResult> getImage(@Path("name") String name);
}
