package net.kalpas.VKCore.simple.VKApi;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.DO.WallPost;
import net.kalpas.VKCore.simple.VKApi.client.Result;
import net.kalpas.VKCore.simple.VKApi.client.VKClient;
import net.kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

@Component
public class Wall {

    // FIXME seems like not retrieves 1st post
    // FIXME seems like reposts are not shown correctly

    private static final Integer max_count = 100;

    @Autowired
    private Gson                 gson;
    @Autowired
    private JsonParser           parser;

    private Logger               logger    = LogManager.getLogger(Wall.class);
    private VKClient             client;

    private static final String  get       = "wall.get";

    @Autowired
    private MapJoiner            joiner;

    @Autowired
    public Wall(VKClient client) {
        this.client = client;
    }

    public List<WallPost> getPosts(String ownerId) throws VKError {
        return getPosts(ownerId, false);
    }

    public List<WallPost> getPosts(String ownerId, int count) throws VKError {
        return getPosts(ownerId, false, count);
    }

    public List<WallPost> getPosts(String ownerId, boolean isGroup, int count) throws VKError {
        List<WallPost> list = new ArrayList<>();
        Result result;
        Entry<Integer, List<WallPost>> posts;

        if (count <= max_count) {
            result = client.send(buildRequest(ownerId, isGroup, 0, count));
            posts = parseWallPosts(result);
            list.addAll(posts.getValue());

        } else {
            result = client.send(buildRequest(ownerId, isGroup));
            posts = parseWallPosts(result);
            list.addAll(posts.getValue());
            list.addAll(getRemainigPosts(ownerId, isGroup, count));
        }
        return list;
    }

    public List<WallPost> getPosts(String ownerId, boolean isGroup) throws VKError {
        List<WallPost> list = new ArrayList<>();

        Result result = client.send(buildRequest(ownerId, isGroup));
        Entry<Integer, List<WallPost>> posts = parseWallPosts(result);
        list.addAll(posts.getValue());
        Integer totalCount = posts.getKey();

        if (totalCount > max_count) {
            list.addAll(getRemainigPosts(ownerId, isGroup, totalCount));
        }
        return list;

    }

    private List<WallPost> getRemainigPosts(String ownerId, boolean isGroup, Integer totalCount) throws VKError {
        List<WallPost> list = new ArrayList<>();
        List<VKAsyncResult> futures = new ArrayList<>();

        for (int offset = max_count; offset < totalCount; offset += max_count) {
            futures.add(client.sendAsync(buildRequest(ownerId, isGroup, offset)));
        }

        VKAsyncResult future;
        Iterator<VKAsyncResult> iterator;
        while (!futures.isEmpty()) {
            iterator = futures.iterator();
            while (iterator.hasNext()) {
                future = iterator.next();
                if (!future.isDone()) {
                    continue;
                }
                iterator.remove();
                list.addAll(parseWallPosts(future.get()).getValue());
            }
        }

        return list;
    }

    private Map.Entry<Integer, List<WallPost>> parseWallPosts(Result result) throws VKError {
        VKError error;
        if (result.errCode != null) {
            error = new VKError(result.errMsg);
            throw error;
        }

        List<WallPost> list = new ArrayList<>();
        int wallPostsCount = 0;
        String json = null;
        JsonObject response = null;
        try {
            json = IOUtils.toString(result.stream, "UTF-8");
            response = parser.parse(json).getAsJsonObject().getAsJsonObject("response");
        } catch (Exception e1) {
            logger.error(e1);
            error = new VKError(e1.toString());
            throw error;
        }
        if (response != null) {
            wallPostsCount = response.getAsJsonPrimitive("count").getAsInt();
            Iterator<JsonElement> iterator = response.getAsJsonArray("items").iterator();
            while (iterator.hasNext()) {
                try {
                    list.add(gson.fromJson(iterator.next(), WallPost.class));
                } catch (JsonSyntaxException | NullPointerException e) {
                    String message = "exception while parsing wallpost object";
                    logger.error(message, e);
                    throw new VKError(message);
                }
            }
        } else {
            error = VKError.fromJSON(json);
        }

        return new AbstractMap.SimpleEntry<Integer, List<WallPost>>(wallPostsCount, list);
    }

    public int getPostsCount(String ownerId) throws VKError {
        return getPostsCount(ownerId, false);
    }

    public int getPostsCount(String ownerId, boolean isGroup) throws VKError {
        Result result = client.send(buildRequest(ownerId, isGroup, 0, 1));
        Entry<Integer, List<WallPost>> posts = parseWallPosts(result);

        return posts.getKey() == null ? 0 : posts.getKey();
    }

    public List<WallPost> getPosts4Period(String ownerId, boolean isGroup, int days) throws VKError {
        List<WallPost> list = new ArrayList<>();
        Entry<Integer, List<WallPost>> posts;
        DateTime now = new DateTime();

        Result result;

        int totalCount = max_count;
        DateTime firstPost = DateTime.now();
        for (int offset = 0; offset < totalCount && !now.minusDays(days).isAfter(firstPost); offset += max_count) {
            result = client.send(buildRequest(ownerId, isGroup, offset));
            posts = parseWallPosts(result);
            list.addAll(posts.getValue());
            firstPost = new DateTime(Long.valueOf(list.get(list.size() - 1).date) * 1000L);
            totalCount = posts.getKey();
        }

        Iterator<WallPost> iterator = list.iterator();
        WallPost post = null;
        while (iterator.hasNext()) {
            post = iterator.next();
            if (new DateTime(Long.valueOf(post.date) * 1000L).isBefore(now.minusDays(days))) {
                iterator.remove();
            }
        }

        return list;
    }

    private String buildRequest(String ownerId, boolean isGroup) {
        return buildRequest(ownerId, isGroup, 0);
    }

    private String buildRequest(String ownerId, boolean isGroup, Integer offset) {
        return buildRequest(ownerId, isGroup, offset, max_count);
    }

    private String buildRequest(String ownerId, boolean isGroup, Integer offset, Integer count) {
        Map<String, String> params = new HashMap<>();
        params.put("owner_id", (isGroup ? "-" : "") + ownerId);
        params.put("count", count.toString());
        params.put("offset", offset.toString());
        return get + "?" + joiner.join(params);
    }
}
