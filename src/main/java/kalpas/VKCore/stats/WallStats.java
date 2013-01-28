package kalpas.VKCore.stats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
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
import com.google.common.collect.Multimap;
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
    private WallComments comments;

    private Logger       logger = LogManager.getLogger(WallStats.class);

    public Multimap<User, Map.Entry<EdgeProperties, User>> getInteractions(String id) {
        String separator = "===================";

        List<WallPost> list = wall.getPosts(id, true);

        likes.getLikes(list, true);
        comments.get(list);

        logger.info(list.size() + " posts");

        List<WallPost> groupPosts = new ArrayList<>();
        List<WallPost> userPost = new ArrayList<>();

        List<User> adminStuff = new ArrayList<>();
        List<User> activeUsers = new ArrayList<>();

        for (WallPost post : list) {
            if (post.signer_id != null) {
                groupPosts.add(post);
                adminStuff.add(users.get(post.signer_id));
            } else {
                userPost.add(post);
                activeUsers.add(users.get(post.from_id));
            }
        }

        logger.info(groupPosts.size() + " posts from administration");
        logger.info(userPost.size() + " posts from subscribers");
        logger.info(separator);
        logger.info(adminStuff.size() + " active admins");
        logger.info(activeUsers.size() + " active users");

        // ************ writing csv **************************

        try (CSVHelper helper = new CSVHelper(id + new DateTime().getMillis())) {
            helper.writeHeader("id", "isAdmin", "text", "attach", "likes", "comments", "reposts");
            writePosts(groupPosts, helper);
            writePosts(userPost, helper);
        } catch (IOException e) {
            logger.error("error creating file", e);
        }

        // ************ end writing csv **************************

        
        Multimap<User, Map.Entry<EdgeProperties, User>> multimap = ArrayListMultimap.create();

        /*
        Map<User, Multiset<User>> map = new HashMap<>();
        for (WallPost post : list) {
            User author;
            if (post.signer_id != null) {
                author = users.get(post.signer_id);
            } else {
                author = users.get(post.from_id);
            }

            Multiset<User> set = map.get(author);

            if (set == null) {
                set = HashMultiset.create();
                map.put(author, set);
            }

            for (String uid : post.likes.users) {
                User element = users.get(uid);
                set.add(element);
                multimap.put(element, null);
            }
        }

        Iterator<Map.Entry<User, Multiset<User>>> iterator = map.entrySet().iterator();
        Iterator<Multiset.Entry<User>> entryIterator;
        while (iterator.hasNext()) {
            Entry<User, Multiset<User>> mapEntry = iterator.next();
            entryIterator = mapEntry.getValue().entrySet().iterator();
            while (entryIterator.hasNext()) {
                Multiset.Entry<User> multisetEntry = entryIterator.next();
                EdgeProperties edgeProperties = new EdgeProperties();
                edgeProperties.reposts = multisetEntry.getCount();
                multimap.put(mapEntry.getKey(), new AbstractMap.SimpleEntry<EdgeProperties, User>(edgeProperties,
                        multisetEntry.getElement()));
            }
        }
        */

        return multimap;

    }

    private void writePosts(List<WallPost> groupPosts, CSVHelper helper) throws IOException {
        for (WallPost post : groupPosts) {
            List<String> types = new ArrayList<>();
            if (post.attachments != null) {
                for (Attachment attach : post.attachments) {
                    types.add(attach.type);
                }
            }
            helper.writeRow(post.id, "1", post.text, Joiner.on(",").join(types), post.likes.count.toString(),
                    post.comments.count.toString(), post.reposts.count.toString());
        }
    }

}
