package kalpas.VKCore.simple.core;

import kalpas.VK.BaseApiTest;
import kalpas.VKCore.IO.GmlWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoreTest extends BaseApiTest {

    private Core core;

    @Before
    public void before() throws InterruptedException {
        core = getInjector().getInstance(Core.class);
    }

    @After
    public void tearDown() {
        core = null;
    }

    @Test
    public void test_hp() {

        GmlWriter writer = new GmlWriter();
        writer.saveGraphToFile("test_drive", core.buildGraph("1080446", false));

    }

}
