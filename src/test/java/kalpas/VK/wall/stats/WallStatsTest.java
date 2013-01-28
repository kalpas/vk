package kalpas.VK.wall.stats;

import kalpas.VKCore.VKModule;
import kalpas.VKCore.simple.helper.HttpClientContainer;
import kalpas.VKCore.stats.WallStats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class WallStatsTest {

    private Logger logger = LogManager.getLogger(WallStatsTest.class);

    @Test
    public void test_hp() {
        Injector injector;
        HttpClientContainer container = null;
        try {
            injector = Guice.createInjector(new VKModule());
            container = injector.getInstance(HttpClientContainer.class);

            WallStats stats = injector.getInstance(WallStats.class);
            stats.getInteractions("45435572");
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
