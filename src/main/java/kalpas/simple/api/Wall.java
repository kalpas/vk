package kalpas.simple.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kalpas.simple.VKClient;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.inject.Inject;

public class Wall {

    private Logger              logger = Logger.getLogger(Wall.class);
    private VKClient            client;

    private static final String get    = "wall.get";

    static final MapJoiner      joiner = Joiner.on("&").withKeyValueSeparator("=");

    private Map<String, String> params = new HashMap<>();

    @Inject
    public Wall(VKClient client) {
        this.client = client;
    }

    public List<WallPost> get(String ownerId) {
        JSONObject result = client.send(get + "?" + buildRequest(ownerId));
        return null;
    }

    protected String buildRequest(String ownerId) {
        params.put("owner_id", ownerId);
        return joiner.join(params);
    }

    public Wall addOffset(String offset) {
        params.put("offset", offset);
        return this;
    }

    public Wall addCount(String count) {
        params.put("count", count);
        return this;
    }

    // owner, others,all
    public Wall addFilter(String filter) {
        params.put("filter", filter);
        return this;
    }

    public Wall extended() {
        params.put("extended", "1");
        return this;
    }

}
