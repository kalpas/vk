package kalpas.VK;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MultiMapTest {

    private static final Logger logger = Logger.getLogger(MultiMapTest.class);

    @BeforeClass
    public static void before() {
        BasicConfigurator.configure();
    }


    @Test
    public void listMultiMapTest() {
        Multimap<String, String> map = ArrayListMultimap.create();

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
