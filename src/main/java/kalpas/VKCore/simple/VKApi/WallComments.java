package kalpas.VKCore.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kalpas.VKCore.simple.DO.Comment;
import kalpas.VKCore.simple.DO.WallPost;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class WallComments {

    private static final Integer MAX_COMMENTS = 100;

    @Inject
    private Gson                 gson;
    @Inject
    private JsonParser           parser;

    private Logger               logger       = LogManager.getLogger(WallComments.class);
    private VKClient             client;

    private static final String  get          = "wall.getComments";

    @Inject
    private MapJoiner            joiner;

    private Map<String, String>  params       = new HashMap<>();

    @Inject
    public WallComments(VKClient client) {
        this.client = client;
        params.put("need_likes", "1");
        params.put("sort", "asc");
    }

    public List<WallPost> get(List<WallPost> posts) {
        params.put("count", MAX_COMMENTS.toString());
        params.put("offset", "0");

        Map<WallPost, VKAsyncResult> futures = new HashMap<>();
        for (WallPost post : posts) {
            params.put("owner_id", post.to_id);
            futures.put(post, client.sendAsync(get + "?" + buildRequest(post.id)));
        }

        Iterator<Map.Entry<WallPost, VKAsyncResult>> iterator;
        Map.Entry<WallPost, VKAsyncResult> entry;
        while (!futures.isEmpty()) {
            iterator = futures.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (!entry.getValue().isDone()) {
                    continue;
                }
                iterator.remove();
                entry.getKey().comments.comments = new ArrayList<Comment>();
                getChunk(entry.getValue().get(), entry.getKey().comments.comments);
            }
        }

        for (WallPost post : posts) {
            if (notAllCommentsGot(post.comments.comments, post.comments.count)) {
                getRest(post.id, post.comments.comments, post.comments.count);
            }
        }

        return posts;
    }

    public List<Comment> get(String ownerId, String postId) {
        params.put("count", MAX_COMMENTS.toString());
        params.put("offset", "0");
        params.put("owner_id", ownerId);

        List<Comment> comments = new ArrayList<>();

        InputStream stream = client.send(get + "?" + buildRequest(postId));
        get(postId, comments, stream);

        return comments;
    }

    private void get(String postId, List<Comment> comments, InputStream stream) {
        Integer commentsCount;
        commentsCount = getChunk(stream, comments);

        if (notAllCommentsGot(comments, commentsCount)) {
            getRest(postId, comments, commentsCount);
        }
    }

    private void getRest(String postId, List<Comment> comments, Integer commentsCount) {
        InputStream stream;
        for (Integer offset = MAX_COMMENTS; offset < commentsCount; offset += MAX_COMMENTS) {
            params.put("offset", offset.toString());
            stream = client.send(get + "?" + buildRequest(postId));
            getChunk(stream, comments);
        }
    }

    private Integer getChunk(InputStream stream, List<Comment> comments) {
        Integer commentsCount = null;
        try {
            JsonObject json = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
            JsonArray response = json.getAsJsonArray("response");
            if (response != null) {
                Iterator<JsonElement> iterator = response.iterator();
                commentsCount = iterator.hasNext() ? iterator.next().getAsInt() : 0;
                while (iterator.hasNext()) {
                    comments.add(gson.fromJson(iterator.next(), Comment.class));
                }
            } else {
                logger.error("error " + json.toString());
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.error("exception while parsing json", e);
        }
        return commentsCount;
    }

    private boolean notAllCommentsGot(List<Comment> comments, Integer commentsCount) {
        return commentsCount != null && !comments.isEmpty() && comments.size() < commentsCount;
    }

    protected String buildRequest(String postId) {
        params.put("post_id", postId);
        params.put("preview_length", "0");
        return joiner.join(params);
    }

    public WallComments addSort(String sort) {
        params.put("sort", sort);
        return this;
    }

    public WallComments addNeedLikes(Integer needLikes) {
        params.put("need_likes", needLikes.toString());
        return this;
    }

}
