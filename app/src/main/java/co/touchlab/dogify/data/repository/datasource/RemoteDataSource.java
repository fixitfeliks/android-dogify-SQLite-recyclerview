package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import co.touchlab.dogify.data.entities.ApiError;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.retrofit.ApiErrorUtil;
import co.touchlab.dogify.data.retrofit.DogService;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RemoteDataSource implements DataSource {
    private final MutableLiveData<ApiError> mApiError = new MutableLiveData<>();
    private final MutableLiveData<NamesResult> mBreedNamesResult = new MutableLiveData<>();
    private final MutableLiveData<ImageResult> mBreedImageResult = new MutableLiveData<>();

    private final Retrofit retrofit;
    private final DogService service;
    private final ApiErrorUtil apiErrorUtil;
    
    public RemoteDataSource(Retrofit retrofit, Class<DogService> serviceInterface) {
        this.retrofit = retrofit;
        this.service = retrofit.create(serviceInterface);
        this.apiErrorUtil = new ApiErrorUtil(retrofit);
    }

    @Override
    public LiveData<NamesResult> getBreedNames() {
        return mBreedNamesResult;
    }

    @Override
    public LiveData<ImageResult> getBreedImage() {
        return mBreedImageResult;
    }

    @Override
    public LiveData<ApiError> getApiError() {
        return mApiError;
    }

    public void fetchBreedNames() {
        try {
            Response<NamesResult> response = service.getBreeds().execute();
            if (response.isSuccessful()) {
                NamesResult namesResult = response.body();
                if (namesResult != null) {
                    mBreedNamesResult.postValue(namesResult);
                }
            } else {
                ApiError apiError = apiErrorUtil.parseError(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

