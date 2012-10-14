package kalpas.VK.requests;

import java.util.HashMap;
import java.util.Map;

import kalpas.VK.requests.base.VKRequestFactory;

import com.google.common.base.Joiner;

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

    /**
     * 
     * @param fields
     *            {@link FriendsGet#addField(String...)}
     * @return
     */
    public FriendsGet createRequestWithFields(String... fields) {
        FriendsGet request = null;
        String key = Joiner.on("").join(fields);
        if (cache.containsKey(key)) {
            request = cache.get(key);
        } else {
            request = secret == null ? new FriendsGet(this.accessToken)
                    : new FriendsGet(this.accessToken, false, secret);
            request.addField(fields);
            cache.put(key, request);
        }
        return request;
    }

}
