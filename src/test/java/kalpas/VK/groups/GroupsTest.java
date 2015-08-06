package kalpas.VK.groups;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Groups;

public class GroupsTest extends BaseApiTest {

	@Autowired
    private Groups groups;

    String         fearTheBeard = "32013533";

    @Test
    public void getMembers_hp() throws VKError {
        List<User> members = groups.getMembers(fearTheBeard);
        assertNotNull(members);
        assertFalse(members.isEmpty());
        getLogger().info(members.size());
        getLogger().info(members.toString());
    }

}
