package kalpas.simple.DO;

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
        friends.add(self);
        friends = USERS.get(friends);

        Map<User, List<WallPost>> wallPosts = new HashMap<>();
        for (User user : friends) {
            List<WallPost> wall = WALL.get(user.uid);
            wall = LIKES.get(wall);
            wall = WALLCOMMENTS.get(wall);
            wallPosts.put(user, wall);
        }

        UserRelation myRelations = new UserRelation(self);
        Map<User, RelationCounters> relationsList = myRelations.relations;
        for (WallPost post : wallPosts.get(self)) {
            countLikes(friends, relationsList, post);
            countWallPosts(self, friends, relationsList, post);
            countComments(self, friends, relationsList, post);
        }
        edges.putAll(myRelations, friends);

        Map<User, List<User>> friendsOfFriends = FRIENDS.get(friends);
        for (Map.Entry<User, List<User>> entry : friendsOfFriends.entrySet()) {
            for (User user : entry.getValue()) {
                if (friends.contains(user)) {
                    edges.put(new UserRelation(entry.getKey()), user);
                }
            }
        }
        logger.info(edges.size() + "edges");
    }

    private void countComments(User self, List<User> friends, Map<User, RelationCounters> relationsList, WallPost post) {
        User user;
        RelationCounters counters;
        for (Comment comment : post.comments.comments) {
            if (comment.from_id != self.uid) {
                user = new User(post.from_id);
                counters = findOrCreate(friends, relationsList, user);
                counters.comments++;
            }
        }
    }

    private void countWallPosts(User self, List<User> friends, Map<User, RelationCounters> relationsList, WallPost post) {
        User user;
        RelationCounters counters;
        if (post.from_id != self.uid) {
            user = new User(post.from_id);
            counters = findOrCreate(friends, relationsList, user);
            counters.wallPosts++;
        }
    }

    private void countLikes(List<User> friends, Map<User, RelationCounters> relationsList, WallPost post) {
        User user;
        RelationCounters counters;
        for (String uid : post.likes.users) {
            user = new User(uid);
            counters = findOrCreate(friends, relationsList, user);
            counters.likes++;
        }
    }

    private RelationCounters findOrCreate(List<User> friends, Map<User, RelationCounters> relationsList, User user) {
        RelationCounters counters;
        if (relationsList.containsKey(user)) {
            counters = relationsList.get(user);
        } else {
            counters = new RelationCounters();
            if (friends.contains(user)) {
                user = friends.get(friends.indexOf(user));
            }
            relationsList.put(user, counters);
        }
        return counters;
    }
}
