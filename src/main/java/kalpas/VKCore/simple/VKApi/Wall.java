package kalpas.VKCore.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

import org.apache.log4j.Logger;

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

    // FIXME wallcount

    private static final Integer MAX_GET_COUNT = 100;

    @Inject
    private Gson                 gson;
    @Inject
    private JsonParser           parser;

    private Logger               logger        = Logger.getLogger(Wall.class);
    private VKClient             client;

    private static final String  get           = "wall.get";

    @Inject
    private MapJoiner            joiner;

    private Map<String, String>  params        = new HashMap<>();

    private Integer                  count;

    @Inject
    public Wall(VKClient client) {
        this.client = client;
    }


    public List<WallPost> get(String ownerId) {
        List<WallPost> wallPosts = new ArrayList<>();
        int wallPostsCount = MAX_GET_COUNT;

        params.put("count", MAX_GET_COUNT.toString());
        params.put("offset", "0");
        wallPostsCount = get(ownerId, wallPosts);
        if (count != null && count != 0) {
            wallPostsCount = count;
        }

        List<VKAsyncResult> futures = new ArrayList<>();
        for (Integer step = MAX_GET_COUNT; step < wallPostsCount; step += MAX_GET_COUNT) {
            params.put("count", MAX_GET_COUNT.toString());
            params.put("offset", step.toString());
            futures.add(client.sendAsync(get + "?" + buildRequest(ownerId)));
        }

        VKAsyncResult future;
        while (!futures.isEmpty()) {
            Iterator<VKAsyncResult> iterator = futures.iterator();
            while (iterator.hasNext()) {
                future = iterator.next();
                if (!future.isDone()) {
                    continue;
                }
                iterator.remove();
                get(wallPosts, future.get());
            }
        }

        return wallPosts;

    }

    private int get(String ownerId, List<WallPost> wallPosts) {
        InputStream result = client.send(get + "?" + buildRequest(ownerId));
        return get(wallPosts, result);
    }

    private int get(List<WallPost> wallPosts, InputStream result) {
        int wallPostsCount = 0;
        try {
            JsonObject response = parser.parse(new InputStreamReader(result)).getAsJsonObject();
            JsonArray posts = response.getAsJsonArray("response");
            if (posts != null) {
                Iterator<JsonElement> iterator = posts.iterator();
                wallPostsCount = iterator.hasNext() ? iterator.next().getAsInt() : 0;
                while (iterator.hasNext()) {
                    wallPosts.add(gson.fromJson(iterator.next(), WallPost.class));
                }
            } else {
                logger.error("error " + response.toString());
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.error("exception while parsing json", e);
        }
        return wallPostsCount;
    }

    protected String buildRequest(String ownerId) {
        params.put("owner_id", ownerId);
        return joiner.join(params);
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

    public Wall addCount(int count) {
        this.count = count;
        return this;
    }

}
