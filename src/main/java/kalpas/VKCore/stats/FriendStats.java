package kalpas.VKCore.stats;

import java.util.List;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

public class FriendStats {

    private Logger logger = LogManager.getLogger(FriendStats.class);
    
    @Inject
    private Friends friends;

    @Inject
    private Users   users;

    public Multimap<User, User> getNetwork(String uid, boolean withMe) {
        User me = users.get(uid);
        List<User> friendList = null;
        try{
            friendList = friends.get(uid);
        }catch(VKError e){
            logger.error(e);
            return null;
        }
        friendList = users.get(friendList);
        
        Multimap<User, User> network = ArrayListMultimap.create();
        if (withMe) {
            for (User friend : friendList) {
                network.put(me, friend);
            }
        }

        List<User> list = null;
        for (User friend : friendList) {
            try {
                list = friends.get(friend);
            } catch (VKError e) {
                logger.error(e);
                return null;
            }
            for (User user : list) {
                if (friendList.contains(user)) {
                    user = list.get(list.indexOf(user));// TODO consider smth
                                                        // a bit more beautiful
                    network.put(friend, user);
                } else if (withMe && user.equals(me)) {
                    network.put(friend, me);
                }
            }
        }

        return network;
    }

}
