package kalpas.VKCore.stats;

import java.util.List;

import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Users;
import kalpas.VKCore.simple.VKApi.Wall;
import kalpas.VKCore.simple.VKApi.WallComments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

public class WallStats {

    @Inject
    private Friends      friends;
    @Inject
    private Users        users;
    @Inject
    private Wall         wall;
    @Inject
    private Likes        likes;
    @Inject
    private WallComments wallcomments;

    private Logger       logger = LogManager.getLogger(WallStats.class);

    public void getInteractions(String id) {
        List<WallPost> list = wall.getPosts(id);

    }





}
