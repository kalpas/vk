package kalpas.VK.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Users;

public class UsersTest extends BaseApiTest {

	private static final String selfUid = "1080446";

	@Autowired
	private Users users;

	@Test
	public void users_getByString_hp() throws VKError {

		User me = users.get(selfUid);

		assertLoaded(me);
	}

	@Test
	public void users_getByUser_hp() throws VKError {

		User me = users.get(new User(selfUid));

		assertLoaded(me);
	}

	@Test
	public void users_getList_hp() throws VKError {

		List<User> list = users.get(Arrays.asList(new User(selfUid), new User("1")));

		assertNotNull(list);
		assertFalse(list.isEmpty());

		for (User user : list) {
			assertLoaded(user);
		}
	}

	private void assertLoaded(User me) {
		assertNotNull(me);
		assertNotNull(me.first_name);
		assertNotNull(me.last_name);

		getLogger().info(me);
	}

}
