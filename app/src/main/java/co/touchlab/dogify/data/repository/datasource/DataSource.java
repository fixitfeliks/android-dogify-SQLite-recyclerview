package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;

import java.util.List;

import co.touchlab.dogify.data.entities.ApiError;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.models.BreedModel;

public interface DataSource {
    LiveData<List<BreedModel>> getBreedDataStream();
    LiveData<ApiError> getApiErrorStream();
    void fetchBreedData();
}
