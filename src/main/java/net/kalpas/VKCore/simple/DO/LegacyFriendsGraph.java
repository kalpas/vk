package net.kalpas.VKCore.simple.DO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import net.kalpas.VKCore.simple.DO.WallPost.Comments;
import net.kalpas.VKCore.simple.VKApi.Friends;
import net.kalpas.VKCore.simple.VKApi.Likes;
import net.kalpas.VKCore.simple.VKApi.Users;
import net.kalpas.VKCore.simple.VKApi.Wall;
import net.kalpas.VKCore.simple.VKApi.WallComments;

public class LegacyFriendsGraph {

    public SetMultimap<UserRelation, User> edges  = HashMultimap.create();

    private final static String            selfId = "1080446";

    @Autowired
    private Friends                        FRIENDS;
    @Autowired
    private Users                          USERS;
    @Autowired
    private Wall                           WALL;
    @Autowired
    private Likes                          LIKES;
    @Autowired
    private WallComments                   WALLCOMMENTS;

    Logger                                 logger = LogManager.getLogger(LegacyFriendsGraph.class);

    public LegacyFriendsGraph() {
    }

    public void getMyFriends() {
        User self = null;
        try {
            self = USERS.get(selfId);
        } catch (VKError e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        List<User> friends = null;
        friends = FRIENDS.get(self);
        logger.info(friends.size() + " friends");
        try {
            friends = USERS.get(friends);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        logger.debug("got friends");

        Map<User, List<WallPost>> wallPosts = new HashMap<>();
        int i = 0;
        int size = friends.size();
        for (User user : friends) {
            List<WallPost> wall = null;
            try {
                wall = WALL.getPosts(user.id);
            } catch (VKError e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            logger.debug("got wall");
            try {
                LIKES.getLikes(wall);
            } catch (VKError e) {
                e.printStackTrace();
            }
            logger.debug("got likes");
            try {
                wall = WALLCOMMENTS.get(wall);
            } catch (VKError e) {
                e.printStackTrace();
            }
            logger.debug("got commets");
            wallPosts.put(user, wall);
            logger.debug(i + "//" + size);
            i++;
        }
        logger.debug("got all info");

        Map<String, Map<String, List<WallPost>>> posts = new HashMap<>();
        Map<String, Map<String, List<WallPost>>> likes = new HashMap<>();
        Map<String, Map<String, List<WallPost>>> comments = new HashMap<>();
        for (Map.Entry<User, List<WallPost>> entry : wallPosts.entrySet()) {
            for (WallPost post : entry.getValue()) {
                countPosts(posts, entry, post);
                countLikes(likes, post);
                countComments(comments, post);
            }
        }

        logger.debug("counters");

        UserRelation myRelations = new UserRelation(self);
        for (User user : friends) {
            getCounters(posts, likes, comments, myRelations, user);
            edges.put(myRelations, user);
        }

        Map<User, List<User>> friendsOfFriends = FRIENDS.get(friends);

        logger.debug("got friends of a friends");

        List<User> usersOfInterest = new ArrayList<>();
        usersOfInterest.add(self);
        usersOfInterest.addAll(friends);
        for (Map.Entry<User, List<User>> entry : friendsOfFriends.entrySet()) {
            for (User user : entry.getValue()) {
                if (usersOfInterest.contains(user)) {
                    UserRelation relations = new UserRelation(entry.getKey());
                    getCounters(posts, likes, comments, relations, user);
                    edges.put(relations, user);
                }
            }
        }
        logger.info(edges.size() + "edges");
    }

    private void getCounters(Map<String, Map<String, List<WallPost>>> posts,
            Map<String, Map<String, List<WallPost>>> likes, Map<String, Map<String, List<WallPost>>> comments,
            UserRelation relations, User user) {
        RelationCounters counters = new RelationCounters();
        counters.wallPosts = tryGet(posts, relations.user.id, user.id);
        counters.likes = tryGet(likes, relations.user.id, user.id);
        counters.comments = tryGet(comments, relations.user.id, user.id);
        relations.relations.put(user.id, counters);
    }

    private int tryGet(Map<String, Map<String, List<WallPost>>> likes, String fromId, String toId) {
        Map<String, List<WallPost>> map;
        List<WallPost> list;
        map = likes.get(fromId);
        list = map != null ? map.get(toId) : null;
        return list != null ? list.size() : 0;
    }

    private void countComments(Map<String, Map<String, List<WallPost>>> posts, WallPost post) {
        Comments comments = post.comments;
        if (comments == null || comments.comments == null) {
            return;
        }
        for (Comment comment : comments.comments) {
            getOrCreate(posts, comment.from_id, post.to_id).add(post);
        }
    }

    private void countLikes(Map<String, Map<String, List<WallPost>>> posts, WallPost post) {
        Like likes = post.likes;
        if (likes == null || likes.items == null) {
            return;
        }
        for (String id : likes.items) {
            getOrCreate(posts, id, post.to_id).add(post);

        }
    }

    private void countPosts(Map<String, Map<String, List<WallPost>>> posts, Map.Entry<User, List<WallPost>> entry,
            WallPost post) {
        if (post.from_id != entry.getKey().id) {
            getOrCreate(posts, post.from_id, post.to_id).add(post);
        }
    }

    private List<WallPost> getOrCreate(Map<String, Map<String, List<WallPost>>> posts, String from_id, String to_id) {
        Map<String, List<WallPost>> fromUser = posts.get(from_id);
        if (fromUser == null) {
            fromUser = new HashMap<>();
            posts.put(from_id, fromUser);
        }
        List<WallPost> toUser = fromUser.get(to_id);
        if (toUser == null) {
            toUser = new ArrayList<>();
            fromUser.put(to_id, toUser);
        }
        return toUser;
    }

}
