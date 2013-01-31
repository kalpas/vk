package kalpas.VKCore;

import kalpas.VKCore.simple.VKApi.client.VKClient;
import kalpas.VKCore.simple.VKApi.client.VKHttpClient;
import kalpas.VKCore.simple.VKApi.client.VKHttpsClient;
import kalpas.VKCore.simple.helper.AuthHelper;
import kalpas.VKCore.simple.helper.HttpClientContainer;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class VKModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VKClient.class).to(VKHttpClient.class);
        bind(Boolean.class).annotatedWith(Names.named("isHttps")).toInstance(false);
        bind(Gson.class).in(Singleton.class);
        bind(JsonParser.class).in(Singleton.class);
        // Sleep.interval = 0L;
    }

    @Provides
    public VKHttpClient provideVKHttpClient(@Named("accessToken") String accessToken, @Named("secret") String secret,
            HttpClientContainer container) {
        return new VKHttpClient(accessToken, secret, container);
    }

    @Provides
    public VKHttpsClient provideVKHttpsClient(@Named("accessToken") String accessToken, HttpClientContainer container) {
        return new VKHttpsClient(accessToken, container);
    }

    @Provides
    @Named("accessToken")
    public String provideAccessToken(AuthHelper helper) {
        return helper.getAccessToken();
    }

    @Provides
    @Named("secret")
    public String provideSecret(AuthHelper helper) {
        return helper.getSecret();
    }

    @Provides
    public MapJoiner provideMapJoiner() {
        return Joiner.on("&").withKeyValueSeparator("=");
    }
}
