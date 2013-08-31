package kalpas.VKCore.simple.VKApi.client;

import java.io.IOException;
import java.util.concurrent.Future;

import kalpas.VKCore.simple.helper.HttpClientContainer;

import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VKHttpsClient extends VKClient {

    private Logger          logger = LogManager.getLogger(VKHttpsClient.class);

    private final String    api    = "api.vk.com";
    private final String    accessToken;

    private HttpClient      client;
    private HttpAsyncClient asyncClient;

    @SuppressWarnings("unused")
    private VKHttpsClient() {
        accessToken = null;
    }

    public VKHttpsClient(String accessToken, HttpClientContainer container) {
        this.accessToken = accessToken;
        client = container.getInstance();
        asyncClient = container.getAsyncInstance();
    }

    @Override
    protected HttpResponse sendInternal(String request) {
        Validate.notNull(accessToken);

        request = buildRequest(request);

        logger.debug("request {}", request);

        HttpResponse response = null;
        HttpGet get = new HttpGet(request);
        sleepIfNeeded();
        try {
            response = client.execute(get);
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (IOException e) {
            logger.error("IO exception", e);
        }

        logger.debug(response);

        return response;
    }

    @Override
    protected Future<HttpResponse> sendAsyncInternal(String request) {
        Validate.notNull(accessToken);

        request = buildRequest(request);

        logger.debug("async request {}", request);

        HttpGet get = new HttpGet(request);
        sleepIfNeeded();
        return asyncClient.execute(get, null);
    }

    private String buildRequest(String request) {
        request = "/method/" + request + "&v=5.0" + "&access_token=" + this.accessToken;
        request = "https://" + api + request;
        return request;
    }

}
