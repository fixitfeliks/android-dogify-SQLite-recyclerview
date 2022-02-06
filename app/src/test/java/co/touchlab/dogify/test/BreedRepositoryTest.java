package co.touchlab.dogify.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
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

import co.touchlab.dogify.data.db.BreedDao;
import co.touchlab.dogify.data.db.DogifyDb;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.models.ErrorModel;
import co.touchlab.dogify.data.repository.BreedRepositoryImpl;
import co.touchlab.dogify.data.repository.datasource.LocalBreedDataSource;
import co.touchlab.dogify.data.repository.datasource.RemoteBreedDataSource;
import co.touchlab.dogify.data.retrofit.DogService;
import co.touchlab.dogify.data.retrofit.RetrofitFactory;

import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.test.utils.DogTestLogger;
import co.touchlab.dogify.test.utils.EntityFilepaths;
import co.touchlab.dogify.test.utils.LiveDataTestUtil;
import co.touchlab.dogify.test.utils.MockWebServerFactory;
import mockwebserver3.MockWebServer;
import retrofit2.Retrofit;

@RunWith(MockitoJUnitRunner.class)
@Category(BreedRepositoryTest.class)
public class BreedRepositoryTest
{
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    RequestManager glide;

    @Mock
    DogifyDb dogifyDb;

    @Mock
    BreedDao breedDao;

    private Gson gson = new Gson();
    private MockWebServer mockWebServer;
    private Retrofit retrofit;
    private RemoteBreedDataSource remoteBreedDataSource;
    private LocalBreedDataSource localBreedDataSource;
    private BreedRepositoryImpl breedRepository;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServerFactory().getSuccessfulBreedCallFlow();
        retrofit = RetrofitFactory.getNewRetrofit(mockWebServer.url("/").toString());
        remoteBreedDataSource = new RemoteBreedDataSource(glide, retrofit, DogService.class);
        localBreedDataSource = new LocalBreedDataSource(dogifyDb);
        breedRepository = new BreedRepositoryImpl(remoteBreedDataSource,localBreedDataSource);
        mockWebServer.start();
    }

    @Test
    public void testGetRepositoryData() throws IOException, InterruptedException {

        Type breedModelListType = new TypeToken<List<BreedModel>>(){}.getType();
        List<BreedModel> breedModelsExpected = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE), breedModelListType);

        breedRepository.fetchBreedData();
        List<BreedModel> breedModelsActual = LiveDataTestUtil.awaitValue(breedRepository.getBreedDataStream(), 5);

        DogTestLogger.logAssertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
        assertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
        mockWebServer.shutdown();
    }

    @Test
    public void testGetRepositoryDataNoServerResponse() throws Exception {
        ErrorModel errorExpected = new ErrorModel();
        errorExpected.status = "error";
        errorExpected.message = "Failed to connect to localhost";

        Type breedModelListType = new TypeToken<List<BreedModel>>(){}.getType();
        List<BreedModel> breedModelsExpected = gson.fromJson(new FileReader(EntityFilepaths.FILEPATH_BREED_MODELS_EXPECTED_SAVED_RESPONSE), breedModelListType);
        when(dogifyDb.breedDao()).thenReturn(breedDao);
        when(breedDao.getAllBreeds()).thenReturn(breedModelsExpected);

        mockWebServer.shutdown();
        breedRepository.fetchBreedData();
        ErrorModel errorActual = LiveDataTestUtil.awaitValue(breedRepository.getErrorStream(), 5);
        List<BreedModel> breedModelsActual = LiveDataTestUtil.awaitValue(breedRepository.getBreedDataStream(), 5);

        System.out.println("only checking message for: " + errorExpected.message);
        DogTestLogger.logAssertEquals(gson.toJson(errorExpected), gson.toJson(errorActual));
        assertEquals(errorExpected.status, errorActual.status);
        assertEquals(0, errorActual.message.indexOf(errorExpected.message));

        DogTestLogger.logAssertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
        assertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
    }

    @Test
    public void testGetRepositoryDataError() throws Exception {
        ErrorModel errorExpectedRemote = new ErrorModel();
        errorExpectedRemote.status = "error";
        errorExpectedRemote.message = "Failed to connect to localhost";

        ErrorResult errorExpectedLocal = new ErrorResult();
        errorExpectedLocal.status = "error";
        errorExpectedLocal.message = "Test_Exception";
        errorExpectedLocal.code = 0;

        when(dogifyDb.breedDao()).thenReturn(breedDao);
        when(breedDao.getAllBreeds()).thenThrow(new Exception("Test_Exception"));

        mockWebServer.shutdown();
        breedRepository.fetchBreedData();
        ErrorModel errorActualRepoRemote = LiveDataTestUtil.awaitValue(breedRepository.getErrorStream(), 5);
        ErrorResult errorActualLocal = LiveDataTestUtil.awaitValue(localBreedDataSource.getErrorStream(), 10);

        System.out.println("only checking message for: " + errorExpectedRemote.message);
        DogTestLogger.logAssertEquals(gson.toJson(errorExpectedRemote), gson.toJson(errorActualRepoRemote));
        assertEquals(errorExpectedRemote.status, errorActualRepoRemote.status);
        assertEquals(0, errorActualRepoRemote.message.indexOf(errorExpectedRemote.message));

        System.out.println("checking message for: " + errorExpectedLocal.message);
        DogTestLogger.logAssertEquals(gson.toJson(errorExpectedLocal), gson.toJson(errorActualLocal));
        assertEquals(errorExpectedLocal.status, errorActualLocal.status);
        assertEquals(gson.toJson(errorExpectedLocal), gson.toJson(errorActualLocal));
    }
}
