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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class Users {

    private static final String   requstName   = "users.get";

    private final List<String>    allowedCases = Arrays.asList("nom", "gen", "dat", "acc", "ins", "abl");

    static final MapJoiner        joiner       = Joiner.on("&").withKeyValueSeparator("=");

    protected Logger              logger       = Logger.getLogger(Users.class);

    protected VKClient            client;

    protected Map<String, String> params       = new HashMap<>();

    private Function<VKUser, String> getUid       = new Function<VKUser, String>() {
                                                      @Override
                                                      public String apply(VKUser input) {
                                                          return input.getUid();
                                                      }
                                                  };

    @Inject
    public Users(VKClient vkClient) {
        this.client = vkClient;
    }

    public VKUser get(String uid) {
        VKUser user = new VKUser();
        return get(user);
    }

    public VKUser get(VKUser user) {
        JSONObject result = client.send(requstName + "?" + buildRequest(user.getUid()));
        JSONArray response = result.optJSONArray("response");
        if (response != null) {
            user = Converter.convertFromJSON(response.optJSONObject(0), user);
        }
        return user;
    }

    public List<VKUser> get(List<VKUser> users) {
        Map<VKUser, VKAsyncResult> futures = new HashMap<VKUser, VKClient.VKAsyncResult>();
        for (VKUser user : users) {
            futures.put(user, client.sendAsync(requstName + "?" + buildRequest(user.getUid())));
        }
        logger.debug("finished with sending requsests");

        Iterator<Map.Entry<VKUser, VKAsyncResult>> iterator = null;
        Map.Entry<VKUser, VKAsyncResult> entry = null;
        JSONObject json = null;
        do {
            iterator = futures.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (!entry.getValue().isDone()) {
                    continue;
                }

                json = entry.getValue().get();
                iterator.remove();
                JSONArray response = json.optJSONArray("response");
                if (response != null) {
                    Converter.convertFromJSON(response.optJSONObject(0), entry.getKey());
                }
            }

        } while (!futures.isEmpty());
        return users;
    }

    public List<VKUser> batchGet(List<VKUser> users) {
        if (users.size() > 999) {
            throw new IllegalArgumentException("amount shouldn't exceed 1000 per call");
        }

        Map<String,VKUser> map = new HashMap<>();
        for(VKUser entry: users){
            map.put(entry.getUid(), entry);
        }
        String uids = Joiner.on(",").skipNulls().join(Iterables.transform(users, getUid));
        JSONObject result = client.send(requstName + "?" + buildRequest(uids));
        JSONArray response = result.optJSONArray("response");
        VKUser userInfo = null;
        JSONObject user = null;
        String uid = null;
        if (response != null) {
            for(int i = 0; i < response.length(); i++){
                user = response.optJSONObject(i);
                if (user != null) {
                    uid = user.optString("uid");
                    userInfo = map.get(uid);
                    Converter.convertFromJSON(user, userInfo);
                }
            }
        }
        return users;
    }

    public List<VKUser> batchGet(String... uids) {
        if (uids.length > 999) {
            throw new IllegalArgumentException("amount shouldn't exceed 1000 per call");
        }

        List<VKUser> users = new ArrayList<>();
        
        JSONObject result = client.send(requstName + "?" + buildRequest(Joiner.on(",").skipNulls().join(uids)));
        JSONArray response = result.optJSONArray("response");
        JSONObject user = null;
        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                user = response.optJSONObject(i);
                if (user != null) {
                    users.add(Converter.convertFromJSON(user));
                }
            }
        }
        return users;
    }

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
