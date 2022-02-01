package co.touchlab.dogify.entities;

import java.io.File;

public class EntityFilepaths {
    public static final String FILEPATH_ERROR_RESULT_SAVED_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/responses/ApiErrorEntity_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_NAME_RESULT_SAVED_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/responses/NamesResultEntity_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_NAME_RESULT_ERROR_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/responses/NamesResultEntity_ErrorResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_SAVED_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/responses/ImageResultList_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_ERROR_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/responses/ImageResultList_ErrorResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_ONE_ERROR_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/responses/ImageResultList_One_ErrorResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_FIVE_ERROR_RESPONSE_FIVE_INVALID_URL =
            new File("src/test/java/co/touchlab/dogify/entities/responses/ImageResultList_Five_ErrorResponse_Five_Invalid_Url.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_ONE_INVALID_URL=
            new File("src/test/java/co/touchlab/dogify/entities/responses/ImageResultList_One_InvalidUrl.json").getAbsolutePath();

    public static final String FILEPATH_IMAGE_URL_CALLLIST_EXPECTED_SAVED_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/expected/ImageCallList_ExpectedParsedResult_For_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE =
            new File("src/test/java/co/touchlab/dogify/entities/expected/BreedModels_ExpectedParsedResult_For_SavedResponse.json").getAbsolutePath();

}
