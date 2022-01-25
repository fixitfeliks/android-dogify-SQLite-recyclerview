package co.touchlab.dogify.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.data.retrofit.GetBreedsTask;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView breedList;
    private BreedAdapter adapter   = new BreedAdapter();
    private GetBreedsTask getBreeds = new GetBreedsTask(this);
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.spinner);
        breedList = findViewById(R.id.breed_list);
        breedList.setLayoutManager(new GridLayoutManager(this, 2));
        breedList.setAdapter(adapter);
        getBreeds.execute();
        ExecutorService mExecutor = Executors.newFixedThreadPool(5);
        mExecutor.execute(() -> testNewDataSource());
    }

    @Override
    protected void onDestroy()
    {
        getBreeds.cancel(false);
        super.onDestroy();
    }

    public void testNewDataSource() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://dog.ceo/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RemoteDataSource dataSource = new RemoteDataSource(retrofit, DogService.class);
        dataSource.fetchBreedNames();
    }

    public void initList() {
        showSpinner(true);
        adapter.clear();
    }

    public void addAllBreedsToList(List<String> breeds) {
        showSpinner(false);
        adapter.addAll(breeds);
    }

    public void showSpinner(Boolean show)
    {
        spinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
