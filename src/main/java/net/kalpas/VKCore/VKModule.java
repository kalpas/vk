package net.kalpas.VKCore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import net.kalpas.VKCore.simple.VKApi.client.VKHttpsClient;
import net.kalpas.VKCore.simple.helper.AuthHelper;
import net.kalpas.VKCore.simple.helper.HttpClientContainer;

@Configuration
@ComponentScan("net.kalpas")
public class VKModule {

	@Bean(name = "isHttps")
	public Boolean isHttps() {
		return true;
	}

	// @Bean
	// public VKHttpClient provideVKHttpClient(@Qualifier("accessToken") String
	// accessToken,
	// @Qualifier("secret") String secret, HttpClientContainer container) {
	// return new VKHttpClient(accessToken, secret, container);
	// }

	@Bean
	public VKHttpsClient VKHttpsClient(@Qualifier("accessToken") String accessToken,
	        HttpClientContainer container) {
		return new VKHttpsClient(accessToken, container);
	}

	@Bean(name = "accessToken")
	public String provideAccessToken(AuthHelper helper) {
		return helper.getAccessToken();
	}

	@Bean(name = "secret")
	public String provideSecret(AuthHelper helper) {
		return helper.getSecret();
	}

	@Bean
	public MapJoiner provideMapJoiner() {
		return Joiner.on("&").withKeyValueSeparator("=");
	}

	@Bean
	public Gson gson() {
		return new Gson();
	}

	@Bean
	public JsonParser jsonParser() {
		return new JsonParser();
	}
}
