package kalpas.simple.api;

import kalpas.VK.VKUser;
import kalpas.simple.VKClient;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.inject.Inject;

public class Users extends UsersBase {

    private static final String requstName = "users.get";

    private final Logger        logger     = Logger.getLogger(Users.class);

    private final VKClient      client;

    @Inject
    public Users(VKClient vkClient) {
        this.client = vkClient;
    }

    public VKUser getUser(String uid) {
        VKUser userInfo = new VKUser();

        JSONObject result = client.send(requstName + "?" + buildRequest(uid));
        JSONObject user = null;
        
        return userInfo;// FIXME not finished
    }
}
