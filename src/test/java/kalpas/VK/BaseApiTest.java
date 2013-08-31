package kalpas.VK;

import kalpas.VKCore.VKModule;
import kalpas.VKCore.simple.helper.HttpClientContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class BaseApiTest {

    static private Logger logger = LogManager.getLogger(BaseApiTest.class);

    private static Injector injector  = Guice.createInjector(new VKModule());

    private static HttpClientContainer container = null;

    @BeforeClass
    public static void prepareBeforeClass() {
        // BasicConfigurator.configure();
        // LogManager.getLogger("org.apache").setLevel(Level.ERROR);
        container = getInjector().getInstance(HttpClientContainer.class);
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        try {
            container.shutdown();
        } catch (Exception e) {
        }
    }

    /**
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * @return the injector
     */
    public static Injector getInjector() {
        return injector;
    }
}
