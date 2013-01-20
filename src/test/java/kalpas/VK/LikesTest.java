package kalpas.VK;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import kalpas.VKCore.simple.DO.Like;
import kalpas.VKCore.simple.DO.VKException;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Likes.LikeObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LikesTest extends BaseApiTest {

    private Logger logger = LogManager.getLogger(LikesTest.class.getName());

    private Likes likes;

    @Before
    public void setup() {
        likes = getInjector().getInstance(Likes.class);
    }

    @After
    public void tearDown() {
        likes = null;
    }

    @Test
    @Ignore
    public void likes_get_hp() {
        Like like = likes.get("1080446", "1476");

        logger.info(like.toString());
    }

    @Test
    public void likes_getLikes_hp() throws VKException {
        Like like = likes.getLikes(LikeObject.post, "1080446", "1466");

        assertTrue(like.count > 1);

        logger.info(like.toString());

    }

    @Test
    public void likes_getLikesRepostOnly_hp() throws VKException {
        Like like = likes.getLikes(LikeObject.post, "1080446", "1466", true);

        assertEquals(Integer.valueOf(1), like.count);

        logger.info(like.toString());

    }

    @Test
    public void likes_getLikesMoreThenMax_hp() throws VKException {
        Like like = likes.getLikes(LikeObject.post, "-21642795", "251539");

        assertTrue(like.count > 1);

        logger.info(like.toString());

    }

    @Test(expected = VKException.class)
    @Ignore
    public void likes_getLikesMoreThenMax_ex() throws VKException {
        likes.getLikes(LikeObject.note, "-21642795", "251539");
    }

}
