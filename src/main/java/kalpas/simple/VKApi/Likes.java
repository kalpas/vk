package kalpas.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.simple.DO.Like;
import kalpas.simple.DO.WallPost;
import kalpas.simple.VKApi.client.VKClient;
import kalpas.simple.VKApi.client.VKClient.VKAsyncResult;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
    
    public void get(List<WallPost> posts) {
        Integer step = "1".equals(params.get("friends_only")) ? 100:1000;
        params.put("count", step.toString());
        params.put("offset", "0");
        
        Map<WallPost, VKAsyncResult> futures = new HashMap<>();
        
        for (WallPost post : posts) {
            params.put("owner_id", post.to_id);
            futures.put(post, client.sendAsync(get + "?" + buildRequest(post.id)));
        }
        
        Like like;
        VKAsyncResult result;
        Map.Entry<WallPost, VKAsyncResult> entry;
        Iterator<Map.Entry<WallPost, VKAsyncResult>> iteartor;

        while(!futures.isEmpty()){
            iteartor = futures.entrySet().iterator();
            while (iteartor.hasNext()) {
                entry = iteartor.next();
                result = entry.getValue();
                if (!result.isDone()) {
                    continue;
                }
                iteartor.remove();
                like = getChunk(result.get());
                entry.getKey().likes = like;
            }
        }

        Like likes;
        for (WallPost post : posts) {
            likes = post.likes;
            if (notAllUsersGot(likes)) {
                getAll(post.id, step, likes);
            }
        }

    }

    public Like get(String itemId) {
        Integer step = "1".equals(params.get("friends_only")) ? 100:1000;
        params.put("count", step.toString());
        params.put("offset", "0");

        InputStream stream = client.send(get + "?" + buildRequest(itemId));
        Like like = get(itemId, step, stream);

        return like;
    }

    private Like get(String itemId, Integer step, InputStream stream) {
        Like like = getChunk(stream);
        if (notAllUsersGot(like)) {
            like = getAll(itemId, step, like);
        }
        return like;
    }

    private boolean notAllUsersGot(Like like) {
        return like != null && like.count != null && like.count > like.users.length;
    }

    private Like getAll(String itemId, Integer step, Like like) {
        InputStream stream;
        List<Integer> usersLike = new ArrayList<>();
        usersLike.addAll(Arrays.asList(like.users));
        for (Integer offset = step; offset < like.count; offset += step) {
            params.put("offset", offset.toString());
            stream = client.send(get + "?" + buildRequest(itemId));
            like = getChunk(stream);
            usersLike.addAll(Arrays.asList(like.users));
        }
        like.users = usersLike.toArray(new Integer[0]);
        return like;
    }

    private Like getChunk(InputStream stream) {
        Like like = null;
        if( stream == null){
            logger.fatal("stream was null");
            return null;
        }
        
        try {
            JsonObject result = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
            if (result != null) {
                like = gson.fromJson(result.getAsJsonObject("response"), Like.class);
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.error("error", e);
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
