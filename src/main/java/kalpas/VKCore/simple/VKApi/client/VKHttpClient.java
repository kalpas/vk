package kalpas.VKCore.simple.VKApi.client;

import java.io.IOException;
import java.util.concurrent.Future;

import kalpas.VKCore.simple.helper.HttpClientContainer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VKHttpClient extends VKClient {

    private Logger          logger = LogManager.getLogger(VKHttpsClient.class);

    private final String    api    = "api.vk.com";
    private final String    accessToken;
    private final String    secret;

    private HttpClient      client;
    private HttpAsyncClient asyncClient;

    private long            lastRequest = System.currentTimeMillis();
    private long            offset      = 340L;

    @SuppressWarnings("unused")
    private VKHttpClient() {
        accessToken = null;
        secret = null;
    }

    public VKHttpClient(String accessToken, String secret, HttpClientContainer container) {
        this.accessToken = accessToken;
        this.secret = secret;
        client = container.getInstance();
        asyncClient = container.getAsyncInstance();
    }

    @Override
    protected HttpResponse sendInternal(String request) {
        Validate.notNull(accessToken);
        Validate.notNull(secret);

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

        return response;
    }

    @Override
    protected Future<HttpResponse> sendAsyncInternal(String request) {
        Validate.notNull(accessToken);
        Validate.notNull(secret);

        request = buildRequest(request);

        logger.debug("async request {}", request);

        HttpGet get = new HttpGet(request);
        sleepIfNeeded();
        return asyncClient.execute(get, null);
    }

    private String buildRequest(String request) {
        request = "/method/" + request + "&access_token=" + this.accessToken;
        request = request + "&sig=" + DigestUtils.md5Hex(request + secret);

        request = "http://" + api + request;
        return request;
    }

    private void sleepIfNeeded() {
        long now = System.currentTimeMillis();
        long diff = now - lastRequest;
        if (diff < offset) {
            try {
                logger.debug("sleeping for {} s", offset - diff);
                Thread.sleep(offset - diff);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        lastRequest = System.currentTimeMillis();
    }
}
