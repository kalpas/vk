package kalpas.VKCore.stats;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.VKApi.Groups;
import kalpas.VKCore.simple.VKApi.Users;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

public class GroupStats {
    
    @Inject
    private Groups groups;

    @Inject
    private Friends friends;

    @Inject
    private Users   users;

    public Multimap<User, User> getMemberNetwork(String gid) {
        List<User> members = groups.getMembers(gid);
        members = users.get(members);
        Map<User, List<User>> result = friends.get(members);
        
        Multimap<User, User> memberNetwork = ArrayListMultimap.<User, User> create();
        Iterator<Map.Entry<User, List<User>>> iterator = result.entrySet().iterator();
        Map.Entry<User, List<User>> entry = null;
        List<User> friends = null;
        while(iterator.hasNext()){
            entry = iterator.next();
            friends = entry.getValue();
            for(User user : friends){
                if(members.contains(user)){
                    memberNetwork.put(entry.getKey(), user);
                }
            }
        }

        return memberNetwork;
    }


}
