package kalpas.VK;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Hello world!
 * 
 */
public class App {
    static private Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ERROR);
        logger.info("Started");
        Controller.getInstance().start();

    }
}
