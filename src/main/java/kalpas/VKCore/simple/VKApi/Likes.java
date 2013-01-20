package kalpas.VKCore.simple.VKApi;

import static kalpas.VKCore.util.DebugUtils.traceResponse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kalpas.VKCore.simple.DO.Like;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;
import kalpas.VKCore.util.DebugUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Likes {

    private Logger               logger    = LogManager.getLogger(Likes.class);

    private static final String  get       = "likes.getList";
    private static final Integer max_count = 1000;

    private VKClient             client;

    @Inject
    private Gson                 gson;
    @Inject
    private JsonParser           parser;

    @Deprecated
    private Map<String, String>  params    = new HashMap<>();

    @Inject
    private MapJoiner            joiner;

    @Inject
    public Likes(VKClient client) {
        this.client = client;
    }

    // *******************************************************************************
    public enum LikeObject {
        post, comment, photo, audio, video, note, sitepage;
    }

    public void getLikes(List<WallPost> list) {
        getLikes(list, false);
    }

    public void getLikes(List<WallPost> list, boolean repostOnly) {

        Map<WallPost, VKAsyncResult> futures = new HashMap<>();
        for (WallPost post : list) {
            futures.put(post, client.sendAsync(buildRequest(LikeObject.post, post.to_id, post.id, 0, repostOnly)));
        }

        Like origin, result;
        Map.Entry<WallPost, VKAsyncResult> entry;
        Iterator<Map.Entry<WallPost, VKAsyncResult>> iterator;
        Set<Map.Entry<WallPost, VKAsyncResult>> entrySet = futures.entrySet();
        while (!entrySet.isEmpty()) {
            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (!entry.getValue().isDone()) {
                    continue;
                }

                iterator.remove();
                origin = entry.getKey().likes;
                result = parseLikes(entry.getValue().get());
                origin.count = result.count;
                origin.users = result.users;
            }
        }

        for (WallPost post : list) {
            if (post.likes.count > max_count) {
                getRemaining(LikeObject.post, post.to_id, post.id, repostOnly, post.likes);
            }
        }
    }

    public Like getLikes(LikeObject type, String ownerId, String itemId) {
        return getLikes(type, ownerId, itemId, false);
    }

    public Like getLikes(LikeObject type, String ownerId, String itemId, boolean repostOnly) {
        InputStream stream = client.send(buildRequest(type, ownerId, itemId, 0, repostOnly));
        Like like = parseLikes(stream);

        if (like.count > max_count) {
            getRemaining(type, ownerId, itemId, repostOnly, like);
        }

        return like;
    }

    private void getRemaining(LikeObject type, String ownerId, String itemId, boolean repostOnly, Like like)
 {
        List<VKAsyncResult> futures = submitRequests4remaining(type, ownerId, itemId, repostOnly, like);
        like.users = Arrays.copyOf(like.users, like.count);
        processResponses(like, futures);
    }

    private void processResponses(Like like, List<VKAsyncResult> futures) {
        int offset = max_count;
        Like likeChunk = null;
        Iterator<VKAsyncResult> iterator;
        VKAsyncResult item;

        while (!futures.isEmpty()) {
            iterator = futures.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                if (!item.isDone()) {
                    continue;
                }

                iterator.remove();
                likeChunk = parseLikes(item.get());
                System.arraycopy(likeChunk.users, 0, like.users, offset, likeChunk.users.length);
                offset += likeChunk.users.length;
            }
        }
    }

    private List<VKAsyncResult> submitRequests4remaining(LikeObject type, String ownerId, String itemId,
            boolean repostOnly, Like like) {
        List<VKAsyncResult> futures = new ArrayList<>();
        for (int offset = max_count; offset < like.count; offset += max_count) {
            futures.add(client.sendAsync(DebugUtils
                    .traceRequest(buildRequest(type, ownerId, itemId, offset, repostOnly))));
        }
        return futures;
    }

    private Like parseLikes(InputStream stream) {
        Response response = null;
        try {
            response = gson.fromJson(new InputStreamReader(stream), Response.class);
            if (response.response == null) {
                logger.error("response was null");
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.error("parsing failed", e);
        }
        return response.response;
    }

    // *******************************************************************************

    @Deprecated
    public Like get(String ownerId, String itemId) {
        params.put("owner_id", ownerId);
        return get(itemId);
    }

    @Deprecated
    public List<WallPost> get(List<WallPost> posts) {
        Integer step = "1".equals(params.get("friends_only")) ? 100 : 1000;
        params.put("count", step.toString());
        params.put("offset", "0");

        Map<WallPost, VKAsyncResult> futures = new HashMap<>();

        for (WallPost post : posts) {
            params.put("owner_id", post.to_id);
            futures.put(post, client.sendAsync(buildRequest(post.id)));
        }

        Like like;
        VKAsyncResult result;
        Map.Entry<WallPost, VKAsyncResult> entry;
        Iterator<Map.Entry<WallPost, VKAsyncResult>> iteartor;

        while (!futures.isEmpty()) {
            iteartor = futures.entrySet().iterator();
            while (iteartor.hasNext()) {
                entry = iteartor.next();
                result = entry.getValue();
                if (!result.isDone()) {
                    continue;
                }
                iteartor.remove();
                like = getChunk(result.get());
                Like target = entry.getKey().likes;
                if (like != null) {
                    target.users = like.users;
                    target.count = like.count;
                } else {
                    entry.getKey().likes = like;
                }
            }
        }

        Like likes;
        for (WallPost post : posts) {
            likes = post.likes;
            if (notAllUsersGot(likes)) {
                getAll(post.id, step, likes);
            }
        }
        return posts;
    }

    @Deprecated
    public Like get(String itemId) {
        Integer step = "1".equals(params.get("friends_only")) ? 100 : 1000;
        params.put("count", step.toString());
        params.put("offset", "0");

        InputStream stream = client.send(buildRequest(itemId));
        Like like = get(itemId, step, stream);

        return like;
    }

    @Deprecated
    private Like get(String itemId, Integer step, InputStream stream) {
        Like like = getChunk(stream);
        if (notAllUsersGot(like)) {
            like = getAll(itemId, step, like);
        }
        return like;
    }

    @Deprecated
    private boolean notAllUsersGot(Like like) {
        return like != null && like.count != null && like.count > like.users.length;
    }

    @Deprecated
    private Like getAll(String itemId, Integer step, Like like) {
        InputStream stream;
        List<String> usersLike = new ArrayList<>();
        usersLike.addAll(Arrays.asList(like.users));
        for (Integer offset = step; offset < like.count; offset += step) {
            params.put("offset", offset.toString());
            stream = client.send(buildRequest(itemId));
            like = getChunk(stream);
            usersLike.addAll(Arrays.asList(like.users));
        }
        like.users = usersLike.toArray(new String[0]);
        return like;
    }

    @Deprecated
    private Like getChunk(InputStream stream) {
        Like like = null;
        if (stream == null) {
            logger.fatal("stream was null");
            return null;
        }

        try {
            // TODO remove debugging
            JsonObject result = parser.parse(traceResponse(stream)).getAsJsonObject();
            like = gson.fromJson(result.getAsJsonObject("response"), Like.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.error("error", e);
        }
        return like;
    }

    @Deprecated
    protected String buildRequest(String itemId) {
        params.put("item_id", itemId);
        return get + "?" + joiner.join(params);
    }

    private String buildRequest(LikeObject type, String ownerId, String itemId, Integer offset, boolean repostOnly) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type.toString());
        params.put("owner_id", ownerId);
        params.put("item_id", itemId);
        params.put("count", max_count.toString());
        if (offset > 0) {
            params.put("offset", offset.toString());
        }
        if (repostOnly) {
            params.put("filter", "copies");
        }
        return get + "?" + joiner.join(params);
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

    // /////////////////////

    private class Response {
        public Like response;
    }

}
