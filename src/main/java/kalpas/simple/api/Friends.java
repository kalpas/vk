package kalpas.simple.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VK.VKUser;
import kalpas.simple.VKClient;
import kalpas.simple.VKClient.VKAsyncResult;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.inject.Inject;

public class Friends {

    private static final String   get             = "friends.get";

    private final List<String>    allowedOrdering = Arrays.asList("name", "hints");
    private final List<String>    allowedCases    = Arrays.asList("nom", "gen", "dat", "acc", "ins", "abl");

    static final MapJoiner        joiner          = Joiner.on("&").withKeyValueSeparator("=");

    private Logger              logger          = Logger.getLogger(Friends.class);

    private VKClient            client;

    private Map<String, String> params          = new HashMap<>();

    @Inject
    public Friends(VKClient vkClient) {
        this.client = vkClient;
    }

    public List<VKUser> get(String uid) {

        List<VKUser> friends = new ArrayList<VKUser>();

        JSONObject result = client.send(get + "?" + buildRequest(uid));

        VKUser vkFriend = null;
        JSONObject friend = null;
        JSONArray friendsArray = result.optJSONArray("response");
        for (int i = 0; i < friendsArray.length(); i++) {
            friend = friendsArray.optJSONObject(i);
            if (friend != null) {
                vkFriend = Converter.convertFromJSON(friend);
            } else {
                vkFriend = new VKUser().setUid(friendsArray.optString(i));
            }
            friends.add(vkFriend);
        }

        return friends;
    }

    public Map<VKUser, List<VKUser>> get(List<VKUser> friends) {

        Map<VKUser, List<VKUser>> friendsMap = new HashMap<VKUser, List<VKUser>>();
        Map<VKUser, VKAsyncResult> futures = new HashMap<VKUser, VKClient.VKAsyncResult>();
        for (VKUser friend : friends) {
            futures.put(friend, client.sendAsync(get + "?" + buildRequest(friend.getUid())));
        }
        logger.debug("finished with sending requsests");

        process(friendsMap, futures);
        return friendsMap;
    }

    private void process(Map<VKUser, List<VKUser>> friendsMap, Map<VKUser, VKAsyncResult> results) {

        List<VKUser> friendsList;
        JSONArray friendsArray;
        Iterator<Map.Entry<VKUser, VKClient.VKAsyncResult>> iterator;
        Map.Entry<VKUser, VKClient.VKAsyncResult> entry;
        do {
            iterator = results.entrySet().iterator();
            entry = null;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getValue().isDone()) {
                    iterator.remove();
                    friendsArray = entry.getValue().get().optJSONArray("response");
                    if (friendsArray != null) {
                        logger.debug("got result " + entry.getKey());
                        friendsList = processArray(friendsArray);
                        friendsMap.put(entry.getKey(), friendsList);
                    }
                }
            }
        } while (!results.isEmpty());
    }

    private List<VKUser> processArray(JSONArray friendsArray) {
        List<VKUser> friendsList;
        VKUser vkFriend;
        JSONObject friend;
        friendsList = new ArrayList<VKUser>();
        for (int i = 0; i < friendsArray.length(); i++) {
            friend = friendsArray.optJSONObject(i);
            if (friend != null) {
                vkFriend = Converter.convertFromJSON(friend);
            } else {
                vkFriend = new VKUser().setUid(friendsArray.optString(i));
            }
            friendsList.add(vkFriend);
        }
        return friendsList;
    }

    private String buildRequest(String uid) {
        params.put("uid", uid);
        return joiner.join(params);

    }

    // *************** SETTERS *************** //

    public Friends addCount(Integer count) {
        params.put("count", count.toString());
        return this;
    }

    public Friends addOffset(Integer offset) {
        params.put("offset", offset.toString());
        return this;
    }

    public Friends addLid(String lid) {
        params.put("lid", lid);
        return this;
    }

    /**
     * 
     * @param order
     *            could be "name", "hints"
     * @return
     */
    public Friends addOrder(String order) {
        if (allowedOrdering.contains(order)) {
            throw new IllegalArgumentException(order + "order is illegal");
        }

        params.put("order", order);
        return this;
    }

    public Friends addFields(String... fields) {
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
    public Friends addNameCase(String nameCase) {
        if (!allowedCases.contains(nameCase)) {
            throw new IllegalArgumentException(nameCase + " nameCase is illegal");
        }

        params.put("name_case", nameCase);
        return this;
    }
}
