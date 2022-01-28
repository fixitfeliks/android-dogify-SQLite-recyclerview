package co.touchlab.dogify.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.Constants;
import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.data.mappers.BreedMapper;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.repository.BreedRepositoryImpl;
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
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.DOG_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        BreedMapper breedMapper = new BreedMapper();
        RemoteDataSource dataSource = new RemoteDataSource(retrofit, DogService.class);
        BreedRepositoryImpl breedRepository = new BreedRepositoryImpl(dataSource, breedMapper);

        breedRepository.getBreedDataStream().observe(this, new Observer<List<BreedModel>>() {
            @Override
            public void onChanged(List<BreedModel> breedModels) {
                System.out.println("Ok");
            }
        });

        mExecutor.execute(() -> breedRepository.fetchBreedData());

    }

    @Override
    protected void onDestroy()
    {
        getBreeds.cancel(false);
        super.onDestroy();
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
