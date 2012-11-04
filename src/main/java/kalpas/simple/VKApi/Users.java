package kalpas.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.simple.DO.User;
import kalpas.simple.VKApi.client.VKClient;
import kalpas.simple.VKApi.client.VKClient.VKAsyncResult;

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

    private static final String    requstName   = "users.get";

    private final List<String>     allowedCases = Arrays.asList("nom", "gen", "dat", "acc", "ins", "abl");

    static final MapJoiner         joiner       = Joiner.on("&").withKeyValueSeparator("=");

    protected Logger               logger       = Logger.getLogger(Users.class);

    protected VKClient             client;

    // FIXME make both injectable
    private static final Gson      gson         = new Gson();
    private JsonParser             parser       = new JsonParser();

    protected Map<String, String>  params       = new HashMap<>();

    private Function<User, String> getUid       = new Function<User, String>() {
                                                    @Override
                                                    public String apply(User input) {
                                                        return input.uid;
                                                    }
                                                };

    @Inject
    public Users(VKClient vkClient) {
        this.client = vkClient;
    }

    public User get(String uid) {
        User user = new User();
        user.uid = uid;
        return get(user);
    }

    public User get(User user) {
        InputStream stream = client.send(requstName + "?" + buildRequest(user.uid));
        user = getUsers(stream).get(0);
        return user;
    }

    public List<User> get(List<User> users) {
        Map<User, VKAsyncResult> futures = new HashMap<User, VKClient.VKAsyncResult>();
        for (User user : users) {
            futures.put(user, client.sendAsync(requstName + "?" + buildRequest(user.uid)));
        }
        logger.debug("finished with sending requsests");

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
        InputStream stream = client.send(requstName + "?" + buildRequest(uids));

        return getUsers(stream);
    }

    public List<User> batchGet(String... uids) {
        if (uids.length > 999) {
            throw new IllegalArgumentException("amount shouldn't exceed 1000 per call");
        }

        InputStream stream = client.send(requstName + "?" + buildRequest(Joiner.on(",").skipNulls().join(uids)));
        return getUsers(stream);
    }

    // ********************** PRIVATE **********************

    private List<User> getUsers(InputStream stream) {
        List<User> users = new ArrayList<>();
        JsonArray array = parser.parse(new InputStreamReader(stream)).getAsJsonObject().getAsJsonArray("response");
        logger.debug(array);
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
            logger.debug(element);
            return gson.fromJson(element, User.class);
        } catch (JsonSyntaxException e) {
            logger.error("exception while parsing json", e);
            return null;
        }
    }


    // ********************** REQUEST BUILDING **********************

    protected String buildRequest(String uid) {
        params.put("uids", uid);
        return joiner.join(params);

    }

    public Users addFields(String... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException("should pass at least one field name");
        }

        params.put("fields", Joiner.on(",").skipNulls().join(fields));
        return this;
    }

    /**
     * 
     * @param nameCase
     *            could be one of "nom", "gen", "dat", "acc", "ins", "abl"
     * @return
     */
    public Users addNameCase(String nameCase) {
        if (!allowedCases.contains(nameCase)) {
            throw new IllegalArgumentException(nameCase + " nameCase is illegal");
        }

        params.put("name_case", nameCase);
        return this;
    }

}
