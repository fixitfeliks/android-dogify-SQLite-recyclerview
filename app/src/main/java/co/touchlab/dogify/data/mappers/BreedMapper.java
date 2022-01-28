package co.touchlab.dogify.data.mappers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.touchlab.dogify.data.entities.ApiError;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;

public class BreedMapper
{
    public ErrorModel mapErrorEntityToModel(ApiError apiError) {
        ErrorModel repoErrorModel = new ErrorModel();
        repoErrorModel.message = apiError.message;
        return repoErrorModel;
    }

    public List<String> getImageUrlCallList(NamesResult namesResult) {
        List<String> imageUrlCallList = new ArrayList<>();
        LinkedHashMap<String, List<String>> breedEntityList = namesResult.message;

        for (Map.Entry<String, List<String>> entry : breedEntityList.entrySet()) {
            List<String> breedTypes = entry.getValue();
            if (breedTypes.size() > 0) {
                for (String breedType : breedTypes) {
                    imageUrlCallList.add(urlJoinBreedNames(entry.getKey(), breedType));
                }
            } else {
                imageUrlCallList.add(urlJoinBreedNames(entry.getKey(), ""));
            }
        }
        return  imageUrlCallList;
    }

    private String urlJoinBreedNames(String breedName, String breedType) {
        String imageUrlName = breedName;
        if (breedType != null && breedType.length() > 0) {
            imageUrlName += "/" + breedType;
        }
        return imageUrlName;
    }

    public List<BreedModel> mapBreedsEntitiesToModel(NamesResult namesResult, List<ImageResult> imageResults) {
        List<BreedModel> breedModelList = new ArrayList<>();
        LinkedHashMap<String, List<String>> breedEntityList = namesResult.message;

        int i = 0;
        for (Map.Entry<String, List<String>> entry : breedEntityList.entrySet()) {
            List<String> breedTypes = entry.getValue();
            if (breedTypes.size() > 0) {
                for (String breedType : breedTypes) {
                    breedModelList.add(parseName(entry.getKey(), breedType, imageResults.get(i).message));
                    i++;
                }
            } else {
                breedModelList.add(parseName(entry.getKey(), "", imageResults.get(i).message));
                i++;
            }
        }
        return  breedModelList;
    }

    private BreedModel parseName(String breedName, String breedType, String url) {
        String breedDisplayName = parseBreedDisplayName(breedName, breedType);

        BreedModel breedModel = new BreedModel(breedDisplayName, url);
        return breedModel;
    }

    private String parseBreedDisplayName(String breedName, String breedType) {
        return formatDisplayString(breedType) + " " + formatDisplayString(breedName);
    }

    private String formatDisplayString(String str) {
        return (str != null && str.length() > 0)
            ? str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()
            : "";
    }
}
