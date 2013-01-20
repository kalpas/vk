package kalpas.VKCore.simple.VKApi.client;

import java.io.IOException;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.client.HttpAsyncClient;

//FIXME it is not finished
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

    public VKHttpsClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected HttpResponse sendInternal(String request) {
        Validate.notNull(accessToken);

        request = "https://" + api + request + "&access_token=" + this.accessToken;

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

        request = "https://" + api + request + "&access_token=" + this.accessToken;

        HttpGet get = new HttpGet(request);
        return asyncClient.execute(get, null);
    }

}
