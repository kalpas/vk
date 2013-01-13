package kalpas.VK.groups;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.VKApi.Groups;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GroupsTest extends BaseApiTest {

    private Groups groups;

    String         fearTheBeard = "32013533";

    @Before
    public void before() {
        groups = getInjector().getInstance(Groups.class);
    }

    @After
    public void tearDown() {
        groups = null;
    }

    @Test
    public void getMembers_hp() {
        List<User> members = groups.getMembers(fearTheBeard);
        assertNotNull(members);
        assertFalse(members.isEmpty());
        getLogger().info(members.size());
        getLogger().info(members.toString());
    }

}
