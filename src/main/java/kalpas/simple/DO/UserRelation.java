package kalpas.simple.DO;

import java.util.HashMap;
import java.util.Map;

public class UserRelation {

    public User user;
    
    public Map<String, RelationCounters> relations = new HashMap<>();

    public UserRelation(User user) {
        this.user = user;
    }
}
