package kalpas.VK.friends;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.stats.FriendStats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class FriendStatsTest extends BaseApiTest {

    private FriendStats stats;

    private Logger     logger = LogManager.getLogger(getClass());

    @Before
    public void before() {
        stats = getInjector().getInstance(FriendStats.class);
    }

    @After
    public void tearDown() {
        stats = null;
    }

    @Test
    public void test() {
        Multimap<User, User> net = stats.getNetwork("1080446", true);
        logger.debug("keys " + net.keys().size());
        logger.debug("keySet " + net.keySet().size());
        logger.debug("values " + net.values().size());
    }

}
