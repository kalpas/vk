package kalpas.VK.requests;

import kalpas.VK.requests.base.VKRequestFactory;

public class FriendsGetFactory implements VKRequestFactory {

    private String accessToken = null;

    private FriendsGetFactory() {
    }

    public FriendsGetFactory(String accessToken) {
        this.accessToken = accessToken;
    }

    public FriendsGet createRequest() {
        return new FriendsGet(this.accessToken);
    }

}
