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

import co.touchlab.dogify.Constants;
import co.touchlab.dogify.R;
import co.touchlab.dogify.adapters.BreedAdapter;
import co.touchlab.dogify.data.mappers.BreedMapper;
import co.touchlab.dogify.data.repository.BreedRepositoryImpl;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.di.RetrofitFactory;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView mBreedList;
    private BreedAdapter mBreedAdapter;
    private BreedMapper mBreedMapper;
    private ProgressBar mSpinner;
    private RemoteDataSource mRemoteDataSource;
    private BreedRepositoryImpl mBreedRepository;
    private ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner = findViewById(R.id.spinner);
        mBreedAdapter = new BreedAdapter();
        mBreedList = findViewById(R.id.breed_list);
        mBreedList.setLayoutManager(new GridLayoutManager(this, 2));
        mBreedList.setAdapter(mBreedAdapter);

        mBreedMapper = new BreedMapper();
        mRemoteDataSource = new RemoteDataSource(RetrofitFactory.getRetrofit(Constants.DOG_API_BASE_URL), DogService.class);
        mBreedRepository = new BreedRepositoryImpl(mRemoteDataSource, mBreedMapper);
        mBreedRepository.getBreedDataStream().observe(this, breedModels -> System.out.println("Ok"));
        mBreedRepository.getErrorStream().observe(this, errorModel -> System.out.println("Ok"));

        mExecutorService = Executors.newCachedThreadPool();
        mExecutorService.execute(() -> mBreedRepository.fetchBreedData());

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void initList() {
        showSpinner(true);
        mBreedAdapter.clear();
    }

    public void addAllBreedsToList(List<String> breeds) {
        showSpinner(false);
        mBreedAdapter.addAll(breeds);
    }

    public void showSpinner(Boolean show)
    {
        mSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
