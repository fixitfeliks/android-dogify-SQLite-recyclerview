package co.touchlab.dogify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;

public class BreedRepositoryImpl implements BreedRepository
{
    private final MediatorLiveData<ErrorModel> mRepoErrorData = new MediatorLiveData<>();
    private final MediatorLiveData<List<BreedModel>> mBreedData = new MediatorLiveData<>();

    private final ExecutorService mExecutor;
    private final RemoteDataSource mRemoteDataSource;

    public BreedRepositoryImpl(RemoteDataSource mRemoteDataSource, BreedMapperImpl mBreedMapper) {
        this.mRemoteDataSource = mRemoteDataSource;
        mExecutor = Executors.newCachedThreadPool();

        mRepoErrorData.addSource(this.mRemoteDataSource.getErrorStream(), apiError ->
                mExecutor.execute(() -> {
                    mRepoErrorData.postValue(mBreedMapper.mapErrorEntityToModel(apiError));
                })
        );

        mBreedData.addSource(this.mRemoteDataSource.getBreedDataStream(), breedModels ->
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
        mRemoteDataSource.fetchBreedData();
    }
}
