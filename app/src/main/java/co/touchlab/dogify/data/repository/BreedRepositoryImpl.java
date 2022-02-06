package co.touchlab.dogify.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.datasource.LocalBreedDataSource;
import co.touchlab.dogify.data.repository.datasource.RemoteBreedDataSource;

public class BreedRepositoryImpl implements BreedRepository
{
    private final MediatorLiveData<ErrorModel> mErrorData = new MediatorLiveData<>();
    private final MediatorLiveData<List<BreedModel>> mBreedData = new MediatorLiveData<>();

    private final ExecutorService mExecutor;
    private final LocalBreedDataSource mLocalBreedDataSource;
    private final RemoteBreedDataSource mRemoteBreedDataSource;

    public BreedRepositoryImpl(RemoteBreedDataSource remoteBreedDataSource, LocalBreedDataSource localBreedDataSource) {
        this.mRemoteBreedDataSource = remoteBreedDataSource;
        this.mLocalBreedDataSource = localBreedDataSource;
        mExecutor = Executors.newCachedThreadPool();

        mBreedData.addSource(this.mRemoteBreedDataSource.getBreedDataStream(), breedModels ->
                mExecutor.execute(() -> {
                        mBreedData.postValue(breedModels);
                        mLocalBreedDataSource.storeBreedModels(breedModels);
                })
        );

        mErrorData.addSource(this.mRemoteBreedDataSource.getErrorStream(), errorResult ->
                mExecutor.execute(() -> {
                    ErrorModel errorModel = new ErrorModel();
                    errorModel.status = errorResult.status;
                    errorModel.message = errorResult.message;
                    mErrorData.postValue(errorModel);
                    localBreedDataSource.fetchBreedData();
                })
        );

        mBreedData.addSource(this.mLocalBreedDataSource.getBreedDataStream(), breedModels ->
                mExecutor.execute(() -> {
                    mBreedData.postValue(breedModels);
                })
        );

        mErrorData.addSource(this.mLocalBreedDataSource.getErrorStream(), errorResult ->
                mExecutor.execute(() -> {
                    ErrorModel errorModel = new ErrorModel();
                    errorModel.status = errorResult.status;
                    errorModel.message = errorResult.message;
                    mErrorData.postValue(errorModel);
                })
        );
    }

    @Override
    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedData;
    }

    @Override
    public LiveData<ErrorModel> getErrorStream() {
        return mErrorData;
    }

    @Override
    public void fetchBreedData() {
        mRemoteBreedDataSource.fetchBreedData();
    }
}
