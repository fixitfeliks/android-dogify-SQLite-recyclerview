package co.touchlab.dogify.data.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.data.retrofit.resultmodels.ImageResult;
import co.touchlab.dogify.data.retrofit.resultmodels.NamesResult;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;

public class BreedMapperImpl implements BreedMapper
{
    @Override
    public ErrorModel mapErrorEntityToModel(ErrorResult errorResult) {
        ErrorModel repoErrorModel = new ErrorModel();
        repoErrorModel.message = errorResult.message;
        repoErrorModel.status = errorResult.status;

        return repoErrorModel;
    }

    @Override
    public List<String> mapImageUrlCallList(NamesResult namesResult) {
        List<String> imageUrlCallList = new ArrayList<>();
        if (validateNameResult(namesResult)) {
            LinkedHashMap<String, List<String>> breedEntityList = namesResult.message;

            for (Map.Entry<String, List<String>> entry : breedEntityList.entrySet()) {
                List<String> breedTypes = entry.getValue();
                for (String breedType : breedTypes) {
                    imageUrlCallList.add(mapGetRandomImagePath(entry.getKey(), breedType));
                }
                if (breedTypes.size() == 0) {
                    imageUrlCallList.add(mapGetRandomImagePath(entry.getKey(), ""));
                }
            }

            Collections.sort(imageUrlCallList, Comparator.comparing(this::geListSortString));
        }
        return  imageUrlCallList;
    }

    private boolean validateNameResult (NamesResult namesResult) {
        if (namesResult != null &&
            namesResult.status != null &&
            namesResult.status.equals("success") &&
            namesResult.message != null &&
            namesResult.message instanceof LinkedHashMap &&
            namesResult.message.size() > 0
        ) {
            return true;
        }
        return false;
    }

    private String mapGetRandomImagePath(String breedName, String breedType) {
        String imageUrlName = breedName;
        if (breedType != null && breedType.length() > 0) {
            imageUrlName += "/" + breedType;
        }
        return imageUrlName;
    }

    public String geListSortString(String str) {
        str = urlChangeList(str);
        String breedName = str.indexOf("/") == -1 ? str : str.substring(0, str.indexOf("/"));
        String breedType = str.indexOf("/") == -1 ? "" : str.substring(str.indexOf("/") + 1);
        String join = upperCaseFirstLetter(breedType.trim()) + upperCaseFirstLetter(breedName.trim());
        return join.replace(" ", "");
    }


    @Override
    public List<BreedModel> mapBreedEntitiesToModel(NamesResult namesResult, List<ImageResult> imageResults) {
        List<BreedModel> breedModelList = new ArrayList<>();
        if (validateNameResult(namesResult)) {
            List<String> sortedNamesList = getSortedNamesList(namesResult);

            if (imageResults.size() == sortedNamesList.size()) {
                for (int i = 0; i < imageResults.size(); i++) {
                    if (validateImageResult(imageResults.get(i))) {
                        BreedModel breedModel = new BreedModel(sortedNamesList.get(i), imageResults.get(i).message);
                        breedModelList.add(breedModel);
                    }
                }
            }
        }

        return breedModelList;
    }

    public boolean validateImageResult(ImageResult imageResult) {
        if (
              imageResult != null &&
              imageResult.status != null &&
              imageResult.status.equals("success") &&
              imageResult.message != null &&
              imageResult.message instanceof String &&
              imageResult.message != "" &&
              (imageResult.message.indexOf("https://") == 0 ||
                    imageResult.message.indexOf("http://") == 0)
        ) {
            return true;
        }
        return false;
    }

    private List<String> getSortedNamesList(NamesResult namesResult) {
        List<String> sortedNamedList = new ArrayList<>();
        LinkedHashMap<String, List<String>> namesList = namesResult.message;

        for (Map.Entry<String, List<String>> entry : namesList.entrySet()) {
            List<String> breedTypes = entry.getValue();
            for (int k = 0; k <= breedTypes.size(); k++) {
                if ((breedTypes.size() == 0 && k > 0) ||
                        (breedTypes.size() > 0 && breedTypes.size() == k)) {
                    break;
                }
                String breedType = breedTypes.size() == 0 ? "" : breedTypes.get(k);
                String displayName =  upperCaseFirstLetter(breedType) + " " + upperCaseFirstLetter(entry.getKey()) ;
                sortedNamedList.add(nameChangeList(displayName.trim()));
            }
        }

        Collections.sort(sortedNamedList, Comparator.comparing(this::geListSortString));
        return sortedNamedList;
    }

    private String upperCaseFirstLetter(String str) {
        String formattedString = "";
        if (str != null && str.length() > 0) {
            formattedString = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase().trim();
        }
        return formattedString;
    }

    private String nameChangeList(String str) {
        switch (str) {
            case "germanshepherd":
                return "German Shepherd";

            case "mexicanhairless":
                return "Mexican Hairless";

            case "Shepherd Australian":
                return "Australian Shepherd";

            case "":
                return "Doggy!";

            default:
                return str;
        }
    }

    private String urlChangeList(String str) {
        switch (str) {
            case "australian/shepherd":
                return "shepherd/australian";

            default:
                return str;
        }
    }
}
