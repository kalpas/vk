package kalpas.VKCore.simple.VKApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.VKApi.client.VKClient;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Groups {

    @Inject
    private Gson                gson;
    @Inject
    private JsonParser          parser;

    private Logger               logger    = Logger.getLogger(Groups.class);

    private VKClient            client;

    private static final String get    = "groups.getMembers";

    @Inject
    private MapJoiner           joiner;
    
    private static final Integer max_count = 1000;

    @Inject
    public Groups(VKClient client) {
        this.client = client;
    }
    
    public List<User> getMembers(String gid){
        List<User> members = new ArrayList<>();

        InputStream stream = client.send(get + "?" + buildRequest(gid));
        try{
            Response response = gson.fromJson(new InputStreamReader(stream), Response.class);
            for (String uid : response.response.users) {
                members.add(new User(uid));
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.error("parsing failed", e);
        } catch (NullPointerException e) {
            logger.error("NPE", e);
        }
        return members;
    }

    private String buildRequest(String gid) {
        Map<String, String> params = new HashMap<>();
        params.put("gid", gid);
        params.put("count", max_count.toString());
        return joiner.join(params);

    }
    
    private class Response {
        public GetMembersResponse response;
    }

    private class GetMembersResponse {
        public int      count;
        public String[] users;
    }
    

}
