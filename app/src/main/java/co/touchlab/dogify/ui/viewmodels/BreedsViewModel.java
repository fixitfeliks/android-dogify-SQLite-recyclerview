package co.touchlab.dogify.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.Constants;
import co.touchlab.dogify.data.db.DogifyDb;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.BreedRepositoryImpl;
import co.touchlab.dogify.data.repository.datasource.LocalBreedDataSource;
import co.touchlab.dogify.data.repository.datasource.RemoteBreedDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.data.retrofit.RetrofitFactory;

public class BreedsViewModel extends AndroidViewModel
{
    private final BreedRepositoryImpl mBreedRepository;
    private final BreedMapperImpl mBreedMapper;
    private final ExecutorService mExecutorService;
    private final LocalBreedDataSource mLocalBreedDataSource;
    private final RemoteBreedDataSource mRemoteBreedDataSource;
    private final DogifyDb mDogifyDb;


    public BreedsViewModel(Application application) {
        super(application);
        mExecutorService = Executors.newCachedThreadPool();
        mBreedMapper = new BreedMapperImpl();
        mRemoteBreedDataSource = new RemoteBreedDataSource(
                Glide.with(getApplication().getApplicationContext()),
                RetrofitFactory.getNewRetrofit(Constants.DOG_API_BASE_URL), DogService.class);
        mDogifyDb = DogifyDb.getInstance(getApplication().getApplicationContext());
        mLocalBreedDataSource = new LocalBreedDataSource(mDogifyDb);
        mBreedRepository = new BreedRepositoryImpl(mRemoteBreedDataSource, mLocalBreedDataSource);
    }

    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedRepository.getBreedDataStream();
    }

    public LiveData<ErrorModel> getErrorStream() {
        return mBreedRepository.getErrorStream();
    }

    public void fetchBreedData() {
        mExecutorService.execute(mBreedRepository::fetchBreedData);
    }
}
