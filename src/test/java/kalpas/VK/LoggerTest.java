package kalpas.VK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class LoggerTest {
    
    private Logger logger = LogManager.getLogger(LoggerTest.class.getName());
    
    // @BeforeClass
    // public static void before() {
    // System.out.println(System.getProperty("log4j.configurationFile"));
    // System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,"log4j2-test.xml");
    // }

    @Test
    public void log_exception_hp(){
        logger.info(System.getProperty("log4j.configurationFile"));
        try{
            testRecursive(10);
        } catch (IllegalArgumentException e) {
            logger.fatal("fatal", e);
        }
        
    }

    private void testRecursive(int i) {
        if (--i > 0) {
            testRecursive(i);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
