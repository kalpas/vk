package kalpas.VKCore.simple.VKApi;

import java.io.IOException;
import java.util.ArrayList;
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
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class Groups {

    @Inject
    private Gson                 gson;
    @Inject
    private JsonParser           parser;

    private Logger               logger    = LogManager.getLogger(Groups.class);

    private VKClient             client;

    private static final String  get       = "groups.getMembers";

    @Inject
    private MapJoiner            joiner;

    private static final Integer max_count = 1000;

    @Inject
    public Groups(VKClient client) {
        this.client = client;
    }

    public List<User> getMembers(String gid) throws VKError {
        List<User> members = new ArrayList<>();

        GetMembersResponse response = parseInputStream(client.send(buildRequest(gid)));
        processResponse(members, response);

        if (response.count > max_count) {
            List<VKAsyncResult> futures = new ArrayList<>();
            for (int offset = max_count; offset < response.count; offset += max_count) {
                futures.add(client.sendAsync(buildRequest(gid, offset)));
            }
            processAsyncResults(members, futures);
        }
        return members;
    }

    private void processAsyncResults(List<User> members, List<VKAsyncResult> futures) throws VKError {
        VKAsyncResult future;
        while (!futures.isEmpty()) {
            Iterator<VKAsyncResult> iterator = futures.iterator();
            while (iterator.hasNext()) {
                future = iterator.next();
                if (!future.isDone()) {
                    continue;
                }
                iterator.remove();
                processResponse(members, parseInputStream(future.get()));
            }
        }
    }

    private GetMembersResponse parseInputStream(Result result) throws VKError {
        if (result.errCode != null) {
            VKError error = new VKError(result.errMsg);
            throw error;
        }
        GetMembersResponse response = null;
        try {
            String json = IOUtils.toString(result.stream, "UTF-8");
            response = gson.fromJson(json, Response.class).response;
            if (response == null) {
                throw VKError.fromJSON(json);
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.error("parsing failed", e);
            throw new VKError(e.toString());
        } catch (IOException e) {
            logger.error(e);
        }
        return response;
    }

    private void processResponse(List<User> members, GetMembersResponse response) {
        for (String uid : response.users) {
            members.add(new User(uid));
        }
    }

    private String buildRequest(String gid) {
        return buildRequest(gid, 0);
    }

    private String buildRequest(String gid, Integer offset) {
        Map<String, String> params = new HashMap<>();
        params.put("group_id", gid);
        params.put("count", max_count.toString());
        params.put("offset", offset.toString());
        return get + "?" + joiner.join(params);
    }

    private class Response {
        public GetMembersResponse response;
    }

    private class GetMembersResponse {
        public int      count;
        public String[] users;
    }

}
