package kalpas.VK.groups;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Multimap;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.helper.GMLHelper;
import net.kalpas.VKCore.stats.GroupStats;

public class GroupStatsTest extends BaseApiTest {

	@SuppressWarnings("unused")
	private static final String purpur = "46944152";

	@Autowired
	private GroupStats stats;

	@Test
	public void groupStats_getMemberNet_hp() {
		Multimap<User, User> multimap = stats.getMemberNetwork("94192358");

		assertNotNull(multimap);

		getLogger().info(multimap.keys().size());
		getLogger().info(multimap.values().size());

		GMLHelper.writeToFile(getClass().toString(), multimap);

	}

}
