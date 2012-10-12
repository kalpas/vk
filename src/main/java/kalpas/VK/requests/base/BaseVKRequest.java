package kalpas.VK.requests.base;

import java.io.IOException;

import kalpas.VK.HttpClientContainer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;

public abstract class BaseVKRequest implements VKRequest {

    Logger logger = Logger.getLogger(this.getClass());

    private final String api = "https://api.vk.com/method/";

    protected String getAccessToken() {
        throw new UnsupportedOperationException();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected HttpClient getHttpClient() {
        return HttpClientContainer.getInstance();
    }

    public void send() {
        String request = api + getName() + "?" + getBody() + "&access_token="
                + getAccessToken();
        HttpPost post = new HttpPost(request);
        HttpResponse response = null;
        String result = null;
        try {
            getLogger().debug(request + " would be executed");
            response = getHttpClient().execute(post);
            HttpEntity entity = response.getEntity();
            result = IOUtils.toString(entity.getContent(), "UTF-8");
        } catch (IOException e) {
            getLogger().error("exception during POST", e);
        }
        getLogger().debug(result);
    }

}
