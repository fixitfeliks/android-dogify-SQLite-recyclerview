package co.touchlab.dogify.test;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import co.touchlab.dogify.data.db.BreedDao;
import co.touchlab.dogify.data.db.DogifyDb;
import co.touchlab.dogify.data.models.BreedModel;
import co.touchlab.dogify.data.repository.datasource.LocalBreedDataSource;
import co.touchlab.dogify.data.retrofit.resultmodels.ErrorResult;
import co.touchlab.dogify.test.utils.DogTestLogger;
import co.touchlab.dogify.test.utils.EntityFilepaths;
import co.touchlab.dogify.test.utils.LiveDataTestUtil;

@RunWith(AndroidJUnit4.class)
@Category(LocalBreedDataSourceTest.class)
public class LocalBreedDataSourceTest
{
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private Gson gson = new Gson();
    private Context context;
    private BreedDao breedDao;
    private DogifyDb dogifyDb;
    private LocalBreedDataSource localBreedDataSource;
    private AssetManager assetManager;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        dogifyDb = Room.inMemoryDatabaseBuilder(context, DogifyDb.class).build();
        breedDao = dogifyDb.breedDao();
        localBreedDataSource = new LocalBreedDataSource(dogifyDb);
        assetManager = InstrumentationRegistry
                .getInstrumentation()
                .getContext()
                .getAssets();
    }

    @After
    public void closeDb() {
        dogifyDb.close();
    }

    @Test
    public void testFetchBreedData() throws Exception {
        InputStream inputStream = assetManager.open("entities/expected/BreedModels_ExpectedParsedResult_For_SavedResponse.json");
        String response = EntityFilepaths.getInputStreamString(inputStream);
        Type breedModelListType = new TypeToken<List<BreedModel>>(){}.getType();
        List<BreedModel> breedModelsExpected = gson.fromJson(response, breedModelListType);
        breedDao.insertBreeds(breedModelsExpected);
        localBreedDataSource.fetchBreedData();
        List<BreedModel> breedModelsActual = LiveDataTestUtil.awaitValue(localBreedDataSource.getBreedDataStream(), 5);

        DogTestLogger.logAssertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
        assertEquals(gson.toJson(breedModelsExpected), gson.toJson(breedModelsActual));
    }

    @Test
    public void testFetchBreedDataNullDb() throws IOException, InterruptedException {
        localBreedDataSource = new LocalBreedDataSource(null);
        localBreedDataSource.fetchBreedData();

        ErrorResult errorResultExpected = new ErrorResult();
        errorResultExpected.status = "error";
        errorResultExpected.message = "null";
        errorResultExpected.code = 0;

        ErrorResult errorResultActual = LiveDataTestUtil.awaitValue(localBreedDataSource.getErrorStream(), 5);
        System.out.println("only checking message for 'null' in message");
        DogTestLogger.logAssertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
        assertEquals(errorResultExpected.code, errorResultActual.code);
        assertEquals(errorResultExpected.status, errorResultActual.status);
        assertTrue(errorResultActual.message.contains(errorResultExpected.message));
    }

    @Test
    public void testInsertNPEError() throws Exception {
        localBreedDataSource.storeBreedModels(null);
        ErrorResult errorResultActual = LiveDataTestUtil.awaitValue(localBreedDataSource.getErrorStream(), 5);

        ErrorResult errorResultExpected = new ErrorResult();
        errorResultExpected.status = "error";
        errorResultExpected.message = "null";
        errorResultExpected.code = 0;

        System.out.println("Just checking for 'null' in message");
        DogTestLogger.logAssertEquals(gson.toJson(errorResultExpected), gson.toJson(errorResultActual));
        assertEquals(errorResultExpected.status, errorResultActual.status);
        assertEquals(errorResultExpected.code, errorResultActual.code);
        assertTrue(errorResultActual.message.contains(errorResultExpected.message));
    }
}
