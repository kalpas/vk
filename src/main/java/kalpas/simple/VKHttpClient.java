package kalpas.simple;

import java.io.IOException;
import java.util.concurrent.Future;

import kalpas.VK.HttpClientContainer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.util.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.log4j.Logger;

public class VKHttpClient extends VKClient {

    private Logger          logger      = Logger.getLogger(VKHttpsClient.class);

    private final String    api    = "api.vk.com";
    private final String    accessToken;
    private final String    secret;

    private HttpClient      client;
    private HttpAsyncClient asyncClient;

    @SuppressWarnings("unused")
    private VKHttpClient() {
        accessToken = null;
        secret = null;
    }

    public VKHttpClient(String accessToken, String secret,
            HttpClientContainer container) {
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

        HttpResponse response = null;
        HttpGet get = new HttpGet(request);
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
        Validate.notNull(secret);

        request = buildRequest(request);

        HttpGet get = new HttpGet(request);
        return asyncClient.execute(get, null);
    }

    private String buildRequest(String request) {
        request = "/method/" + request + "&access_token="
                + this.accessToken;
        request = request + "&sig=" + DigestUtils.md5Hex(request + secret);

        request = "http://" + api + request;
        return request;
    }
}
