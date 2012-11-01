package kalpas.simple.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.simple.VKClient;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Wall {

    private static final Gson   gson   = new Gson();

    private Logger              logger = Logger.getLogger(Wall.class);
    private VKClient            client;

    private static final String get    = "wall.get";

    static final MapJoiner      joiner = Joiner.on("&").withKeyValueSeparator("=");

    private Map<String, String> params = new HashMap<>();

    private Integer             count;

    @Inject
    public Wall(VKClient client) {
        this.client = client;
    }

    public Map.Entry<Integer, List<WallPost>> get(String ownerId) {
        List<WallPost> wallPosts = new ArrayList<>();
        int wallPostsCount = 0;

        InputStream result = client.send(get + "?" + buildRequest(ownerId));
        try {
            JsonParser parser = new JsonParser();
            JsonObject response = parser.parse(new InputStreamReader(result)).getAsJsonObject();
            JsonArray posts = response.getAsJsonArray("response");
            if (posts != null) {
                Iterator<JsonElement> iterator = posts.iterator();
                wallPostsCount = iterator.hasNext() ? iterator.next().getAsInt() : 0;
                while (iterator.hasNext()) {
                    wallPosts.add(gson.fromJson(iterator.next(), WallPost.class));
                }
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.error("exception while parsing json", e);
        }
        return new AbstractMap.SimpleEntry<Integer, List<WallPost>>(wallPostsCount, wallPosts);
    }

    protected String buildRequest(String ownerId) {
        params.put("owner_id", ownerId);
        return joiner.join(params);
    }

    public Wall addOffset(String offset) {
        params.put("offset", offset);
        return this;
    }

    public Wall addCount(Integer count) {
        params.put("count", count.toString());
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
