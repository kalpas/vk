package kalpas.VKCore.stats;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kalpas.VKCore.simple.DO.Comment;
import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.DO.WallPost.Attachment;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Users;
import kalpas.VKCore.simple.VKApi.Wall;
import kalpas.VKCore.simple.VKApi.WallComments;
import kalpas.VKCore.simple.helper.CSVHelper;
import kalpas.VKCore.stats.DO.EdgeProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;

//FIXME damn, rewrite it!
@Deprecated
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
    private WallComments comments;

    private Logger       logger = LogManager.getLogger(WallStats.class);

    public Multimap<User, Map.Entry<EdgeProperties, User>> getRepostsNet(String id) {
        return getRepostsNet(id, null);
    }

    public Multimap<User, Map.Entry<EdgeProperties, User>> getRepostsNet(String id, Integer count) {
        List<WallPost> list = null;
        if (count != null) {
            try {
                list = wall.getPosts(id, true, count);
            } catch (VKError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                list = wall.getPosts(id, true);
            } catch (VKError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // getting reposts only
        try {
            likes.getLikes(list, true);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Multimap<User, Map.Entry<EdgeProperties, User>> multimap = ArrayListMultimap.create();

        Map<User, Multiset<User>> map = new HashMap<>();

        for (WallPost post : list) {
            String authorId = post.signer_id != null ? post.signer_id : post.from_id;

            Multiset<User> set = map.get(new User(authorId));
            if (set == null) {
                set = HashMultiset.create();
                User author = null;
                try {
                    author = users.get(authorId);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                multimap.put(author, null);
                map.put(author, set);
            }

            if (post.likes == null || post.likes.items == null) {
                continue;
            }

            for (String id1 : post.likes.items) {// FIXME NPE
                User element = null;
                try {
                    element = users.get(id1);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                set.add(element);
                multimap.put(element, null);
            }
        }

        Iterator<Map.Entry<User, Multiset<User>>> iterator = map.entrySet().iterator();
        Iterator<Multiset.Entry<User>> entryIterator;
        while (iterator.hasNext()) {
            Map.Entry<User, Multiset<User>> mapEntry = iterator.next();
            entryIterator = mapEntry.getValue().entrySet().iterator();
            while (entryIterator.hasNext()) {
                Multiset.Entry<User> multisetEntry = entryIterator.next();
                EdgeProperties edgeProperties = new EdgeProperties();
                edgeProperties.reposts = multisetEntry.getCount();
                multimap.put(mapEntry.getKey(), new AbstractMap.SimpleEntry<EdgeProperties, User>(edgeProperties,
                        multisetEntry.getElement()));
            }
        }

        return multimap;

    }
    
    public Multimap<User, Map.Entry<EdgeProperties, User>> getRepostsNet4Period(String id, Integer days) {
        List<WallPost> list = null;
        try {
            list = wall.getPosts4Period(id, true, days);
        } catch (VKError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // getting reposts only
        try {
            likes.getLikes(list, true);
        } catch (VKError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Multimap<User, Map.Entry<EdgeProperties, User>> multimap = ArrayListMultimap.create();

        Map<User, Multiset<User>> map = new HashMap<>();

        for (WallPost post : list) {
            String authorId = post.signer_id != null ? post.signer_id : post.from_id;

            Multiset<User> set = map.get(new User(authorId));
            if (set == null) {
                set = HashMultiset.create();
                User author = null;
                try {
                    author = users.get(authorId);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                multimap.put(author, null);
                map.put(author, set);
            }

            if (post.likes == null || post.likes.items == null) {
                continue;
            }

            for (String id1 : post.likes.items) {// FIXME NPE
                User element = null;
                try {
                    element = users.get(id1);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                set.add(element);
                multimap.put(element, null);
            }
        }

        Iterator<Map.Entry<User, Multiset<User>>> iterator = map.entrySet().iterator();
        Iterator<Multiset.Entry<User>> entryIterator;
        while (iterator.hasNext()) {
            Map.Entry<User, Multiset<User>> mapEntry = iterator.next();
            entryIterator = mapEntry.getValue().entrySet().iterator();
            while (entryIterator.hasNext()) {
                Multiset.Entry<User> multisetEntry = entryIterator.next();
                EdgeProperties edgeProperties = new EdgeProperties();
                edgeProperties.reposts = multisetEntry.getCount();
                multimap.put(mapEntry.getKey(), new AbstractMap.SimpleEntry<EdgeProperties, User>(edgeProperties,
                        multisetEntry.getElement()));
            }
        }

        return multimap;

    }

    public Multimap<User, Map.Entry<EdgeProperties, User>> getInteractions(String id, Integer count) {
        List<WallPost> list = null;
        if (count != null) {
            try {
                list = wall.getPosts(id, true, count);
            } catch (VKError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                list = wall.getPosts(id, true);
            } catch (VKError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            likes.getLikes(list);
        } catch (VKError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            comments.get(list);
        } catch (VKError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Multimap<User, Map.Entry<EdgeProperties, User>> multimap = ArrayListMultimap.create();

        Map<User, Multiset<User>> likesMap = new HashMap<>();

        for (WallPost post : list) {
            String authorId = post.signer_id != null ? post.signer_id : post.from_id;

            Multiset<User> set = likesMap.get(new User(authorId));
            if (set == null) {
                set = HashMultiset.create();
                User author = null;
                try {
                    author = users.get(authorId);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                multimap.put(author, null);
                likesMap.put(author, set);
            }

            if (post.likes == null || post.likes.items == null) {
                continue;
            }

            for (String id1 : post.likes.items) {
                User element = null;
                try {
                    element = users.get(id1);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                set.add(element);
                multimap.put(element, null);
            }
        }

        for (WallPost post : list) {
            String authorId = post.signer_id != null ? post.signer_id : post.from_id;

            Multiset<User> set = likesMap.get(new User(authorId));
            if (set == null) {
                set = HashMultiset.create();
                User author = null;
                try {
                    author = users.get(authorId);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                multimap.put(author, null);
                likesMap.put(author, set);
            }

            if (post.comments == null || post.comments.comments == null) {
                continue;
            }

            for (Comment comment : post.comments.comments) {
                User element = null;
                try {
                    element = users.get(comment.from_id);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                set.add(element);
                multimap.put(element, null);
            }
        }

        Iterator<Map.Entry<User, Multiset<User>>> iterator = likesMap.entrySet().iterator();
        Iterator<Multiset.Entry<User>> entryIterator;
        while (iterator.hasNext()) {
            Map.Entry<User, Multiset<User>> mapEntry = iterator.next();
            entryIterator = mapEntry.getValue().entrySet().iterator();
            while (entryIterator.hasNext()) {
                Multiset.Entry<User> multisetEntry = entryIterator.next();
                EdgeProperties edgeProperties = new EdgeProperties();
                edgeProperties.likes = multisetEntry.getCount();
                multimap.put(mapEntry.getKey(), new AbstractMap.SimpleEntry<EdgeProperties, User>(edgeProperties,
                        multisetEntry.getElement()));
            }
        }


        return multimap;
    }

    public Multimap<User, Map.Entry<EdgeProperties, User>> getInteractions4Period(String id, Integer days) {
        List<WallPost> list = null;
        try {
            list = wall.getPosts4Period(id, true, days);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            likes.getLikes(list);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            comments.get(list);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Multimap<User, Map.Entry<EdgeProperties, User>> multimap = ArrayListMultimap.create();

        Map<User, Multiset<User>> likesMap = new HashMap<>();

        for (WallPost post : list) {
            String authorId = post.signer_id != null ? post.signer_id : post.from_id;

            Multiset<User> set = likesMap.get(new User(authorId));
            if (set == null) {
                set = HashMultiset.create();
                User author = null;
                try {
                    author = users.get(authorId);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                multimap.put(author, null);
                likesMap.put(author, set);
            }

            if (post.likes == null || post.likes.items == null) {
                continue;
            }

            for (String id1 : post.likes.items) {
                User element = null;
                try {
                    element = users.get(id1);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                set.add(element);
                multimap.put(element, null);
            }
        }

        for (WallPost post : list) {
            String authorId = post.signer_id != null ? post.signer_id : post.from_id;

            Multiset<User> set = likesMap.get(new User(authorId));
            if (set == null) {
                set = HashMultiset.create();
                User author = null;
                try {
                    author = users.get(authorId);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                multimap.put(author, null);
                likesMap.put(author, set);
            }

            if (post.comments == null || post.comments.comments == null) {
                continue;
            }

            for (Comment comment : post.comments.comments) {
                User element = null;
                try {
                    element = users.get(comment.from_id);
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                set.add(element);
                multimap.put(element, null);
            }
        }

        Iterator<Map.Entry<User, Multiset<User>>> iterator = likesMap.entrySet().iterator();
        Iterator<Multiset.Entry<User>> entryIterator;
        while (iterator.hasNext()) {
            Map.Entry<User, Multiset<User>> mapEntry = iterator.next();
            entryIterator = mapEntry.getValue().entrySet().iterator();
            while (entryIterator.hasNext()) {
                Multiset.Entry<User> multisetEntry = entryIterator.next();
                EdgeProperties edgeProperties = new EdgeProperties();
                edgeProperties.likes = multisetEntry.getCount();
                multimap.put(mapEntry.getKey(), new AbstractMap.SimpleEntry<EdgeProperties, User>(edgeProperties,
                        multisetEntry.getElement()));
            }
        }

        return multimap;
    }

    public void saveDynamics(String id, Integer count) {
        String separator = "===================";

        // FIXME duplicated code
        List<WallPost> list = null;
        if (count != null) {
            try {
                list = wall.getPosts(id, true, count);
            } catch (VKError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                list = wall.getPosts(id, true);
            } catch (VKError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            likes.getLikes(list);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            comments.get(list);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        List<WallPost> groupPosts = new ArrayList<>();
        List<WallPost> userPost = new ArrayList<>();

        Set<User> adminStuff = new HashSet<>();
        Set<User> activeUsers = new HashSet<>();

        for (WallPost post : list) {
            if (post.signer_id != null) {
                groupPosts.add(post);
                try {
                    adminStuff.add(users.get(post.signer_id));
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                userPost.add(post);
                try {
                    activeUsers.add(users.get(post.from_id));
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        logger.info(list.size() + " total posts");
        logger.info(groupPosts.size() + " posts from administration");
        logger.info(userPost.size() + " posts from subscribers");
        logger.info(separator);
        logger.info(adminStuff.size() + " active admins");
        logger.info(activeUsers.size() + " active users");

        // ************ writing csv **************************

        try (CSVHelper helper = new CSVHelper(id + "_" + new DateTime().getMillis())) {
            helper.writeHeader("id", "isAdmin", "text", "attach", "likes", "comments", "reposts");
            writePosts(groupPosts, helper, true);
            writePosts(userPost, helper);
        } catch (IOException e) {
            logger.error("error creating file", e);
        }

        // ************ end writing csv **************************
    }

    public void saveDynamics4Period(String id, Integer days) {
        String separator = "===================";

        // FIXME duplicated code
        List<WallPost> list = null;
        try {
            list = wall.getPosts4Period(id, true, days);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            likes.getLikes(list);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            comments.get(list);
        } catch (VKError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        List<WallPost> groupPosts = new ArrayList<>();
        List<WallPost> userPost = new ArrayList<>();

        Set<User> adminStuff = new HashSet<>();
        Set<User> activeUsers = new HashSet<>();

        for (WallPost post : list) {
            if (post.signer_id != null) {
                groupPosts.add(post);
                try {
                    adminStuff.add(users.get(post.signer_id));
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                userPost.add(post);
                try {
                    activeUsers.add(users.get(post.from_id));
                } catch (VKError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        logger.info(list.size() + " total posts");
        logger.info(groupPosts.size() + " posts from administration");
        logger.info(userPost.size() + " posts from subscribers");
        logger.info(separator);
        logger.info(adminStuff.size() + " active admins");
        logger.info(activeUsers.size() + " active users");

        // ************ writing csv **************************

        try (CSVHelper helper = new CSVHelper(id + "_" + new DateTime().getMillis())) {
            helper.writeHeader("id", "isAdmin", "text", "attach", "likes", "comments", "reposts");
            writePosts(groupPosts, helper, true);
            writePosts(userPost, helper);
        } catch (IOException e) {
            logger.error("error creating file", e);
        }

        // ************ end writing csv **************************
    }

    private void writePosts(List<WallPost> groupPosts, CSVHelper helper) throws IOException {
        writePosts(groupPosts, helper, false);
    }

    private void writePosts(List<WallPost> groupPosts, CSVHelper helper, boolean isAdmin) throws IOException {
        for (WallPost post : groupPosts) {
            List<String> types = new ArrayList<>();
            if (post.attachments != null) {
                for (Attachment attach : post.attachments) {
                    types.add(attach.type);
                }
            }
            helper.writeRow(post.id, isAdmin ? "1" : "0", post.text, Joiner.on(",").join(types),
                    post.likes.count.toString(), post.comments.count.toString(), post.reposts.count.toString());
        }
    }

}
