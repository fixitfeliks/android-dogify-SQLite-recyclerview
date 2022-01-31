package co.touchlab.dogify;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import co.touchlab.dogify.mapper.BreedMapperTest;
import co.touchlab.dogify.repository.BreedRemoteDataSourceTest;
import co.touchlab.dogify.repository.BreedRepositoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BreedMapperTest.class,
        BreedRemoteDataSourceTest.class,
        BreedRepositoryTest.class
})
public class DogifyTestSuite
{

}