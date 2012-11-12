package kalpas.simple.DO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kalpas.simple.VKApi.Friends;
import kalpas.simple.VKApi.Likes;
import kalpas.simple.VKApi.Users;
import kalpas.simple.VKApi.Wall;
import kalpas.simple.VKApi.WallComments;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;

public class NewFriendsGraph {

    public SetMultimap<UserRelation, User> edges  = HashMultimap.create();

    private final static String                selfId = "1080446";

    @Inject
    private Friends                            FRIENDS;
    @Inject
    private Users                              USERS;
    @Inject
    private Wall                               WALL;
    @Inject
    private Likes                              LIKES;
    @Inject
    private WallComments                       WALLCOMMENTS;

    Logger                                     logger = Logger.getLogger(NewFriendsGraph.class);

    public NewFriendsGraph() {
    }

    public void getMyFriends() {
        USERS.addFields("uid", "first_name", "last_name", "nickname", "screen_name", "sex", "bdate", "city", "country",
                "timezone", "photo", "photo_medium", "photo_big", "has_mobile", "contacts", "education", "online",
                "counters", "lists", "can_post", "can_see_all_posts", "activity", "last_seen", "relation", "exports",
                "wall_comments", "connections", "interests", "movies", "tv", "books", "games", "about", "domain");

        User self = USERS.get(selfId);
        List<User> friends = FRIENDS.get(self);
        logger.info(friends.size() + " friends");
        friends = USERS.get(friends);
        
        logger.debug("got friends");

        Map<User, List<WallPost>> wallPosts = new HashMap<>();
        for (User user : friends) {
            List<WallPost> wall = WALL.get(user.uid);
            logger.debug("got wall");
            wall = LIKES.get(wall);
            logger.debug("got likes");
            wall = WALLCOMMENTS.get(wall);
            logger.debug("got commets");
            wallPosts.put(user, wall);
        }
        logger.debug("got all info");

        Map<String, Map<String, List<WallPost>>> posts = new HashMap<>();
        Map<String, Map<String, List<WallPost>>> likes = new HashMap<>();
        Map<String, Map<String, List<WallPost>>> comments = new HashMap<>();
        for (Map.Entry<User, List<WallPost>> entry : wallPosts.entrySet()) {
            for (WallPost post: entry.getValue()){
                countPosts(posts, entry, post);
                countLikes(likes, post);
                countComments(comments, post);
            }
        }
        
        logger.debug("counters");
        

        UserRelation myRelations = new UserRelation(self);
        for(User user: friends){
            getCounters(posts, likes, comments, myRelations, user);
            edges.put(myRelations, user);
        }

        Map<User, List<User>> friendsOfFriends = FRIENDS.get(friends);

        logger.debug("got friends of a friends");

        List<User> usersOfInteres = new ArrayList<>();
        usersOfInteres.add(self);
        usersOfInteres.addAll(friends);
        for (Map.Entry<User, List<User>> entry : friendsOfFriends.entrySet()) {
            for (User user : entry.getValue()) {
                if (usersOfInteres.contains(user)) {
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
        counters.wallPosts = posts.get(relations.user.uid).get(user.uid).size();
        counters.likes = likes.get(relations.user.uid).get(user.uid).size();
        counters.comments = comments.get(relations.user.uid).get(user.uid).size();
        relations.relations.put(user.uid, counters);
    }

    private void countComments(Map<String, Map<String, List<WallPost>>> posts, WallPost post) {
        for (Comment comment : post.comments.comments) {
            getOrCreate(posts, comment.from_id, post.to_id).add(post);
        }
    }

    private void countLikes(Map<String, Map<String, List<WallPost>>> posts, WallPost post) {
        for (String uid : post.likes.users) {
            getOrCreate(posts, uid, post.to_id).add(post);

        }
    }

    private void countPosts(Map<String, Map<String, List<WallPost>>> posts, Map.Entry<User, List<WallPost>> entry,
            WallPost post) {
        if(post.from_id != entry.getKey().uid){
            List<WallPost> toUser = getOrCreate(posts, post.from_id, post.to_id);
            toUser.add(post);
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
