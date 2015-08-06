package kalpas.VK;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.kalpas.VKCore.simple.DO.Like;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.DO.WallPost;
import net.kalpas.VKCore.simple.VKApi.Likes;
import net.kalpas.VKCore.simple.VKApi.Likes.LikeObject;
import net.kalpas.VKCore.simple.VKApi.Wall;

public class LikesTest extends BaseApiTest {

	private Logger logger = LogManager.getLogger(LikesTest.class.getName());

	@Autowired
	private Likes likes;

	@Autowired
	private Wall wall;

	@Test
	public void likes_getLikes_hp() throws VKError {
		Like like = likes.getLikes(LikeObject.post, "1080446", "1466");

		assertTrue(like.count > 1);

		logger.info(like.toString());

	}

	@Test
	public void likes_getLikesRepostOnly_hp() throws VKError {
		Like like = likes.getLikes(LikeObject.post, "1080446", "1466", true);

		assertEquals(Integer.valueOf(1), like.count);

		logger.info(like.toString());

	}

	@Test
	public void likes_getLikesMoreThenMax_hp() throws VKError {
		Like like = likes.getLikes(LikeObject.post, "-21642795", "251539");

		assertTrue(like.count > 1);

		logger.info(like.toString());

	}

	@Test
	public void likes_get4Wall_hp() throws VKError {
		List<WallPost> list = wall.getPosts("32013533", true, 500);
		likes.getLikes(list);

		logger.info(list.get(0).likes.items.length);

	}

	@Test
	public void likes_get4WallrepostOnly_hp() throws VKError {
		List<WallPost> list = wall.getPosts("32013533", true, 500);
		likes.getLikes(list, true);

		logger.info(list.get(0).likes.items.length);

	}

	@Test(expected = VKError.class)
	@Ignore
	public void likes_getLikesMoreThenMax_ex() throws VKError {
		likes.getLikes(LikeObject.note, "-21642795", "251539");
	}

}
