package kalpas.VK.groups;

import static org.junit.Assert.assertNotNull;
import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.helper.GMLHelper;
import kalpas.VKCore.stats.GroupStats;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class GroupStatsTest extends BaseApiTest {

    private static final String purpur = "46944152";
    private GroupStats stats;

    @Before
    public void before() {
        stats = getInjector().getInstance(GroupStats.class);
    }

    @After
    public void tearDown() {
        stats = null;
    }

    @Test
    public void groupStats_getMemberNet_hp() {
		Multimap<User, User> multimap = stats.getMemberNetwork("94192358");

        assertNotNull(multimap);
        
        getLogger().info(multimap.keys().size());
        getLogger().info(multimap.values().size());
        
        GMLHelper.writeToFile(getClass().toString(), multimap);
        

    }

}
