package co.touchlab.dogify.data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;

public interface BreedRepository {
    LiveData<List<BreedModel>> getBreedDataStream();
    LiveData<ErrorModel> getErrorStream();
    void fetchBreedData();
}
