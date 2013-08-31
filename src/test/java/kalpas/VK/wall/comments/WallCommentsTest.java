package kalpas.VK.wall.comments;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.simple.DO.Comment;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.Wall;
import kalpas.VKCore.simple.VKApi.WallComments;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WallCommentsTest extends BaseApiTest {

    private static final String selfUid = "1080446";

    private WallComments comments;
    private Wall         wall;

    @Before
    public void setUp() {
        comments = getInjector().getInstance(WallComments.class);
        wall = getInjector().getInstance(Wall.class);
    }

    @After
    public void tearDown() {
        comments = null;
        wall = null;
    }

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
