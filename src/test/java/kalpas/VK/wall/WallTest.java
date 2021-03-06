package kalpas.VK.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.Wall;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WallTest extends BaseApiTest {

    private Logger logger = LogManager.getLogger(WallTest.class.getName());

    private Wall wall = null;

    @Before
    public void before() {
        wall = getInjector().getInstance(Wall.class);
    }

    @After
    public void tearDown() {
        wall = null;
    }

    @Test
    public void wall_get_hp() throws VKError {

        // 48469829
        logger.error("separator");
        // List<WallPost> list = wall.get("-48282897");
        List<WallPost> list = wall.getPosts("48282897", true);
        
        assertNotNull(list);
        assertFalse(list.isEmpty());

        logger.info(list.size());
        for (WallPost post : list) {
            logger.info(post);
        }

    }

    @Test
    public void wall_getWcount_hp() throws VKError {

        logger.error("separator");
        List<WallPost> list = wall.getPosts("48282897", true, 10);

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(10, list.size());

        logger.info(list.size());
        for (WallPost post : list) {
            logger.info(post);
        }

    }

    @Test
    public void wall_get1_hp() throws VKError {

        logger.error("separator");
        List<WallPost> list = wall.getPosts("1080446", 20);

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(20, list.size());

        logger.info(list.size());
        for (WallPost post : list) {
            logger.info(post);
            logger.info(post.id);

            DateTime time = new DateTime(Long.valueOf(post.date) * 1000L);
            logger.info(time);
        }

    }

    @Test
    public void wall_4period() throws VKError {
        logger.error("separator");

        List<WallPost> list = wall.getPosts4Period("1080446", false, 20);
        assertNotNull(list);
    }

}
