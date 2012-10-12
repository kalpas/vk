package kalpas.VK;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpClientContainer {

    private static HttpClient instance;
    
    private HttpClientContainer() {

    }
    
    public static HttpClient getInstance() {
        if (instance == null) {
            instance = new DefaultHttpClient();
        }
        return instance;
    }

}
