package kalpas.VKCore.simple.graph;

import java.util.HashMap;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class UserGraph {

    public Multimap<GraphNode, GraphNode> nodes = ArrayListMultimap.create();

    public Map<String, GraphEdge>         edges = new HashMap<String, GraphEdge>();

    private String edge(String in, String out) {
        return in + "#" + out;
    }

    public void addAll(User from, Iterable<User> users) {
        for (User to : users) {
            if (to != null) {
                nodes.put(new GraphNode(from), new GraphNode(to));
            }
        }

    }

}
