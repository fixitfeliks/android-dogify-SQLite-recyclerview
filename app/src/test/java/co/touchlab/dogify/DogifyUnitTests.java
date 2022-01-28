package co.touchlab.dogify;

import org.junit.Test;

import static org.junit.Assert.*;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

import co.touchlab.dogify.data.entities.NamesResult;
import co.touchlab.dogify.data.mappers.BreedMapper;
import co.touchlab.dogify.data.models.BreedModel;

public class DogifyUnitTests
{
    private static final String BREED_RESPONSE_FILEPATH =
            new File("src/test/java/co/touchlab/dogify/entities/TestBreedEntity.json").getAbsolutePath();
    Gson gson = new Gson();
    BreedMapper breedMapper = new BreedMapper();

    //API
    @Test
    public void testGetBreedsRequest() {

    }

    @Test
    public void testGetBreedsResponse() {

    }

    @Test
    public void testGetBreedsResponseNoWebAccess() {

    }

    @Test
    public void testApiError() {

    }

    //Repository
    @Test
    public void testMapperBreedEntityToModel() throws FileNotFoundException {
        NamesResult namesResult = gson.fromJson(new FileReader(BREED_RESPONSE_FILEPATH), NamesResult.class);
//        List<BreedModel> breedModels = breedMapper.mapBreedsEntityToModels(namesResult);
        assertNotNull(namesResult);
    }

    @Test
    public void testMapperErrorEntityToModel() {

    }

    @Test
    public void testGetRepositoryData() {

    }

    @Test
    public void testGetRepositoryDataNoWebAccess() {

    }

    @Test
    public void testGetRepositoryDataError() {

    }

}