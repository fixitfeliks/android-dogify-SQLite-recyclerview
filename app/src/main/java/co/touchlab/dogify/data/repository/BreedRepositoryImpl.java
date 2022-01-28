package co.touchlab.dogify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.data.mappers.BreedMapper;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;

public class BreedRepositoryImpl implements BreedRepository
{
    private final MediatorLiveData<ErrorModel> mRepoErrorData = new MediatorLiveData<>();
    private final MediatorLiveData<List<BreedModel>> mBreedData = new MediatorLiveData<>();

    private final ExecutorService mExecutor;
    private final RemoteDataSource remoteDataSource;
    private final BreedMapper breedMapper;

    public BreedRepositoryImpl(RemoteDataSource remoteDataSource, BreedMapper breedMapper) {
        this.remoteDataSource = remoteDataSource;
        this.breedMapper = breedMapper;
        mExecutor = Executors.newCachedThreadPool();

        mRepoErrorData.addSource(this.remoteDataSource.getApiErrorStream(), apiError ->
                mExecutor.execute(() -> {
                    mRepoErrorData.postValue(breedMapper.mapErrorEntityToModel(apiError));
                })
        );

        mBreedData.addSource(this.remoteDataSource.getBreedDataStream(), breedModels ->
                mExecutor.execute(() -> {
                        mBreedData.postValue(breedModels);
                })
        );
    }

    @Override
    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedData;
    }

    @Override
    public LiveData<ErrorModel> getErrorStream() {
        return mRepoErrorData;
    }

    @Override
    public void fetchBreedData() {
        remoteDataSource.fetchBreedData();
    }
}
