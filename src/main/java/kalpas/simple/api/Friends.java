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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Strings;
import com.google.inject.Inject;

public class Friends extends UsersBase {

    private static final DateTimeFormatter formatterFull    = DateTimeFormat.forPattern("dd.MM.yyyy").withZoneUTC();
    private static final DateTimeFormatter formatterPartial = DateTimeFormat.forPattern("dd.MM").withZoneUTC()
                                                                    .withDefaultYear(1972);

    private static final String            requstName       = "friends.get";

    private final List<String>             allowedOrdering  = Arrays.asList("name", "hints");

    @Inject
    public Friends(VKClient vkClient) {
        this.client = vkClient;
    }

    public List<VKUser> get(String uid) {

        List<VKUser> friends = new ArrayList<VKUser>();

        JSONObject result = client.send(requstName + "?" + buildRequest(uid));

        VKUser vkFriend = null;
        JSONObject friend = null;
        JSONArray friendsArray = result.optJSONArray("response");
        for (int i = 0; i < friendsArray.length(); i++) {
            if (selectedFields.isEmpty()) {
                vkFriend = new VKUser().setUid(friendsArray.optString(i));
            } else {
                friend = friendsArray.optJSONObject(i);
                vkFriend = convertFromJSON(friend);
            }
            friends.add(vkFriend);
        }

        return friends;
    }

    public Map<VKUser, List<VKUser>> get(List<VKUser> friends) {

        Map<VKUser, List<VKUser>> friendsMap = new HashMap<VKUser, List<VKUser>>();
        Map<VKUser, VKAsyncResult> futures = new HashMap<VKUser, VKClient.VKAsyncResult>();
        for (VKUser friend : friends) {
            futures.put(friend, client.sendAsync(requstName + "?" + buildRequest(friend.getUid())));
        }
        logger.debug("finished with sending requsests");

        process(friendsMap, futures);
        return friendsMap;
    }

    private void process(Map<VKUser, List<VKUser>> friendsMap, Map<VKUser, VKAsyncResult> results) {

        List<VKUser> friendsList = null;
        VKUser vkFriend = null;
        JSONArray friendsArray = null;
        JSONObject friend = null;
        Iterator<Map.Entry<VKUser, VKClient.VKAsyncResult>> iterator = null;
        Map.Entry<VKUser, VKClient.VKAsyncResult> entry = null;

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
                        friendsList = new ArrayList<VKUser>();
                        for (int i = 0; i < friendsArray.length(); i++) {
                            if (selectedFields.isEmpty()) {
                                vkFriend = new VKUser().setUid(friendsArray.optString(i));
                            } else {
                                friend = friendsArray.optJSONObject(i);
                                vkFriend = convertFromJSON(friend);
                            }

                            friendsList.add(vkFriend);
                        }
                        friendsMap.put(entry.getKey(), friendsList);
                    }
                }
            }
        } while (!results.isEmpty());
    }

    private VKUser convertFromJSON(JSONObject friend) {
        VKUser vkFriend = new VKUser();
        if (selectedFields.contains("uid")) {
            vkFriend.setUid(friend.optString("uid"));
        }
        if (selectedFields.contains("first_name")) {
            vkFriend.setFirstName(friend.optString("first_name"));
        }
        if (selectedFields.contains("last_name")) {
            vkFriend.setLastName(friend.optString("last_name"));
        }
        if (selectedFields.contains("nickname")) {
            vkFriend.setNickname(friend.optString("nickname"));
        }
        if (selectedFields.contains("sex")) {
            vkFriend.setSex(friend.optInt("sex"));
        }
        if (selectedFields.contains("bdate")) {
            DateTime bdate = null;
            String date = friend.optString("bdate");
            if (!Strings.isNullOrEmpty(date) && !"-99.1".equals(date)) {
                try {
                    bdate = formatterFull.parseDateTime(date);
                } catch (IllegalArgumentException e) {
                    try {
                        bdate = formatterPartial.parseDateTime(date);
                    } catch (IllegalArgumentException ex) {
                        logger.error("date not parsed at all", ex);
                    }
                } catch (UnsupportedOperationException e) {
                    logger.error("smth really bad happened while parsing date", e);
                }
                vkFriend.setBdate(bdate);
            }
        }
        if (selectedFields.contains("city")) {
            vkFriend.setCity(friend.optString("city"));
        }
        if (selectedFields.contains("country")) {
            vkFriend.setCountry(friend.optString("country"));
        }

        // FIXME correct fields
        // if(selectedFields.contains("timezone")){
        // vkFriend.setTimezone(friend.optString("timezone"));
        // }
        // if(selectedFields.contains("photo")){
        // vkFriend.setPhoto(friend.optString("photo"));
        // }
        // if(selectedFields.contains("photo_medium")){
        // vkFriend.setPhotoMedium(friend.optString("photo_medium"));
        // }
        // if(selectedFields.contains("photo_big")){
        // vkFriend.setPhotoBig(friend.optString("photo_big"));
        // }
        // if(selectedFields.contains("domain")){
        // vkFriend.setDomain(friend.optString("domain"));
        // }

        return vkFriend;
    }

    protected UsersBase copy() {
        UsersBase newOne = new Friends(client);
        newOne.params = this.params;
        newOne.selectedFields = this.selectedFields;
        return newOne;
    }

    // *************** SETTERS *************** //

    public UsersBase addCount(Integer count) {
        params.put("count", count.toString());
        return copy();
    }

    public UsersBase addOffset(Integer offset) {
        params.put("offset", offset.toString());
        return copy();
    }

    public UsersBase addLid(String lid) {
        params.put("lid", lid);
        return copy();
    }

    /**
     * 
     * @param order
     *            could be "name", "hints"
     * @return
     */
    public UsersBase addOrder(String order) {
        if (allowedOrdering.contains(order)) {
            throw new IllegalArgumentException(order + "order is illegal");
        }

        params.put("order", order);
        return copy();
    }
}
