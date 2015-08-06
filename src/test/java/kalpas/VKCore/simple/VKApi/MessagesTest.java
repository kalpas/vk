package kalpas.VKCore.simple.VKApi;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.Dialog;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Messages;

public class MessagesTest extends BaseApiTest {

	@Autowired
	private Messages messages;

	@Test
	public void test() throws VKError {
		// do
		List<Dialog> result = messages.getDialogs(10);

		// assert
		assertFalse(result.isEmpty());
	}

	@Test
	public void testWithOffset() throws VKError {
		// do
		List<Dialog> result = messages.getDialogs(10, 10);

		// verify
		assertFalse(result.isEmpty());
	}

}
