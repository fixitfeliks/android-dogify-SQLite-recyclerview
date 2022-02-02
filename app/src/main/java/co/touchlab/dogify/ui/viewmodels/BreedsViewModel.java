package co.touchlab.dogify.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import co.touchlab.dogify.Constants;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.BreedRepositoryImpl;
import co.touchlab.dogify.data.repository.datasource.LocalDataSource;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.data.retrofit.RetrofitFactory;

public class BreedsViewModel extends ViewModel
{
    private final RemoteDataSource mRemoteDataSource;
    private LocalDataSource mLocalDataSource;
    private BreedRepositoryImpl mBreedRepository;
    private BreedMapperImpl mBreedMapper;

    public BreedsViewModel(Context context) {
        mBreedMapper = new BreedMapperImpl();
        mRemoteDataSource = new RemoteDataSource(RetrofitFactory.getNewRetrofit(Constants.DOG_API_BASE_URL), DogService.class);
        mLocalDataSource = new LocalDataSource(context);
        mBreedRepository = new BreedRepositoryImpl(mRemoteDataSource, mLocalDataSource, mBreedMapper);
    }

    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedRepository.getBreedDataStream();
    }

    public LiveData<ErrorModel> getErrorStream() {
        return mBreedRepository.getErrorStream();
    }

    public void fetchBreedData() {
        mBreedRepository.fetchBreedData();
    }

}
