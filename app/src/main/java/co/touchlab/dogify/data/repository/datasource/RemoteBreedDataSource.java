package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.data.retrofit.resultmodels.ImageResult;
import co.touchlab.dogify.data.retrofit.resultmodels.NamesResult;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.retrofit.ApiErrorUtil;
import co.touchlab.dogify.data.retrofit.DogService;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RemoteBreedDataSource implements DataSource {
    private final MutableLiveData<ErrorResult> mApiError = new MutableLiveData<>();
    private final MutableLiveData<List<BreedModel>> mBreedData = new MutableLiveData<>();

    private final Gson gson = new Gson();
    private final DogService mDogService;
    private final ApiErrorUtil apiErrorUtil;
    private final BreedMapperImpl breedMapper = new BreedMapperImpl();
    private final RequestManager mGlide;

    public RemoteBreedDataSource(RequestManager glide, Retrofit retrofit, Class<DogService> serviceInterface) {
        this.mGlide = glide;
        this.mDogService = retrofit.create(serviceInterface);
        this.apiErrorUtil = new ApiErrorUtil(retrofit);
    }

    @Override
    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedData;
    }

    @Override
    public LiveData<ErrorResult> getErrorStream() {
        return mApiError;
    }

    public void fetchBreedData() {
        Response<NamesResult> response = null;
        try {
            response = mDogService.getBreeds().execute();
        } catch (IOException e) {
            mApiError.postValue(ApiErrorUtil.postErrorResult("error", e.getMessage()));
            return;
        }
        if (response != null && response.code() == 200 && response.body() != null) {
            NamesResult namesResult = response.body();
            if (namesResult != null && namesResult.status != null) {
                fetchAllImageUrls(namesResult);
                return;
            }
        }
        if (response != null && response.code() >= 300 && response.errorBody() != null) {
            ErrorResult errorResult = gson.fromJson(response.errorBody().charStream(), ErrorResult.class);
            if (errorResult != null) {
                mApiError.postValue(errorResult);
                return;
            }
        }

        mApiError.postValue(apiErrorUtil.parseError(response));
    }

    private void fetchAllImageUrls(NamesResult namesResult) {
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

        for (String url : imageUrls) {
            callableList.add(() -> fetchOneImageUrl(url));
        }

        List<BreedModel> breedModels = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);
        mBreedData.postValue(breedModels);
        fetchAndCacheImages(breedModels);
    }

    private ImageResult fetchOneImageUrl(String imageUrl) {
        try {
            Response<ImageResult> response = mDogService.getImage(imageUrl).execute();
            if (response.isSuccessful()) {
                ImageResult imageResult = response.body();
                if (imageResult != null ) {
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

    public void fetchAndCacheImages(List<BreedModel> breedModels) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            for (BreedModel breedModel : breedModels) {
                mGlide
                        .asBitmap()
                        .load(breedModel.imageUrl)
                        .submit();
            }
        },2, TimeUnit.SECONDS);

        scheduler.shutdown();
    }
}

