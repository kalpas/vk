package kalpas.VKCore.simple.VKApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Friends {

    private static final String get             = "friends.get";

    private final List<String>  allowedOrdering = Arrays.asList("name", "hints");
    private final List<String>  allowedCases    = Arrays.asList("nom", "gen", "dat", "acc", "ins", "abl");

    @Inject
    private MapJoiner           joiner;

    private Logger              logger          = LogManager.getLogger(Friends.class);

    @Inject
    private Gson                gson;
    @Inject
    private JsonParser          parser;

    private VKClient            client;

    @Inject
    public Friends(VKClient vkClient) {
        this.client = vkClient;
    }

    public List<User> get(String uid) throws VKError {

        List<User> friendsList = Collections.emptyList();
        InputStream stream = client.send(buildRequest(uid));
        try {
            String json = IOUtils.toString(stream, "UTF-8");
            JsonObject response = parser.parse(json).getAsJsonObject();
            JsonArray friends = response.getAsJsonArray("response");
            if (friends == null) {
                VKError error = new VKError(json);
                if (error.getErrorCode() == 15) {
                    return friendsList;
                } else {
                    throw error;
                }
            }
            friendsList = processArray(friends);
        } catch (JsonSyntaxException | JsonIOException | UnsupportedEncodingException e) {
            logger.error("exception while parsing json", e);
        } catch (IOException e) {
            logger.error(e);
        }

        return friendsList;
    }

    public List<User> get(User user) throws VKError {
        return get(user.uid);
    }

    public Map<User, List<User>> get(List<User> friends) {

        Map<User, List<User>> friendsMap = new HashMap<User, List<User>>();
        Map<User, VKAsyncResult> futures = new HashMap<User, VKClient.VKAsyncResult>();
        for (User friend : friends) {
            futures.put(friend, client.sendAsync(buildRequest(friend.uid)));
        }

        process(friendsMap, futures);
        return friendsMap;
    }

    private void process(Map<User, List<User>> friendsMap, Map<User, VKAsyncResult> results) {
        JsonArray friends;
        List<User> friendsList;
        Iterator<Map.Entry<User, VKClient.VKAsyncResult>> iterator;
        Map.Entry<User, VKClient.VKAsyncResult> entry;
        do {
            iterator = results.entrySet().iterator();
            entry = null;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getValue().isDone()) {
                    iterator.remove();
                    try {
                        InputStream inputStream = entry.getValue().get();
                        if(inputStream == null){
                            continue;// FIXME smells. hot place
                        }
                        JsonObject json = parser.parse(new InputStreamReader(inputStream,"UTF-8")).getAsJsonObject();
                        friends = json.getAsJsonArray("response");
                        if (friends != null) {
                            friendsList = processArray(friends);
                            friendsMap.put(entry.getKey(), friendsList);
                        }
                    } catch (JsonParseException | UnsupportedEncodingException e) {
                        logger.error("error parsing json", e);
                    }
                }
            }
        } while (!results.isEmpty());
    }

    private List<User> processArray(JsonArray array) {
        List<User> users = new ArrayList<User>();
        User user;
        JsonElement element;
        Iterator<JsonElement> iterator = array.iterator();// FIXME smells NPE
        while (iterator.hasNext()) {
            user = null;
            element = iterator.next();
            try {
                user = gson.fromJson(element, User.class);
            } catch (JsonSyntaxException e) {
                try{
                    user = new User();
                    user.uid = element.getAsString();
                } catch (JsonSyntaxException ex) {
                    logger.error("error parsing json", ex);
                }
            }
            users.add(user);
        }
        return users;
    }

    private String buildRequest(String uid) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        return get + "?" + joiner.join(params);

    }

    // *************** SETTERS *************** //

    @Deprecated
    public Friends addCount(Integer count) {
        // params.put("count", count.toString());
        return this;
    }

    @Deprecated
    public Friends addOffset(Integer offset) {
        // params.put("offset", offset.toString());
        return this;
    }

    @Deprecated
    public Friends addLid(String lid) {
        // params.put("lid", lid);
        return this;
    }

    /**
     * 
     * @param order
     *            could be "name", "hints"
     * @return
     */
    @Deprecated
    public Friends addOrder(String order) {
        if (allowedOrdering.contains(order)) {
            throw new IllegalArgumentException(order + "order is illegal");
        }

        // params.put("order", order);
        return this;
    }

    @Deprecated
    public Friends addFields(String... fields) {
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
    public Friends addNameCase(String nameCase) {
        if (!allowedCases.contains(nameCase)) {
            throw new IllegalArgumentException(nameCase + " nameCase is illegal");
        }

        // params.put("name_case", nameCase);
        return this;
    }
}
