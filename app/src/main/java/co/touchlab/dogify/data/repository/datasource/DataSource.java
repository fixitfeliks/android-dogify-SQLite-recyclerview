package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;

import java.util.List;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.data.models.BreedModel;

public interface DataSource {
    LiveData<List<BreedModel>> getBreedDataStream();
    LiveData<ErrorResult> getErrorStream();
    void fetchBreedData();
}
