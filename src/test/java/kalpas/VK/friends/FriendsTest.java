package kalpas.VK.friends;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.VKApi.Friends;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FriendsTest extends BaseApiTest {

    private static final String selfUid = "1080446";
    private Friends             friends;

    @Before
    public void before() throws InterruptedException {
        friends = getInjector().getInstance(Friends.class);
        Thread.sleep(400L);
    }

    @After
    public void tearDown() {
        friends = null;
    }

    @Test
    public void friends_get_hp() throws VKError {
        List<User> list = friends.get(selfUid);

        checkErrors();
        assertFriendsLoaded(list);

    }

    @Test
    public void friends_getByUser_hp() throws VKError {
        List<User> list = friends.get(new User(selfUid));

        checkErrors();
        assertFriendsLoaded(list);

    }

    @Test
    public void friends_loadList_hp() {
        Map<User, List<User>> map = friends.get(Arrays.asList(new User(selfUid), new User("1")));

        checkErrors();

        assertNotNull(map);
        assertFalse(map.keySet().isEmpty());

        Iterator<Map.Entry<User, List<User>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            assertFriendsLoaded(iterator.next().getValue());
        }

    }

    private void checkErrors() {
        Iterator<VKError> i = friends.getErrors().iterator();
        while (i.hasNext()) {
            System.err.println(i.next());
        }
        assertTrue(friends.getErrors().isEmpty());
    }

    private void assertFriendsLoaded(List<User> list) {
        assertNotNull(list);
        assertFalse(list.isEmpty());

        getLogger().info(list.toString());
        getLogger().info(list.size());
    }

}
