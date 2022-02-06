package co.touchlab.dogify.test;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.repository.datasource.RemoteBreedDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.data.retrofit.RetrofitFactory;
import co.touchlab.dogify.test.utils.DogTestLogger;
import co.touchlab.dogify.test.utils.EntityFilepaths;
import co.touchlab.dogify.test.utils.LiveDataTestUtil;
import co.touchlab.dogify.test.utils.MockWebServerFactory;
import mockwebserver3.MockWebServer;
import retrofit2.Retrofit;

@RunWith(MockitoJUnitRunner.class)
@Category(RemoteBreedDataSourceTest.class)
public class RemoteBreedDataSourceTest
{
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private RequestManager glide;

    private Gson gson = new Gson();
    private MockWebServer mockWebServer;
    private Retrofit retrofit;
    private RemoteBreedDataSource remoteBreedDataSource;

    public void setupNewMockWebServer(MockWebServer mockWebServer) throws IOException {
        this.mockWebServer = mockWebServer;
        retrofit = RetrofitFactory.getNewRetrofit(this.mockWebServer.url("/").toString());
        remoteBreedDataSource = new RemoteBreedDataSource(glide, retrofit, DogService.class);
        this.mockWebServer.start();
    }

    @After
    public void shutdown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public void testFetchBreedData() throws IOException, InterruptedException {
        setupNewMockWebServer(MockWebServerFactory.getSuccessfulBreedCallFlow());
        remoteBreedDataSource.fetchBreedData();

        List<BreedModel> breedModelsActual = LiveDataTestUtil.awaitValue(remoteBreedDataSource.getBreedDataStream(), 5);
        Type breedModelListType = new TypeToken<List<BreedModel>>(){}.getType();
        List<BreedModel> breedModelsExpected = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE), breedModelListType);

        DogTestLogger.logAssertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
        assertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
    }



    @Test
    public void testGetBreedsResponseRemoteTimeout() throws IOException, InterruptedException {
        setupNewMockWebServer(MockWebServerFactory.getNoResponse());
        remoteBreedDataSource.fetchBreedData();

        ErrorResult errorExpected = new ErrorResult();
        errorExpected.code = 0;
        errorExpected.status = "error";
        errorExpected.message = "timeout";

        ErrorResult errorActual = LiveDataTestUtil.awaitValue(remoteBreedDataSource.getErrorStream(), 5);
        DogTestLogger.logAssertEquals(gson.toJson(errorExpected), gson.toJson(errorActual));
        assertEquals(gson.toJson(errorExpected), gson.toJson(errorActual));
    }

    @Test
    public void testGetBreedsResponseNoConnection() throws IOException, InterruptedException {
        setupNewMockWebServer(MockWebServerFactory.getSuccessfulBreedCallFlow());
        mockWebServer.shutdown();
        remoteBreedDataSource.fetchBreedData();

        ErrorResult errorExpected = new ErrorResult();
        errorExpected.code = 0;
        errorExpected.status = "error";
        errorExpected.message = "Failed to connect to localhost";

        ErrorResult errorActual = LiveDataTestUtil.awaitValue(remoteBreedDataSource.getErrorStream(), 5);
        System.out.println("only checking message for: " + errorExpected.message);
        DogTestLogger.logAssertEquals(gson.toJson(errorExpected), gson.toJson(errorActual));
        assertEquals(errorExpected.code, errorActual.code);
        assertEquals(errorExpected.status, errorActual.status);
        assertEquals(0, errorActual.message.indexOf(errorExpected.message));
    }

    @Test
    public void testApiError() throws IOException, InterruptedException {
        setupNewMockWebServer(MockWebServerFactory.getErrorResponse());

        ErrorResult errorResultExpected = new ErrorResult();
        errorResultExpected.status = "error";
        errorResultExpected.message = "No route found for \"GET /api/breeds/lij]/st/all\" with code: 0";
        errorResultExpected.code = 404;

        remoteBreedDataSource.fetchBreedData();
        ErrorResult errorResultActual = LiveDataTestUtil.awaitValue(remoteBreedDataSource.getErrorStream(), 5);

        DogTestLogger.logAssertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
        assertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
    }



    @Test
    public void testApiErrorEmptyResponse() throws IOException, InterruptedException {
        setupNewMockWebServer(MockWebServerFactory.getEmptyErrorResponse());

        ErrorResult errorResultExpected = new ErrorResult();
        errorResultExpected.status = "error";
        errorResultExpected.message = "Error getting breed data";
        errorResultExpected.code = 0;

        remoteBreedDataSource.fetchBreedData();
        ErrorResult errorResultActual = LiveDataTestUtil.awaitValue(remoteBreedDataSource.getErrorStream(), 5);

        DogTestLogger.logAssertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
        assertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
    }
}
