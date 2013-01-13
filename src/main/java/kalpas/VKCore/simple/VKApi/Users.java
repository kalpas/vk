package kalpas.VKCore.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Users {

    private static final String    requstName     = "users.get";

    private final List<String>     allowedCases   = Arrays.asList("nom", "gen", "dat", "acc", "ins", "abl");

    @Inject
    private MapJoiner              joiner;

    protected Logger               logger         = Logger.getLogger(Users.class);

    protected VKClient             client;

    @Inject
    private Gson                   gson;
    @Inject
    private JsonParser             parser;


    private Function<User, String> getUid         = new Function<User, String>() {
                                                      @Override
                                                      public String apply(User input) {
                                                          return input.uid;
                                                      }
                                                  };

    public final static String[]   ALL_FIELDS     = { "uid", "first_name", "last_name", "nickname", "screen_name",
            "sex", "bdate", "city", "country", "timezone", "photo", "photo_medium", "photo_big", "has_mobile",
            "contacts", "education", "online", "counters", "lists", "can_post", "can_see_all_posts", "activity",
            "last_seen", "relation", "exports", "wall_comments", "connections", "interests", "movies", "tv", "books",
            "games", "about", "domain"           };

    public final static String[]   DEFAULT_FIELDS = { "uid", "first_name", "last_name", "sex" };

    @Inject
    public Users(VKClient vkClient) {
        this.client = vkClient;
    }

    public User get(String uid) {
        User user = new User(uid);
        return get(user);
    }

    public User get(User user) {
        InputStream stream = client.send(buildRequest(user.uid));
        user = getUsers(stream).get(0);
        return user;
    }

    public List<User> get(List<User> users) {
        Map<User, VKAsyncResult> futures = new HashMap<User, VKClient.VKAsyncResult>();
        for (User user : users) {
            futures.put(user, client.sendAsync(buildRequest(user.uid)));
        }

        users = new ArrayList<>();
        Iterator<Map.Entry<User, VKAsyncResult>> iterator = null;
        Map.Entry<User, VKAsyncResult> entry = null;
        User user;
        while (!futures.isEmpty()) {
            iterator = futures.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (!entry.getValue().isDone()) {
                    continue;
                }
                iterator.remove();
                user = getUsers(entry.getValue().get()).get(0);
                users.add(user);
            }
        }
        return users;
    }

    public List<User> batchGet(List<User> users) {
        if (users.size() > 999) {
            throw new IllegalArgumentException("amount shouldn't exceed 1000 per call");
        }

        String uids = Joiner.on(",").skipNulls().join(Iterables.transform(users, getUid));
        InputStream stream = client.send(buildRequest(uids));

        return getUsers(stream);
    }

    public List<User> batchGet(String... uids) {
        if (uids.length > 999) {
            throw new IllegalArgumentException("amount shouldn't exceed 1000 per call");
        }

        InputStream stream = client.send(buildRequest(Joiner.on(",").skipNulls().join(uids)));
        return getUsers(stream);
    }

    // ********************** PRIVATE **********************

    private List<User> getUsers(InputStream stream) {
        List<User> users = new ArrayList<>();
        JsonArray array = parser.parse(new InputStreamReader(stream)).getAsJsonObject().getAsJsonArray("response");
        if (array != null) {
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()) {
                User user = getUser(iterator.next());
                users.add(user);
            }
        }
        return users;
    }

    private User getUser(JsonElement element) {
        try {
            return gson.fromJson(element, User.class);
        } catch (JsonSyntaxException e) {
            logger.error("exception while parsing json", e);
            return null;
        }
    }

    // ********************** REQUEST BUILDING **********************

    protected String buildRequest(String uid) {
        return buildRequest(uid, DEFAULT_FIELDS);
    }

    protected String buildRequest(String uid, String[] fields) {
        Map<String, String> params = new HashMap<>();
        params.put("uids", uid);
        params.put("fields", Joiner.on(",").skipNulls().join(fields));
        return requstName + "?" + joiner.join(params);
    }

    @Deprecated
    public Users addFields(String... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException("should pass at least one field name");
        }

        // params.put("fields", Joiner.on(",").skipNulls().join(fields));
        return this;
    }

    /**
     * 
     * @param nameCase
     *            could be one of "nom", "gen", "dat", "acc", "ins", "abl"
     * @return
     */
    @Deprecated
    public Users addNameCase(String nameCase) {
        if (!allowedCases.contains(nameCase)) {
            throw new IllegalArgumentException(nameCase + " nameCase is illegal");
        }

        // params.put("name_case", nameCase);
        return this;
    }

}
