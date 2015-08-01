package kalpas.VKCore.simple.graph;

import kalpas.VKCore.simple.DO.User;

public class GraphNode {

    public User                 user;

    // public Map<User, GraphEdge> inbound;
    // public Map<User, GraphEdge> outbound;

    public GraphNode(User user) {
        this.user = user;

    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GraphNode other = (GraphNode) obj;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}
