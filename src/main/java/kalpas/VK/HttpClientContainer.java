package kalpas.VK;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;


public class HttpClientContainer {

    private static HttpClient instance;
    
    private HttpClientContainer() {

    }
    
    public static HttpClient getInstance() {
        if (instance == null) {
            PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
            instance = new DefaultHttpClient(manager);
            instance.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.IGNORE_COOKIES);

        }
        return instance;
    }

}
