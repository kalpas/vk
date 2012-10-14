package kalpas.VK.requests;

import java.util.HashMap;
import java.util.Map;

import kalpas.VK.requests.base.VKRequestFactory;

public class FriendsGetFactory implements VKRequestFactory {

    private String accessToken = null;
    private String secret      = null;
    
    private Map<String, FriendsGet> cache       = new HashMap<String, FriendsGet>();

    @SuppressWarnings("unused")
    private FriendsGetFactory() {
    }

    public FriendsGetFactory(String accessToken) {
        this.accessToken = accessToken;
    }

    public FriendsGetFactory(String accessToken, String secret) {
        this.accessToken = accessToken;
        this.secret = secret;

    }

    public FriendsGet createRequest() {
        return secret == null ? new FriendsGet(this.accessToken)
                : new FriendsGet(this.accessToken, false, secret);
    }
}
