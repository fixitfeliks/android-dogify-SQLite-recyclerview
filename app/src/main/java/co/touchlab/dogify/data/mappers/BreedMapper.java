package co.touchlab.dogify.data.mappers;

import java.util.List;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.data.retrofit.resultmodels.ImageResult;
import co.touchlab.dogify.data.retrofit.resultmodels.NamesResult;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;

public interface BreedMapper {
    ErrorModel mapErrorEntityToModel(ErrorResult errorResult);
    List<String> mapImageUrlCallList(NamesResult namesResult);
    List<BreedModel> mapBreedEntitiesToModel(NamesResult namesResult, List<ImageResult> imageResults);
}
