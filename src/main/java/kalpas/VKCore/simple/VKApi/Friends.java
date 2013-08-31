package kalpas.VKCore.simple.VKApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.VKError;
import kalpas.VKCore.simple.VKApi.client.Result;
import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKClient.VKAsyncResult;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Friends {

    private static final String get    = "friends.get";

    @Inject
    private MapJoiner           joiner;

    private Logger              logger = LogManager.getLogger(Friends.class);

    @Inject
    private Gson                gson;
    @Inject
    private JsonParser          parser;

    private VKClient            client;

    private List<VKError>       errors = new ArrayList<>();

    @Inject
    public Friends(VKClient vkClient) {
        this.client = vkClient;
    }

    private String buildRequest(String uid) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        return get + "?" + joiner.join(params);

    }

    public Map<User, List<User>> get(List<User> friends) {
        errors.clear();

        Map<User, List<User>> friendsMap = new HashMap<User, List<User>>();
        Map<User, VKAsyncResult> futures = new HashMap<User, VKClient.VKAsyncResult>();

        for (User friend : friends) {
            futures.put(friend, client.sendAsync(buildRequest(friend.uid)));
        }

        process(friendsMap, futures);
        return friendsMap;
    }

    public List<User> get(String uid) {
        errors.clear();
        Result result = client.send(buildRequest(uid));
        return processResponse(uid, result);
    }

    private List<User> processResponse(String uid, Result result) {
        List<User> friendsList = new ArrayList<>();
        if (result.errCode == null) {
            FriendsResponse response = null;
            FriendsResponseWithFields responseWithFields = null;
            try {
                String json = IOUtils.toString(result.stream, "UTF-8");
                JsonObject asJsonObject = parser.parse(json).getAsJsonObject().getAsJsonObject("response");
                try {
                    response = gson.fromJson(asJsonObject, FriendsResponse.class);
                    for (String id : response.items) {
                        friendsList.add(new User(id));
                    }
                } catch (JsonSyntaxException e) {
                    try {
                        responseWithFields = gson.fromJson(asJsonObject, FriendsResponseWithFields.class);
                        friendsList.addAll(Arrays.asList(responseWithFields.items));
                    } catch (JsonSyntaxException e1) {
                        logException(e);
                    }
                }
            } catch (JsonSyntaxException e) {
                logException(e);
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            logCommunicationError(uid, result);
        }
        return friendsList;
    }

    public List<User> get(User user) {
        errors.clear();
        return get(user.uid);
    }

    private void logCommunicationError(String uid, Result result) {
        VKError error = new VKError(result.errMsg);
        error.uid = uid;
        errors.add(error);
        logger.error("HTTP error for {} query", uid);
    }

    private void logException(Exception e) {
        String message = "exception while parsing json";
        logger.error(message, e);
        VKError error = new VKError(message);
        errors.add(error);
    }

    private void process(Map<User, List<User>> friendsMap, Map<User, VKAsyncResult> results) {
        Iterator<Map.Entry<User, VKClient.VKAsyncResult>> iterator;
        Map.Entry<User, VKClient.VKAsyncResult> entry;
        do {
            iterator = results.entrySet().iterator();
            entry = null;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getValue().isDone()) {
                    iterator.remove();
                    friendsMap.put(entry.getKey(), processResponse(entry.getKey().uid, entry.getValue().get()));
                }
            }
        } while (!results.isEmpty());
    }


    public List<VKError> getErrors() {
        return errors;
    }

    private class FriendsResponse {
        @SuppressWarnings("unused")
        public int      count;
        public String[] items;
    }

    private class FriendsResponseWithFields {
        @SuppressWarnings("unused")
        public int    count;
        public User[] items;

    }
}
