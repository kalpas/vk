package kalpas.VK.wall.stats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.kalpas.VKCore.simple.helper.GMLHelper;
import net.kalpas.VKCore.simple.helper.HttpClientContainer;
import net.kalpas.VKCore.stats.WallStats;

@RunWith(SpringJUnit4ClassRunner.class)
public class WallStatsTest {

	private Logger logger = LogManager.getLogger(WallStatsTest.class);

	@Autowired
	private HttpClientContainer container;

	@Autowired
	private WallStats stats;

	@Test
	public void test_hp() {
		try {

			GMLHelper.writeToFileM("out\\gml\\" + getClass() + new DateTime().getMillis(),
			        stats.getRepostsNet("45435572", 50));

		} catch (Exception e) {
			logger.fatal("!", e);
		} finally {
			try {
				container.shutdown();

			} catch (Exception e) {
			}
		}
	}

	@Test
	public void test_dynam_hp() {
		try {
			stats.saveDynamics("45435572", 50);

		} catch (Exception e) {
			logger.fatal("!", e);
		} finally {
			try {
				container.shutdown();

			} catch (Exception e) {
			}
		}
	}

	@Test
	public void test_inter_hp() {
		try {
			GMLHelper.writeToFileM2("out\\gml\\" + this.getClass().toString() + new DateTime().getMillis(),
			        stats.getInteractions("45435572", 50));

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
