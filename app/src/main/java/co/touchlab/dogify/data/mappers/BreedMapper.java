package co.touchlab.dogify.data.mappers;

import java.util.List;

import co.touchlab.dogify.data.entities.ApiError;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;

public interface BreedMapper {
    ErrorModel mapErrorEntityToModel(ApiError apiError);
    List<String> mapImageUrlCallList(NamesResult namesResult);
    List<BreedModel> mapBreedEntitiesToModel(NamesResult namesResult, List<ImageResult> imageResults);
}
