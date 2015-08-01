package kalpas.VKCore.simple.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Users;
import kalpas.VKCore.simple.graph.UserGraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class Core {

    private Logger  logger = LogManager.getLogger(Core.class);

    @Inject
    private Friends FRIENDS;

    @Inject
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
