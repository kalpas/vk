package kalpas.VKCore;

import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.helper.GMLHelper;
import kalpas.VKCore.simple.helper.HttpClientContainer;
import kalpas.VKCore.stats.WallStats;
import kalpas.VKCore.stats.DO.EdgeProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Hello world!
 * 
 */

// TODO timeout for socket operations
public class App {
    static private Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        Injector injector;
        HttpClientContainer container = null;
        try {

            injector = Guice.createInjector(new VKModule());
            container = injector.getInstance(HttpClientContainer.class);

            WallStats stats = injector.getInstance(WallStats.class);

            Multimap<User, Map.Entry<EdgeProperties, User>> multimap = stats.getRepostsNet("45435572");
            GMLHelper.writeToFileM("reposts" + "_out", multimap);

            multimap = stats.getInteractions("45435572", null);
            GMLHelper.writeToFileM2("interactions" + "_out", multimap);

            stats.saveDynamics("45435572", null);
            logger.debug("wohoo");

        } catch (Exception e) {
            logger.fatal("!", e);
        } finally {
            try {
                container.shutdown();
            } catch (Exception e) {
            }
        }

    }
}
