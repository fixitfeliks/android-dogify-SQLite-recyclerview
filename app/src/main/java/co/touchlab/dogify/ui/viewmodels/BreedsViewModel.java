package co.touchlab.dogify.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.BreedRepository;

public class BreedsViewModel extends ViewModel
{
    private final BreedRepository breedRepository;

    public BreedsViewModel(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
    }

    public LiveData<List<BreedModel>> getBreedDataStream() {
        return breedRepository.getBreedDataStream();
    }

    public LiveData<ErrorModel> getErrorStream() {
        return breedRepository.getErrorStream();
    }

    public void fetchBreedData() {
        breedRepository.fetchBreedData();
    }

}
