package kalpas.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kalpas.VK.VKFriend;
import kalpas.simple.VKClient.VKAsyncResult;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

public class Friends {

    private final Logger   logger = Logger.getLogger(Friends.class);

    private final VKClient client;

    @Inject
    public Friends(VKClient vkClient) {
        this.client = vkClient;
    }

    public List<VKFriend> get(String uid) {

        List<VKFriend> friends = new ArrayList<VKFriend>();

        JSONObject result = client.send("friends.get?uid=" + uid);

        VKFriend vkFriend = null;
        JSONArray friendsArray = result.optJSONArray("response");
        for (int i = 0; i < friendsArray.length(); i++) {
            vkFriend = new VKFriend().setUid(friendsArray.optString(i));
            friends.add(vkFriend);
        }

        return friends;
    }

    public Map<String, List<VKFriend>> get(List<VKFriend> friends) {

        Map<String, List<VKFriend>> friendsMap = new HashMap<String, List<VKFriend>>();
        Map<String, VKAsyncResult> results = new HashMap<String, VKClient.VKAsyncResult>(
                friends.size());
        for (VKFriend friend : friends) {
            results.put(friend.getUid(),
                    client.sendAsync("friends.get?uid=" + friend.getUid()));
        }

        logger.debug("finished with sending requsests");

        List<VKFriend> friendsList;
        VKFriend vkFriend = null;
        JSONArray friendsArray = null;
        do {
            Iterator<Map.Entry<String, VKClient.VKAsyncResult>> iterator = results
                    .entrySet().iterator();
            Map.Entry<String, VKClient.VKAsyncResult> entry = null;
            while (iterator.hasNext()) {
                friendsList = new ArrayList<VKFriend>();
                entry = iterator.next();
                if (entry.getValue().isDone()) {
                    friendsArray = entry.getValue().get()
                            .optJSONArray("response");
                    iterator.remove();
                    if (friendsArray != null) {
                        logger.debug("got result " + entry.getKey());
                        for (int i = 0; i < friendsArray.length(); i++) {
                            vkFriend = new VKFriend().setUid(friendsArray
                                    .optString(i));
                            friendsList.add(vkFriend);
                        }
                        friendsMap.put(entry.getKey(), friendsList);
                    }
                }
            }
        } while (!results.isEmpty());
        return friendsMap;
    }
}
