package co.touchlab.dogify.data.repository.datasource;

import androidx.lifecycle.LiveData;

import co.touchlab.dogify.data.entities.ApiError;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;

public interface DataSource {
    LiveData<NamesResult> getBreedNames();
    LiveData<ImageResult> getBreedImage();
    LiveData<ApiError> getApiError();
}
