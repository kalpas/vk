package kalpas.VK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.kalpas.VKCore.VKModule;
import net.kalpas.VKCore.simple.helper.HttpClientContainer;

@ContextConfiguration(classes = VKModule.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseApiTest {

    static private Logger logger = LogManager.getLogger(BaseApiTest.class);

    @Autowired
    private static HttpClientContainer container;

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
}
