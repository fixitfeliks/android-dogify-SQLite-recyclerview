package co.touchlab.dogify.data.repository.datasource;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import co.touchlab.dogify.data.db.BreedModelEntity;
import co.touchlab.dogify.data.db.DogifyDb;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;

public class LocalDataSource implements DataSource
{
    private final static String FILEPATH_IMAGE_FOLDER = ".images";
    private final Context mAppContext;
    private final DogifyDb mDogifyDb;
//    private final MutableLiveData<List<BreedModel>> mBreedModels = new MutableLiveData<>();
    private final MutableLiveData<ErrorResult> mError = new MutableLiveData<>();


    public LocalDataSource(Context mAppContext) {
        this.mAppContext = mAppContext;
        mDogifyDb = DogifyDb.getInstance(mAppContext);
    }

    @Override
    public LiveData<List<BreedModel>> getBreedDataStream() {
        return null;
    }

    @Override
    public LiveData<ErrorResult> getErrorStream() {
        return mError;
    }

    @Override
    public void fetchBreedData() {
        List<BreedModelEntity> breedModelEntitiesList = mDogifyDb.breedDao().getAllBreeds();
        List<BreedModel> breedModelList = new ArrayList<>();

        breedModelEntitiesList.stream().forEach(breedModelEntity -> {
            BreedModel breedModel = new BreedModel(breedModelEntity.displayName, breedModelEntity.imageUrl);
            breedModelList.add(breedModel);
        });
        int i = 0;
    }

    public void storeBreedModels(List<BreedModel> breedModels) {
        int i = 0;
        List<BreedModelEntity> breedModelEntitiesList = new ArrayList<>();

        breedModels.stream().forEach(breedModel -> {
            BreedModelEntity breedModelEntity = new BreedModelEntity(breedModel.displayName, breedModel.imageUrl);
            breedModelEntitiesList.add(breedModelEntity);
        });
        mDogifyDb.breedDao().insertBreeds(breedModelEntitiesList);
    }
}
