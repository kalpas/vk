package kalpas.VKCore.stats;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Likes;
import kalpas.VKCore.simple.VKApi.Users;
import kalpas.VKCore.simple.VKApi.Wall;
import kalpas.VKCore.simple.VKApi.WallComments;
import kalpas.VKCore.stats.DO.EdgeProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
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

    public Multimap<User, Map.Entry<EdgeProperties, User>> getInteractions(String id) {
        List<WallPost> list = wall.getPosts(id, true);
        likes.getLikes(list, true);

        Multimap<User, Map.Entry<EdgeProperties, User>> multimap = ArrayListMultimap.create();

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

        return multimap;

    }

}
