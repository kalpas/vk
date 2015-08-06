package net.kalpas.VKCore.simple.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.Friends;
import net.kalpas.VKCore.simple.VKApi.Users;
import net.kalpas.VKCore.simple.graph.UserGraph;

@Component
public class Core {

    private Logger  logger = LogManager.getLogger(Core.class);

    @Autowired
    private Friends FRIENDS;

    @Autowired
    private Users   USERS;

    public UserGraph buildGraph(String id, boolean includingUid) {
        UserGraph graph = new UserGraph();
        try {
            User forUid = USERS.get(id);
            List<User> friends = FRIENDS.get(forUid);

            Set<User> base = new HashSet<>(friends);
            if (includingUid) {
                base.add(forUid);
            }

            Set<User> to = null;
            for (User from : base) {
                to = new HashSet<>(FRIENDS.get(from));
                Set<User> intersection = Sets.intersection(base, to);
                graph.addAll(from, intersection);
            }
        } catch (VKError e) {
            logger.fatal(e);
        }
        return graph;

    }

}
