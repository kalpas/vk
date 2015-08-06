package kalpas.VKCore.simple.VKApi;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.City;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Locations;

public class LocationsTest extends BaseApiTest {

	@Autowired
	private Locations locations;

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
