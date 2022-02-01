package co.touchlab.dogify.repository;

import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import co.touchlab.dogify.DogTestLogger;
import co.touchlab.dogify.MarkerInterfaces;
import co.touchlab.dogify.data.entities.ErrorResult;
import co.touchlab.dogify.data.entities.ImageResult;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.repository.datasource.RemoteDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.di.RetrofitFactory;
import co.touchlab.dogify.entities.EntityFilepaths;
import co.touchlab.dogify.utils.LiveDataTestUtil;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import retrofit2.Retrofit;

@RunWith(MockitoJUnitRunner.class)
@Category(MarkerInterfaces.BreedRemoteDataSourceTest.class)
public class BreedRemoteDataSourceTest
{
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private Gson gson = new Gson();
    private MockWebServer mockWebServer;
    private Retrofit retrofit;
    private RemoteDataSource remoteDataSource;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        retrofit = RetrofitFactory.getRetrofit(mockWebServer.url("/").toString());
        remoteDataSource = new RemoteDataSource(retrofit, DogService.class);
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testFetchBreedData() throws IOException, InterruptedException {
        List<String> imageCallList = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGE_URL_CALLLIST_EXPECTED_SAVED_RESPONSE), List.class);
        Type imageResultsListType = new TypeToken<List<ImageResult>>(){}.getType();
        List<ImageResult> imageResults = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_IMAGES_RESULT_LIST_SAVED_RESPONSE), imageResultsListType);
        byte[] nameResultJson = Files.readAllBytes(Paths.get(EntityFilepaths.FILEPATH_NAME_RESULT_SAVED_RESPONSE));

        HashMap<String, String> imageUrlLookup = imageResultListToMap(imageResults, imageCallList);
        Dispatcher dispatcher = new Dispatcher() {
            @NonNull
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String parsedBreedPath = parseUrlPathToCallLitItem(Objects.requireNonNull(request.getPath()));
                if ("/breeds/list/all".equals(parsedBreedPath)) {
                    return getMockResponse(nameResultJson);
                }
                if (imageUrlLookup.containsKey(parsedBreedPath)) {
                    String url = parseUrlPathToCallLitItem(parsedBreedPath);
                    return getMockResponse(imageUrlLookup.get(url));
                } else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        remoteDataSource.fetchBreedData();

        List<BreedModel> breedModelsActual = LiveDataTestUtil.awaitValue(remoteDataSource.getBreedDataStream(), 5);
        Type breedModelListType = new TypeToken<List<BreedModel>>(){}.getType();
        List<BreedModel> breedModelsExpected = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE), breedModelListType);

        DogTestLogger.logAssertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
        assertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
    }

    private MockResponse getMockResponse(Object obj) {
        if (obj instanceof String) {
            return new MockResponse()
                    .setResponseCode(200)
                    .setBody((String)obj)
                    .addHeader("Content-Type", "application/json");
        } else if (obj instanceof byte[]) {
            return new MockResponse()
                    .setResponseCode(200)
                    .setBody(new String((byte[]) obj, StandardCharsets.US_ASCII))
                    .addHeader("Content-Type", "application/json");
        }
        return new MockResponse().setResponseCode(404);
    }

    private HashMap<String, String> imageResultListToMap(List<ImageResult> imageResults, List<String> imageCallList) {
        HashMap<String, String> urlLookUp = new HashMap<>();
        for (int i = 0; i < imageResults.size(); i++) {
            urlLookUp.put(imageCallList.get(i), gson.toJson(imageResults.get(i)));
        }
        return urlLookUp;
    }

    private String parseUrlPathToCallLitItem(String path) {
        int startIndex = path.indexOf("/",1) + 1;
        int endIndex = path.indexOf("/images/random");

        String substr;
        try {
            substr = path.substring(startIndex, endIndex);
        } catch (IndexOutOfBoundsException e) {
            return path;
        }
        return substr;
    }

    @Test
    public void testGetBreedsResponseNoWebAccess() {

    }

    @Test
    public void testApiError() throws FileNotFoundException, InterruptedException {
        ErrorResult errorResultExpected = new ErrorResult();
        errorResultExpected.status = "500";
        errorResultExpected.message = "Error getting data from server";
        ErrorResult errorResult = gson.fromJson(new FileReader((EntityFilepaths.FILEPATH_ERROR_RESULT_SAVED_RESPONSE)), ErrorResult.class);
        mockWebServer.enqueue(getMockResponse(gson.toJson(errorResultExpected)));
        remoteDataSource.fetchBreedData();
        ErrorResult errorResultActual = LiveDataTestUtil.awaitValue(remoteDataSource.getErrorStream(), 5);

        DogTestLogger.logAssertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
        assertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
    }
}
