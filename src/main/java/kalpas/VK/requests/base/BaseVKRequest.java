package kalpas.VK.requests.base;

public abstract class BaseVKRequest implements VKRequest {

    private final String api = "https://api.vk.com/method/";

    protected String buildBaseRequest() {
        return api + getName() + "?";
    }

}
