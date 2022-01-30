package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.touchlab.dogify.data.entities.ApiError;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.retrofit.ApiErrorUtil;
import co.touchlab.dogify.data.retrofit.DogService;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RemoteDataSource implements DataSource {
    private final MutableLiveData<ApiError> mApiError = new MutableLiveData<>();
    private final MutableLiveData<List<BreedModel>> mBreedData = new MutableLiveData<>();

    private final DogService service;
    private final ApiErrorUtil apiErrorUtil;
    private final BreedMapperImpl breedMapper = new BreedMapperImpl();
    
    public RemoteDataSource(Retrofit retrofit, Class<DogService> serviceInterface) {
        this.service = retrofit.create(serviceInterface);
        this.apiErrorUtil = new ApiErrorUtil(retrofit);
    }

    @Override
    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedData;
    }

    @Override
    public LiveData<ApiError> getErrorStream() {
        return mApiError;
    }

    public void fetchBreedData() {
        try {
            Response<NamesResult> response = service.getBreeds().execute();
            if (response.isSuccessful()) {
                NamesResult namesResult = response.body();
                if (namesResult != null) {
                    fetchAllImageUrls(namesResult);
                }
            } else {
                mApiError.postValue(apiErrorUtil.parseError(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchAllImageUrls(NamesResult namesResult) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Callable<ImageResult>> callableList = new ArrayList<>();
        List<Future<ImageResult>> futureList = null;
        List<ImageResult> imageResults = new ArrayList<>();

        List<String> imageUrls = breedMapper.mapImageUrlCallList(namesResult);
        for (String url : imageUrls) {
            callableList.add(() -> fetchOneImageUrl(url));
        }

        try {
            futureList = executorService.invokeAll(callableList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        if (futureList != null) {
            for (Future<ImageResult> future : futureList) {
                try {
                    imageResults.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        mBreedData.postValue(breedMapper.mapBreedEntitiesToModel(namesResult, imageResults));
    }

    private ImageResult fetchOneImageUrl(String imageUrl) {
        try {
            Response<ImageResult> response = service.getImage(imageUrl).execute();
            if (response.isSuccessful()) {
                ImageResult imageResult = response.body();
                if (imageResult != null) {
                    return imageResult;
                }
            } else {
                mApiError.postValue(apiErrorUtil.parseError(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

