package kalpas.VK;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingClientAsyncConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class HttpClientContainer {

    private static Logger          logger = Logger.getLogger(HttpClientContainer.class);

    private static HttpClient      instance;

    private static HttpAsyncClient asyncInstance;

    public HttpClient getInstance() {
        if (instance == null) {
            PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
            manager.setMaxTotal(200);
            instance = new DefaultHttpClient(manager);
            instance.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.IGNORE_COOKIES);

        }
        return instance;
    }

    public HttpAsyncClient getAsyncInstance() {
        if (asyncInstance == null) {
            try {
                ConnectingIOReactor reactor = new DefaultConnectingIOReactor();
                PoolingClientAsyncConnectionManager manager = new PoolingClientAsyncConnectionManager(
                        reactor);
                manager.setMaxTotal(200);
                asyncInstance = new DefaultHttpAsyncClient(manager);
                asyncInstance.start();
            } catch (IOReactorException e) {
                logger.fatal("IOReactor exception", e);
            }
        }
        return asyncInstance;
    }

    public void shutdown() {
        try {
            asyncInstance.shutdown();
        } catch (InterruptedException e) {
            logger.error("error shutting down http client", e);
        }
    }

}
