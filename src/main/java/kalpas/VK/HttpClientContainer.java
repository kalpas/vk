package kalpas.VK;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpClientContainer {

    private static HttpClient instance;
    
    private HttpClientContainer() {

    }
    
    public static HttpClient getInstance() {
        if (instance == null) {
            instance = new DefaultHttpClient();
            instance.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.IGNORE_COOKIES);
        }
        return instance;
    }

}
