package kalpas.VK.csv;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.kalpas.VKCore.simple.helper.CSVHelper;

public class CSVHelperTest {

    private Logger logger = LogManager.getLogger(CSVHelperTest.class);

    @Test
    public void tsv_write_hp() throws IOException {

        try (CSVHelper helper = new CSVHelper("test")) {
            helper.writeHeader("id", "value");
            helper.writeRow("1", "10");
        } catch (IOException e) {
            logger.error("smth happened", e);
        }

    }

}
