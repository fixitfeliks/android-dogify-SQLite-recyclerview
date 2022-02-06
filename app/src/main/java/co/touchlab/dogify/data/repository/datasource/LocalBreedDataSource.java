package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Comparator;
import java.util.List;

import co.touchlab.dogify.data.db.DogifyDb;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.retrofit.ApiErrorUtil;
import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;

public class LocalBreedDataSource implements DataSource
{
    private final DogifyDb mDogifyDb;
    private final MutableLiveData<List<BreedModel>> mBreedModels = new MutableLiveData<>();
    private final MutableLiveData<ErrorResult> mError = new MutableLiveData<>();
    private final BreedMapperImpl breedMapper = new BreedMapperImpl();

    public LocalBreedDataSource(DogifyDb dogifyDb) {
        mDogifyDb = dogifyDb;
    }

    @Override
    public LiveData<List<BreedModel>> getBreedDataStream() {
        return mBreedModels;
    }

    @Override
    public LiveData<ErrorResult> getErrorStream() {
        return mError;
    }

    public void fetchBreedData() {
        List<BreedModel> breedModelList = null;
        try {
            breedModelList = mDogifyDb.breedDao().getAllBreeds();
            breedModelList.sort(Comparator.comparing(breedModel ->
                    breedMapper.geListSortString(breedModel.displayName)
            ));
            mBreedModels.postValue(breedModelList);
        } catch (Exception e) {
            mError.postValue(ApiErrorUtil.postErrorResult("error", e.getMessage()));
        }
    }

    public void storeBreedModels(List<BreedModel> breedModels) {
        try {
            mDogifyDb.breedDao().insertBreeds(breedModels);
        } catch (Exception e) {
            mError.postValue(ApiErrorUtil.postErrorResult("error", e.getMessage()));
        }
    }
}
