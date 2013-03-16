package kalpas.VKCore.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

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

    // FIXME seems like not retrieves 1st post
    // FIXME seems like reposts are not shown correctly

    private static final Integer max_count = 100;

    @Inject
    private Gson                 gson;
    @Inject
    private JsonParser           parser;

    private Logger               logger    = LogManager.getLogger(Wall.class);
    private VKClient             client;

    private static final String  get       = "wall.get";

    @Inject
    private MapJoiner            joiner;

    @Inject
    public Wall(VKClient client) {
        this.client = client;
    }

    public List<WallPost> getPosts(String ownerId) {
        return getPosts(ownerId, false);
    }

    public List<WallPost> getPosts(String ownerId, int count) {
        return getPosts(ownerId, false, count);
    }

    public List<WallPost> getPosts(String ownerId, boolean isGroup, int count) {
        List<WallPost> list = new ArrayList<>();
        InputStream stream;
        Entry<Integer, List<WallPost>> result;

        if (count <= max_count) {
            stream = client.send(buildRequest(ownerId, isGroup, 0, count));
            result = parseWallPosts(stream);
            list.addAll(result.getValue());

        } else {
            stream = client.send(buildRequest(ownerId, isGroup));
            result = parseWallPosts(stream);
            list.addAll(result.getValue());
            list.addAll(getRemainigPosts(ownerId, isGroup, count));
        }
        return list;
    }

    public List<WallPost> getPosts(String ownerId, boolean isGroup) {
        List<WallPost> list = new ArrayList<>();

        InputStream stream = client.send(buildRequest(ownerId, isGroup));
        Entry<Integer, List<WallPost>> result = parseWallPosts(stream);
        list.addAll(result.getValue());
        Integer totalCount = result.getKey();

        if (totalCount > max_count) {
            list.addAll(getRemainigPosts(ownerId, isGroup, totalCount));
        }
        return list;

    }

    private List<WallPost> getRemainigPosts(String ownerId, boolean isGroup, Integer totalCount) {
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

    private Map.Entry<Integer, List<WallPost>> parseWallPosts(InputStream result) {
        List<WallPost> list = new ArrayList<>();
        int wallPostsCount = 0;
        try {
            JsonObject response = parser.parse(new InputStreamReader(result, "UTF-8")).getAsJsonObject();
            JsonArray posts = response.getAsJsonArray("response");
            if (posts != null) {
                Iterator<JsonElement> iterator = posts.iterator();
                wallPostsCount = iterator.hasNext() ? iterator.next().getAsInt() : 0;
                while (iterator.hasNext()) {
                    try {
                        list.add(gson.fromJson(iterator.next(), WallPost.class));
                    } catch (JsonSyntaxException | NullPointerException e) {
                        logger.error("exception while parsing wallpost object", e);
                    }
                }
            } else {
                logger.error("error " + response.toString());
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.error("exception while parsing json", e);
        } catch (IllegalStateException e) {
            logger.error("exception while parsing json", e);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return new AbstractMap.SimpleEntry<Integer, List<WallPost>>(wallPostsCount, list);
    }

    public int getPostsCount(String ownerId) {
        return getPostsCount(ownerId, false);
    }

    public int getPostsCount(String ownerId, boolean isGroup) {
        InputStream stream = client.send(buildRequest(ownerId, isGroup, 0, 1));
        Entry<Integer, List<WallPost>> result = parseWallPosts(stream);
        result.getKey();

        return result.getKey() == null ? 0 : result.getKey();
    }

    public List<WallPost> getPosts4Period(String ownerId, boolean isGroup, int days) {
        List<WallPost> list = new ArrayList<>();
        InputStream stream;
        Entry<Integer, List<WallPost>> result;
        DateTime now = new DateTime();

        // Sleep.sleep();
        // stream = client.send(buildRequest(ownerId, isGroup, 0, max_count));
        // result = parseWallPosts(stream);
        // list.addAll(result.getValue());

        
        int totalCount = max_count;
        DateTime firstPost = DateTime.now();
        for (int offset = 0; offset < totalCount && !now.minusDays(days).isAfter(firstPost); offset += max_count) {
           stream = client.send(buildRequest(ownerId, isGroup, offset));
           result = parseWallPosts(stream);
           list.addAll(result.getValue());
           firstPost = new DateTime(Long.valueOf(list.get(list.size() - 1).date) * 1000L);
            totalCount = result.getKey();
        }
        
        Iterator<WallPost> iterator = list.iterator();
        WallPost post = null;
        while(iterator.hasNext()){
            post = iterator.next();
            if (new DateTime(Long.valueOf(post.date) * 1000L).isBefore(now.minusDays(days))) {
                iterator.remove();
            }
        }

        return list;
    }

    @Deprecated
    public List<WallPost> get(String ownerId) {
        List<WallPost> wallPosts = new ArrayList<>();
        // int wallPostsCount = MAX_GET_COUNT;
        //
        // params.put("count", MAX_GET_COUNT.toString());
        // params.put("offset", "0");
        // wallPostsCount = get(ownerId, wallPosts);
        // if (count != null && count != 0) {
        // wallPostsCount = count;
        // }
        //
        // List<VKAsyncResult> futures = new ArrayList<>();
        // for (Integer step = MAX_GET_COUNT; step < wallPostsCount; step +=
        // MAX_GET_COUNT) {
        // params.put("count", MAX_GET_COUNT.toString());
        // params.put("offset", step.toString());
        // futures.add(client.sendAsync(get + "?" + buildRequest(ownerId)));
        // }
        //
        // VKAsyncResult future;
        // while (!futures.isEmpty()) {
        // Iterator<VKAsyncResult> iterator = futures.iterator();
        // while (iterator.hasNext()) {
        // future = iterator.next();
        // if (!future.isDone()) {
        // continue;
        // }
        // iterator.remove();
        // get(wallPosts, future.get());
        // }
        // }

        return wallPosts;

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

    // owner, others,all FIXME
    @Deprecated
    public Wall addFilter(String filter) {
        // params.put("filter", filter);
        return this;
    }

    @Deprecated
    public Wall extended() {
        // params.put("extended", "1");
        return this;
    }

    @Deprecated
    public Wall addCount(int count) {
        // this.count = count;
        return this;
    }

}
