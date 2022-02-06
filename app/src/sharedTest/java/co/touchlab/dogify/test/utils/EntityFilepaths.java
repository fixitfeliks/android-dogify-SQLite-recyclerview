package co.touchlab.dogify.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EntityFilepaths {
    public static final String FILEPATH_ERROR_RESULT_SAVED_RESPONSE =
            new File("src/sharedTest/assets/entities/responses/ApiErrorEntity_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_NAME_RESULT_SAVED_RESPONSE =
            new File("src/sharedTest/assets/entities/responses/NamesResultEntity_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_NAME_RESULT_ERROR_RESPONSE =
            new File("src/sharedTest/assets/entities/responses/NamesResultEntity_ErrorResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_SAVED_RESPONSE =
            new File("src/sharedTest/assets/entities/responses/ImageResultList_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_ERROR_RESPONSE =
            new File("src/sharedTest/assets/entities/responses/ImageResultList_ErrorResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_ONE_ERROR_RESPONSE =
            new File("src/sharedTest/assets/entities/responses/ImageResultList_One_ErrorResponse.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_FIVE_ERROR_RESPONSE_FIVE_INVALID_URL =
            new File("src/sharedTest/assets/entities/responses/ImageResultList_Five_ErrorResponse_Five_Invalid_Url.json").getAbsolutePath();

    public static final String FILEPATH_IMAGES_RESULT_LIST_ONE_INVALID_URL=
            new File("src/sharedTest/assets/entities/responses/ImageResultList_One_InvalidUrl.json").getAbsolutePath();

    public static final String FILEPATH_IMAGE_URL_CALLLIST_EXPECTED_SAVED_RESPONSE =
            new File("src/sharedTest/assets/entities/expected/ImageCallList_ExpectedParsedResult_For_SavedResponse.json").getAbsolutePath();

    public static final String FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE =
            new File("src/sharedTest/assets/entities/expected/BreedModels_ExpectedParsedResult_For_SavedResponse.json").getAbsolutePath();


    public static final String getInputStreamString(InputStream inputStream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                    int c;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
        }
        return  textBuilder.toString();
    }
}
