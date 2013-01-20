package kalpas.VKCore.simple.DO;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

public class FriendsGraph {

    private Set<User>                  nodes = new HashSet<>();

    private Set<Map.Entry<String, String>> edges = new HashSet<>();

    public void add(User person, List<User> friends) {
        Validate.notNull(person);
        Validate.notNull(friends);

        nodes.add(person);
        nodes.addAll(friends);

        SimpleEntry<String, String> edge = null;
        for (User friend : friends) {
            edge = new AbstractMap.SimpleEntry<String, String>(friend.uid, person.uid);
            if (!edges.contains(edge)) {
                edges.add(new AbstractMap.SimpleEntry<String, String>(person.uid, friend.uid));
            }
        }

    }

    public void addInterconnections(Map<User, List<User>> friendsMap) {
        Validate.notNull(friendsMap);

        SimpleEntry<String, String> edge = null;

        for (Map.Entry<User, List<User>> entry : friendsMap.entrySet()) {
            if (!nodes.contains(entry.getKey()))
                continue;
            for (User value : entry.getValue()) {
                if (!nodes.contains(value))
                    continue;
                edge = new AbstractMap.SimpleEntry<String, String>(value.uid, entry.getKey().uid);
                if (!edges.contains(edge)) {
                    edges.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey().uid, value.uid));
                }
            }
        }

    }

    public void addAll(Map<User, List<User>> friendsMap) {
        Validate.notNull(friendsMap);

        SimpleEntry<String, String> edge = null;

        nodes.addAll(friendsMap.keySet());
        for (Map.Entry<User, List<User>> entry : friendsMap.entrySet()) {
            nodes.addAll(entry.getValue());
            for (User value : entry.getValue()) {
                edge = new AbstractMap.SimpleEntry<String, String>(value.uid, entry.getKey().uid);
                if (!edges.contains(edge)) {
                    edges.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey().uid, value.uid));
                }
            }
        }
    }

    public Set<User> getNodes() {
        return nodes;
    }

    public Set<Map.Entry<String, String>> getEdges() {
        return edges;
    }

}
