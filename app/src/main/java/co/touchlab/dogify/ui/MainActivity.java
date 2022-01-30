package co.touchlab.dogify.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.Constants;
import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.repository.BreedRepositoryImpl;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.di.RetrofitFactory;
import co.touchlab.dogify.ui.viewmodels.BreedsViewModel;

public class MainActivity extends AppCompatActivity
{
    private ProgressBar mSpinner;
    private BreedAdapter mBreedAdapter;
    private RecyclerView mBreedList;
    private BreedMapperImpl mBreedMapper;
    private RemoteDataSource mRemoteDataSource;
    private BreedRepositoryImpl mBreedRepository;
    private BreedsViewModel mBreedsViewModel;
    private ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner = findViewById(R.id.intro_spinner);

        // RecyclerView
        mBreedAdapter = new BreedAdapter();
        mBreedList = findViewById(R.id.breed_list);
        mBreedList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mBreedList.setAdapter(mBreedAdapter);

        // Repository
        mBreedMapper = new BreedMapperImpl();
        mRemoteDataSource = new RemoteDataSource(RetrofitFactory.getRetrofit(Constants.DOG_API_BASE_URL), DogService.class);
        mBreedRepository = new BreedRepositoryImpl(mRemoteDataSource, mBreedMapper);

        // View Model
        mBreedsViewModel = new BreedsViewModel(mBreedRepository);
        mBreedsViewModel.getBreedDataStream().observe(this, breedModels -> {
            mSpinner.setVisibility(View.GONE);
            mBreedAdapter.addAll(breedModels);
        });
        mBreedsViewModel.getErrorStream().observe(this, errorModel -> System.out.println("Ok"));

        // Thread Pool
        mExecutorService = Executors.newCachedThreadPool();
        mExecutorService.execute(() -> mBreedsViewModel.fetchBreedData());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
