package net.kalpas.VKCore.simple.VKApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.kalpas.VKCore.simple.DO.City;
import net.kalpas.VKCore.simple.DO.User;
import net.kalpas.VKCore.simple.DO.VKError;
import net.kalpas.VKCore.simple.VKApi.client.Result;
import net.kalpas.VKCore.simple.VKApi.client.VKClient;
import net.kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

@Component
public class Users {

    // FIXME use sets

    private static final String    requstName     = "users.get";

    @Autowired
    private MapJoiner              joiner;

    protected Logger               logger         = LogManager.getLogger(Users.class);

    protected VKClient             client;

    @Autowired
    private Gson                   gson;
    @Autowired
    private JsonParser             parser;
    @Autowired
    private Locations              locations;

    public final static String[]   ALL_FIELDS     = { "id", "first_name", "last_name", "nickname", "screen_name",
            "sex", "bdate", "city", "country", "timezone", "photo", "photo_medium", "photo_big", "has_mobile",
            "contacts", "education", "online", "counters", "lists", "can_post", "can_see_all_posts", "activity",
            "last_seen", "relation", "exports", "wall_comments", "connections", "interests", "movies", "tv", "books",
            "games", "about", "domain"           };

    public final static String[]   DEFAULT_FIELDS = { "id", "first_name", "last_name", "sex" };

    @Autowired
    public Users(VKClient vkClient) {
        this.client = vkClient;
    }

    public User get(String id) throws VKError {
        User user = new User(id);
        return get(user);
    }

    public User get(User user) throws VKError {
        Result result = client.send(buildRequest(user.id));
        List<User> users = getUsers(result);
        return users.get(0);
    }

    // FIXME why not to pass users in chunks?
    public List<User> get(List<User> users) throws VKError {
        Map<User, VKAsyncResult> futures = new HashMap<User, VKClient.VKAsyncResult>();
        for (User user : users) {
            futures.put(user, client.sendAsync(buildRequest(user.id)));
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
                // FIXME smells from somewhere here
                List<User> result = getUsers(entry.getValue().get());
                if (!result.isEmpty()) {
                    user = result.get(0);
                    users.add(user);
                }
            }
        }
        return users;
    }

    // ********************** PRIVATE **********************

    private List<User> getUsers(Result result) throws VKError {
        VKError error;
        if (result.errCode != null) {
            error = new VKError(result.errMsg);
            throw error;
        }

        List<User> users = new ArrayList<>();
        JsonArray array = null;
        String json = null;
        try {
            json = IOUtils.toString(result.stream, "UTF-8");
            array = parser.parse(json).getAsJsonObject().getAsJsonArray("response");
        } catch (IllegalStateException | JsonIOException | JsonSyntaxException | UnsupportedEncodingException e) {
            logger.error("error parsing ", e);
            error = new VKError(e.toString());
            throw error;
        } catch (IOException e) {
            logger.error(e);
        }

        if (array != null) {
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()) {
                User user = getUser(iterator.next());
                users.add(user);
            }
        } else {
            error = VKError.fromJSON(json);
            throw error;
        }
        return users;
    }

    private User getUser(JsonElement element) throws VKError {
        User user = null;
        try {
            user = gson.fromJson(element, User.class);
            City city = locations.getCityById(user.city);
            if (city != null)
                user.city = city.title;
        } catch (JsonSyntaxException e) {
            logger.error("exception while parsing json", e);
        }
        return user;
    }

    // ********************** REQUEST BUILDING **********************

    protected String buildRequest(String id) {
        return buildRequest(id, ALL_FIELDS);// used to be DEFAULT_FIELDS
    }

    protected String buildRequest(String id, String[] fields) {
        Map<String, String> params = new HashMap<>();
        params.put("user_ids", id);
        params.put("fields", Joiner.on(",").skipNulls().join(fields));
        return requstName + "?" + joiner.join(params);
    }

}
