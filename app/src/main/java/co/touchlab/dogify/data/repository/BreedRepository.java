package co.touchlab.dogify.data.repository;

import java.util.List;

import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.RepoErrorModel;

public interface BreedRepository {
    List<BreedModel> getBreedData();
    RepoErrorModel getDataAccessError();
}
