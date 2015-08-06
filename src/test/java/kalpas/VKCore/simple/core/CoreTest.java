package kalpas.VKCore.simple.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kalpas.VK.BaseApiTest;
import net.kalpas.VKCore.IO.GmlWriter;
import net.kalpas.VKCore.simple.core.Core;

public class CoreTest extends BaseApiTest {

	@Autowired
    private Core core;

    @Test
    public void test_hp() {

        GmlWriter writer = new GmlWriter();
        writer.saveGraphToFile("test_drive", core.buildGraph("1080446", false));

    }

}
