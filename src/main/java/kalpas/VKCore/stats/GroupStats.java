package kalpas.VKCore.stats;

import java.util.Collections;
import java.util.List;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Groups;
import kalpas.VKCore.simple.VKApi.Users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

public class GroupStats {
    
    private Logger  logger = LogManager.getLogger(GroupStats.class);

    @Inject
    private Groups groups;

    @Inject
    private Friends friends;

    @Inject
    private Users   users;

    public Multimap<User, User> getMemberNetwork(String gid) {
        List<User> members = Collections.emptyList();
        try{
            members = groups.getMembers(gid);
        } catch (VKError e) {
            // TODO
            // place code to recover from error
            logger.error("vk returned error: {}", e.getMessage());
            logger.error(e.getRobustError());
        }

        logger.debug("members " + members.size());

        try {
            members = users.get(members);
        } catch (VKError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Multimap<User, User> memberNetwork = ArrayListMultimap.<User, User> create();

        int progressCounter = 0;
        for (User member : members) {
            memberNetwork.put(member, null);

            List<User> memberFriends = null;
            memberFriends = friends.get(member);
            if (memberFriends == null) {
                continue;
            }
            for (User friend : memberFriends) {
                if (members.contains(friend)) {
                    memberNetwork.put(member, friend);
                }
            }
            logger.info(progressCounter++ + " of " + members.size());
        }

        logger.debug("keys " + memberNetwork.keys().size());
        logger.debug("keySet " + memberNetwork.keySet().size());
        logger.debug("values " + memberNetwork.values().size());

        return memberNetwork;
    }


}
