package co.touchlab.dogify.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BreedMapperTest.class,
        RemoteBreedDataSourceTest.class,
        BreedRepositoryTest.class
})
public class DogifyUnitTestSuite
{

}