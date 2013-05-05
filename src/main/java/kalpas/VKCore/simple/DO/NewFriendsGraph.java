package kalpas.VKCore.simple.DO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.WallPost.Comments;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Users;
import kalpas.VKCore.simple.VKApi.Wall;
import kalpas.VKCore.simple.VKApi.WallComments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;

public class NewFriendsGraph {

    public SetMultimap<UserRelation, User> edges  = HashMultimap.create();

    private final static String            selfId = "1080446";

    @Inject
    private Friends                        FRIENDS;
    @Inject
    private Users                          USERS;
    @Inject
    private Wall                           WALL;
    @Inject
    private Likes                          LIKES;
    @Inject
    private WallComments                   WALLCOMMENTS;

    Logger                                 logger = LogManager.getLogger(NewFriendsGraph.class);

    public NewFriendsGraph() {
    }

    public void getMyFriends() {
        USERS.addFields("uid", "first_name", "last_name", "nickname", "screen_name", "sex", "bdate", "city", "country",
                "timezone", "photo", "photo_medium", "photo_big", "has_mobile", "contacts", "education", "online",
                "counters", "lists", "can_post", "can_see_all_posts", "activity", "last_seen", "relation", "exports",
                "wall_comments", "connections", "interests", "movies", "tv", "books", "games", "about", "domain");
        WALL.addCount(300);
        LIKES.addType("post");

        User self = USERS.get(selfId);
        List<User> friends = null;
        try {// FIXME fix later
            friends = FRIENDS.get(self);
        } catch (VKError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.info(friends.size() + " friends");
        friends = USERS.get(friends);

        logger.debug("got friends");

        Map<User, List<WallPost>> wallPosts = new HashMap<>();
        int i = 0;
        int size = friends.size();
        for (User user : friends) {
            List<WallPost> wall = WALL.get(user.uid);
            logger.debug("got wall");
            wall = LIKES.get(wall);
            logger.debug("got likes");
            wall = WALLCOMMENTS.get(wall);
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
        counters.wallPosts = tryGet(posts, relations.user.uid, user.uid);
        counters.likes = tryGet(likes, relations.user.uid, user.uid);
        counters.comments = tryGet(comments, relations.user.uid, user.uid);
        relations.relations.put(user.uid, counters);
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
        if (likes == null || likes.users == null) {
            return;
        }
        for (String uid : likes.users) {
            getOrCreate(posts, uid, post.to_id).add(post);

        }
    }

    private void countPosts(Map<String, Map<String, List<WallPost>>> posts, Map.Entry<User, List<WallPost>> entry,
            WallPost post) {
        if (post.from_id != entry.getKey().uid) {
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
