package kalpas.VK.friends;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Multimap;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.helper.GMLHelper;
import net.kalpas.VKCore.stats.FriendStats;

public class FriendStatsTest extends BaseApiTest {

	@Autowired
	private FriendStats stats;

	private Logger logger = LogManager.getLogger(getClass());

	@Test
	public void test() throws VKError {
		Multimap<User, User> net = stats.getNetwork("1080446", true);
		logger.debug("keys " + net.keys().size());
		logger.debug("keySet " + net.keySet().size());
		logger.debug("values " + net.values().size());
	}

	@Test
	public void exp() throws VKError {
		Multimap<User, User> net = stats.getNetwork("1080446", true);

		GMLHelper.writeToFile("me" + new Date().getTime(), net);
		logger.debug("keys " + net.keys().size());
		logger.debug("keySet " + net.keySet().size());
		logger.debug("values " + net.values().size());
	}

}
