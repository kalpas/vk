package kalpas.VK;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import kalpas.VKCore.simple.DO.Like;
import kalpas.VKCore.simple.DO.VKException;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Likes.LikeObject;
import kalpas.VKCore.simple.VKApi.Wall;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LikesTest extends BaseApiTest {

    private Logger logger = LogManager.getLogger(LikesTest.class.getName());

    private Likes  likes;
    private Wall   wall;

    @Before
    public void setup() {
        likes = getInjector().getInstance(Likes.class);
        wall = getInjector().getInstance(Wall.class);
    }

    @After
    public void tearDown() {
        likes = null;
        wall = null;

    }

    @Test
    public void likes_getLikes_hp() {
        Like like = likes.getLikes(LikeObject.post, "1080446", "1466");

        assertTrue(like.count > 1);

        logger.info(like.toString());

    }

    @Test
    public void likes_getLikesRepostOnly_hp() {
        Like like = likes.getLikes(LikeObject.post, "1080446", "1466", true);

        assertEquals(Integer.valueOf(1), like.count);

        logger.info(like.toString());

    }

    @Test
    public void likes_getLikesMoreThenMax_hp() {
        Like like = likes.getLikes(LikeObject.post, "-21642795", "251539");

        assertTrue(like.count > 1);

        logger.info(like.toString());

    }

    @Test
    public void likes_get4Wall_hp() {
        List<WallPost> list = wall.getPosts("32013533", true, 500);
        likes.getLikes(list);

        logger.info(list.get(0).likes.users.length);

    }

    @Test
    public void likes_get4WallrepostOnly_hp() {
        List<WallPost> list = wall.getPosts("32013533", true, 500);
        likes.getLikes(list, true);

        logger.info(list.get(0).likes.users.length);

    }

    @Test(expected = VKException.class)
    @Ignore
    public void likes_getLikesMoreThenMax_ex() {
        likes.getLikes(LikeObject.note, "-21642795", "251539");
    }

}
