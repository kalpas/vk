package kalpas;

import kalpas.VK.HttpClientContainer;
import kalpas.simple.AuthHelper;
import kalpas.simple.VKClient;
import kalpas.simple.VKHttpClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class VKModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VKClient.class).to(VKHttpClient.class);
        bind(Boolean.class).annotatedWith(Names.named("isHttps")).toInstance(
                false);
    }

    @Provides
    public VKHttpClient provideVKHttpClient(
            @Named("accessToken") String accessToken,
            @Named("secret") String secret, HttpClientContainer container) {
        return new VKHttpClient(accessToken, secret, container);
    }
    
    @Provides
    @Named("accessToken")
    public String provideAccessToken(AuthHelper helper){
        return helper.getAccessToken();
    }
    
    @Provides
    @Named("secret")
    public String provideSecret(AuthHelper helper){
        return helper.getSecret();
    }
    


}
