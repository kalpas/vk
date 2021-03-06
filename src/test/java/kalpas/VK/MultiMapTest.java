package kalpas.VK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MultiMapTest {

    private static final Logger logger = LogManager.getLogger(MultiMapTest.class);

    @BeforeClass
    public static void before() {
        // BasicConfigurator.configure();
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "log4j2-test.xml");

    }


    @Test
    public void listMultiMapTest() {
        Multimap<String, String> map = ArrayListMultimap.create();

        map.put("fuits", "apple");
        map.put("fuits", "apple");
        map.put("fuits", "orange");
        map.put("fuits", "bannana");
        map.put("vegetables", "carrot");

        logger.info(map.get("fuits").toString());
        logger.info(map.get("vegetables").toString());

        logger.info(map.keySet().toString());

        logger.info(map.values().toString());
     }


}
