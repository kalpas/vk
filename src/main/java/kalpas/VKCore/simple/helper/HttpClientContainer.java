package kalpas.VKCore.simple.helper;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class HttpClientContainer {

    private static Logger          logger = LogManager.getLogger(HttpClientContainer.class);

    private static HttpClient      instance;

    private static HttpAsyncClient asyncInstance;

    public HttpClient getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
                    manager.setMaxTotal(200);
                    instance = new DefaultHttpClient(manager);
                    instance.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
                }
            }
        }
        return instance;
    }

    public HttpAsyncClient getAsyncInstance() {
        if (asyncInstance == null) {
            synchronized (this) {
                if (asyncInstance == null) {
                    try {
                        ConnectingIOReactor reactor = new DefaultConnectingIOReactor();
                        PoolingClientAsyncConnectionManager manager = new PoolingClientAsyncConnectionManager(reactor);
                        manager.setDefaultMaxPerRoute(400);
                        manager.setMaxTotal(400);
                        asyncInstance = new DefaultHttpAsyncClient(manager);
                        asyncInstance.start();
                    } catch (IOReactorException e) {
                        logger.fatal("IOReactor exception", e);
                    }
                }
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

    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }

}
