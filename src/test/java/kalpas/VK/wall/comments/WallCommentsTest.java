package kalpas.VK.wall.comments;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.simple.DO.Comment;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.DO.WallPost;
import net.kalpas.VKCore.simple.VKApi.Wall;
import net.kalpas.VKCore.simple.VKApi.WallComments;

public class WallCommentsTest extends BaseApiTest {

	private static final String selfUid = "1080446";

	@Autowired
	private WallComments comments;

	@Autowired
	private Wall wall;

	@Test
	public void getWallComments_hp() throws VKError {
		List<WallPost> list = wall.getPosts(selfUid, 5);
		assertNotNull(list);
		assertFalse(list.isEmpty());

		comments.get(list);

		for (WallPost post : list) {
			assertNotNull(post.comments.comments);
			getLogger().info("========================" + post.from_id + "===========================");
			for (Comment comment : post.comments.comments) {
				getLogger().info(comment.toString());
			}
		}
	}

}
