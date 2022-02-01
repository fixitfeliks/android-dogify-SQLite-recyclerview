package co.touchlab.dogify.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import co.touchlab.dogify.DogTestLogger;
import co.touchlab.dogify.MarkerInterfaces;
import co.touchlab.dogify.data.entities.ErrorResult;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.mappers.BreedMapperImpl;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.entities.EntityFilepaths;

@Category(MarkerInterfaces.BreedMapperTest.class)
public class BreedMapperTest
{
    @Rule
    public TestRule logRule = DogTestLogger.getLogRule;

    Gson gson = new Gson();
    BreedMapperImpl breedMapper = new BreedMapperImpl();

    @Test
    public void testMapperErrorEntityToModelHistorical() throws FileNotFoundException {
        ErrorResult errorResult = gson.fromJson(new FileReader((EntityFilepaths.FILEPATH_ERROR_RESULT_SAVED_RESPONSE)), ErrorResult.class);
        ErrorModel errorModel = breedMapper.mapErrorEntityToModel(errorResult);

        DogTestLogger.logAssertEquals("error", errorModel.status);
        assertEquals("error", errorModel.status);
    }

    @Test
    public void testMapImageUrlCallListHistorical() throws IOException {
        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE), NamesResult.class);
        List<String> actual = breedMapper.mapImageUrlCallList(namesResult);
        List<String> expected = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGE_URL_CALLLIST_EXPECTED_SAVED_RESPONSE), List.class);

        DogTestLogger.logAssertEquals(expected.toString(), actual.toString());
        assertEquals(expected,actual);
    }

    @Test
    public void testMapImageUrlCallListError() throws IOException {
        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_ERROR_RESPONSE), NamesResult.class);
        List<String> actual = breedMapper.mapImageUrlCallList(namesResult);

        DogTestLogger.logAssertEquals(String.valueOf(0), String.valueOf(actual.size()));
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    public void testMapperBreedEntityToModel() throws FileNotFoundException {
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();
        Type breedModelListType = new TypeToken<List<BreedModel>>(){}.getType();

        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE), NamesResult.class);
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_SAVED_RESPONSE), imageResultsListType);
        List<BreedModel> breedModelsActual = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);
        List<BreedModel> breedModelsExpected = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE), breedModelListType);

        String actual = gson.toJson(breedModelsActual);
        String expected = gson.toJson(breedModelsExpected);

        DogTestLogger.logAssertEquals(expected, actual);
        assertNotNull(breedModelsActual);
        assertEquals(expected, actual);
    }

    @Test
    public void testMapperBreedEntityToModelNamesResultError() throws FileNotFoundException {
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();

        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_ERROR_RESPONSE), NamesResult.class);
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_SAVED_RESPONSE), imageResultsListType);
        List<BreedModel> breedModelsActual = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);

        DogTestLogger.logAssertEquals(String.valueOf(0), String.valueOf(breedModelsActual.size()));
        assertNotNull(breedModelsActual);
        assertEquals(0, breedModelsActual.size());
    }

    @Test
    public void testMapperBreedEntityToModelImageResultError() throws FileNotFoundException {
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();

        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE), NamesResult.class);
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_ERROR_RESPONSE), imageResultsListType);
        List<BreedModel> breedModelsActual = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);

        DogTestLogger.logAssertEquals(String.valueOf(0), String.valueOf(breedModelsActual.size()));
        assertNotNull(breedModelsActual);
        assertEquals(0, breedModelsActual.size());
    }

    @Test
    public void testMapperBreedEntityToModelImageResultOneErrorInList() throws FileNotFoundException {
        int invalidRows = 1;
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();

        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE), NamesResult.class);
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_ONE_ERROR_RESPONSE), imageResultsListType);
        List<BreedModel> breedModelsActual = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);
        int expected = imageResults.size() - invalidRows;

        DogTestLogger.logAssertEquals(String.valueOf(expected), String.valueOf(breedModelsActual.size()));
        assertNotNull(breedModelsActual);
        assertEquals(expected, breedModelsActual.size());
        assertTrue(checkAllModelsToUrl(breedModelsActual, imageResults));
    }

    @Test
    public void testMapperBreedEntityToModelImageResultOneInvalidUrl() throws FileNotFoundException {
        int invalidRows = 1;
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();

        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE), NamesResult.class);
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_ONE_INVALID_URL), imageResultsListType);
        List<BreedModel> breedModelsActual = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);
        int expected = imageResults.size() - invalidRows;

        DogTestLogger.logAssertEquals(String.valueOf(expected), String.valueOf(breedModelsActual.size()));
        assertNotNull(breedModelsActual);
        assertEquals(expected, breedModelsActual.size());
        assertTrue(checkAllModelsToUrl(breedModelsActual, imageResults));
    }

    @Test
    public void testMapperBreedEntityToModelImageResultFiveErrorResultFiveInvalidUrl() throws FileNotFoundException {
        int invalidRows = 10;
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();

        NamesResult namesResult = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE), NamesResult.class);
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_FIVE_ERROR_RESPONSE_FIVE_INVALID_URL), imageResultsListType);
        List<BreedModel> breedModelsActual = breedMapper.mapBreedEntitiesToModel(namesResult, imageResults);
        int expected = imageResults.size() - invalidRows;

        DogTestLogger.logAssertEquals(String.valueOf(expected), String.valueOf(breedModelsActual.size()));
        assertNotNull(breedModelsActual);
        assertEquals(expected, breedModelsActual.size());
        assertTrue(checkAllModelsToUrl(breedModelsActual, imageResults));
    }

    private boolean checkAllModelsToUrl(List<BreedModel> breedModels, List<ImageResult> imageResults) {
        int i = 0;
        for (ImageResult imageResult : imageResults) {
            if (breedMapper.validateImageResult(imageResult)) {
                if (imageResult.message.equals(breedModels.get(i).imageUrl)) {
                    System.out.println(String.format(
                            "Checking Urls at index (%s):\nBreedMode.imageUrl:  %s\nImageResult.message: %s\n",
                            i, breedModels.get(i).imageUrl, imageResult.message
                    ));
                    i++;
                } else {
                    return false;
                }
            }
        }

        if (breedModels.size() == i) {
            return true;
        }

        return false;
    }
}
