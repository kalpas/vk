package net.kalpas.VKCore.simple.helper;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class HttpClientContainer {

	private static Logger logger = LogManager.getLogger(HttpClientContainer.class);

	private static CloseableHttpClient instance;

	private static CloseableHttpAsyncClient asyncInstance;

	public HttpClient getInstance() {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = HttpClients.custom().disableConnectionState().disableContentCompression()
					        .disableCookieManagement().setMaxConnPerRoute(Integer.MAX_VALUE).build();
				}
			}
		}
		return instance;
	}

	public HttpAsyncClient getAsyncInstance() {
		if (asyncInstance == null) {
			synchronized (this) {
				if (asyncInstance == null) {
					asyncInstance = HttpAsyncClients.createDefault();
					asyncInstance.start();
				}
			}
		}
		return asyncInstance;
	}

	public void shutdown() {
		try {
			asyncInstance.close();
			instance.close();
		} catch (IOException e) {
			logger.error("error shutting down http client", e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		shutdown();
		super.finalize();
	}

}
