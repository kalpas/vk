package net.kalpas.VKCore.simple.VKApi;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.kalpas.VKCore.simple.DO.Like;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.DO.WallPost;
import net.kalpas.VKCore.simple.VKApi.client.Result;
import net.kalpas.VKCore.simple.VKApi.client.VKClient;
import net.kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;
import net.kalpas.VKCore.util.DebugUtils;

@Component
public class Likes {

    private Logger               logger    = LogManager.getLogger(Likes.class);

    private static final String  get       = "likes.getList";
    private static final Integer max_count = 1000;

    private VKClient             client;

    @Autowired
    private Gson                 gson;
    @Autowired
    private JsonParser           parser;

    @Deprecated

    @Autowired
    private MapJoiner            joiner;

    @Autowired
    public Likes(VKClient client) {
        this.client = client;
    }

    // *******************************************************************************
    public enum LikeObject {
        post, comment, photo, audio, video, note, sitepage;
    }

    public void getLikes(List<WallPost> list) throws VKError {
        getLikes(list, false);
    }

    public void getLikes(List<WallPost> list, boolean repostOnly) throws VKError {

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
                if (result != null) {
                    origin.count = result.count;
                    origin.items = result.items;
                }
            }
        }

        for (WallPost post : list) {
            if (post.likes.count > max_count) {
                getRemaining(LikeObject.post, post.to_id, post.id, repostOnly, post.likes);
            }
        }
    }



    public Like getLikes(LikeObject type, String ownerId, String itemId) throws VKError {
        return getLikes(type, ownerId, itemId, false);
    }

    public Like getLikes(LikeObject type, String ownerId, String itemId, boolean repostOnly) throws VKError {
        Result result = client.send(buildRequest(type, ownerId, itemId, 0, repostOnly));
        Like like = parseLikes(result);

        if (like.count > max_count) {
            getRemaining(type, ownerId, itemId, repostOnly, like);
        }

        return like;
    }

    private void getRemaining(LikeObject type, String ownerId, String itemId, boolean repostOnly, Like like)
            throws VKError {
        List<VKAsyncResult> futures = submitRequests4remaining(type, ownerId, itemId, repostOnly, like);
        like.items = Arrays.copyOf(like.items, like.count);
        processResponses(like, futures);
    }

    private void processResponses(Like like, List<VKAsyncResult> futures) throws VKError {
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
                System.arraycopy(likeChunk.items, 0, like.items, offset, likeChunk.items.length);
                offset += likeChunk.items.length;
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

    private Like parseLikes(Result result) throws VKError {
        VKError error;
        if (result.errCode != null) {
            error = new VKError(result.errMsg);
            throw error;
        }
        Response response = null;
        try {
            response = gson.fromJson(new InputStreamReader(result.stream, "UTF-8"), Response.class);
            if (response.response == null) {
                logger.error("response was null");
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            String message = "parsing failed";
            error = new VKError(message);
            logger.error(message, e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return response.response;
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

    private class Response {
        public Like response;
    }

}
