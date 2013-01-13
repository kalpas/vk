package kalpas.VKCore.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Users;
import kalpas.VKCore.simple.VKApi.Wall;
import kalpas.VKCore.simple.VKApi.WallComments;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class WallStats {

    @Inject
    private Friends      FRIENDS;
    @Inject
    private Users        USERS;
    @Inject
    private Wall         WALL;
    @Inject
    private Likes        LIKES;
    @Inject
    private WallComments WALLCOMMENTS;

    Logger               logger = Logger.getLogger(WallStats.class);

    private User   user;


    public void getStats(String uid) {
        WALL.addCount(300);
        LIKES.addType("post");

        this.user = USERS.get(uid);
        List<WallPost> wall = WALL.get(user.uid);
        wall = LIKES.get(wall);
        wall = WALLCOMMENTS.get(wall);

        List<User> wallUsers = new ArrayList<>();

        WallPost post = null;
        Iterator<WallPost> iterator = wall.iterator();
        while (iterator.hasNext()) {
            post = iterator.next();
            if (!post.from_id.equals(user.uid)) {
                wallUsers.add(USERS.get(post.from_id));
            }
        }

    }

}
