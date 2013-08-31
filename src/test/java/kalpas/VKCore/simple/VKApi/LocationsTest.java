package kalpas.VKCore.simple.VKApi;

import static org.junit.Assert.assertNotNull;
import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.City;
import kalpas.VKCore.simple.DO.VKError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocationsTest extends BaseApiTest {

    private Locations locations;

    @Before
    public void setup(){
        locations = getInjector().getInstance(Locations.class);
    }

    @After
    public void tearDown() {
        locations = null;
    }

    @Test
    public void test() throws VKError {
        // given

        // do
        City[] result = locations.getCities("10");

        // verify
        assertNotNull(result);
    }

    @Test
    public void getCityByIdTest() throws VKError {
        // do
        City city = locations.getCityById("280");// expected to be Kharkov
        city = locations.getCityById("280");// expected to be Kharkov

        // verify
        assertNotNull(city);
        assertNotNull(city.title);
    }


}
