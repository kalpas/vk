package kalpas.simple;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kalpas.VK.VKUser;

import org.apache.commons.lang3.Validate;

public class FriendsGraph {

    private Set<VKUser>                  nodes = new HashSet<>();

    private Set<Map.Entry<String, String>> edges = new HashSet<>();

    public void add(VKUser person, List<VKUser> friends) {
        Validate.notNull(person);
        Validate.notNull(friends);

        nodes.add(person);
        nodes.addAll(friends);

        SimpleEntry<String, String> edge = null;
        for (VKUser friend : friends) {
            edge = new AbstractMap.SimpleEntry<String, String>(friend.getUid(), person.getUid());
            if (!edges.contains(edge)) {
                edges.add(new AbstractMap.SimpleEntry<String, String>(person.getUid(), friend.getUid()));
            }
        }

    }

    public void addInterconnections(Map<VKUser, List<VKUser>> friendsMap) {
        Validate.notNull(friendsMap);

        SimpleEntry<String, String> edge = null;

        for (Map.Entry<VKUser, List<VKUser>> entry : friendsMap.entrySet()) {
            if (!nodes.contains(entry.getKey()))
                continue;
            for (VKUser value : entry.getValue()) {
                if (!nodes.contains(value))
                    continue;
                edge = new AbstractMap.SimpleEntry<String, String>(value.getUid(), entry.getKey().getUid());
                if (!edges.contains(edge)) {
                    edges.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey().getUid(), value.getUid()));
                }
            }
        }

    }

    public void addAll(Map<VKUser, List<VKUser>> friendsMap) {
        Validate.notNull(friendsMap);

        SimpleEntry<String, String> edge = null;

        nodes.addAll(friendsMap.keySet());
        for (Map.Entry<VKUser, List<VKUser>> entry : friendsMap.entrySet()) {
            nodes.addAll(entry.getValue());
            for (VKUser value : entry.getValue()) {
                edge = new AbstractMap.SimpleEntry<String, String>(value.getUid(), entry.getKey().getUid());
                if (!edges.contains(edge)) {
                    edges.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey().getUid(), value.getUid()));
                }
            }
        }
    }

    public Set<VKUser> getNodes() {
        return nodes;
    }

    public Set<Map.Entry<String, String>> getEdges() {
        return edges;
    }

}
