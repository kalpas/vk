package kalpas.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import kalpas.simple.DO.Like;
import kalpas.simple.VKApi.client.VKClient;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;

public class Likes {

    private Logger              logger = Logger.getLogger(Likes.class);

    private static final String get    = "likes.getList";
    private VKClient            client;

    @Inject
    private Gson                gson;
    @Inject
    private JsonParser          parser;

    private Map<String, String> params = new HashMap<>();

    @Inject
    private MapJoiner           joiner;

    @Inject
    public Likes(VKClient client) {
        this.client = client;
    }

    public Like get(String ownerId, String itemId) {
        params.put("owner_id", ownerId);
        return get(itemId);
    }

    public Like get(String itemId) {
        params.put("count", "1".equals(params.get("friends_only")) ? "100" : "1000");

        Like like = null;
        InputStream stream = client.send(get + "?" + buildRequest(itemId));
        JsonObject result = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
        if (result != null) {

            like = gson.fromJson(result.getAsJsonObject("response"), Like.class);
            if (!like.count.equals(like.users.length)) {
                logger.fatal("not implemented yet");
            }
        }

        return like;
    }

    protected String buildRequest(String itemId) {
        params.put("item_id", itemId);
        return joiner.join(params);
    }

    /**
     * could be post, comment, audio, video, note, sitepage
     */
    public Likes addType(String type) {
        params.put("type", type);
        return this;
    }

    public Likes addOwnerId(String ownerId) {
        params.put("owner_id", ownerId);
        return this;
    }

    public Likes addItemId(String itemId) {
        params.put("item_id", itemId);
        return this;
    }

    /**
     * likes,copies
     */
    public Likes addFilter(String filter) {
        params.put("filter", filter);
        return this;
    }

    /**
     * 
     * @param friendsOnly
     * @return
     */
    public Likes addFriemdsOnly(Integer friendsOnly) {
        params.put("friends_only", friendsOnly.toString());
        return this;
    }

}
