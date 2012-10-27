package kalpas.VK;

import java.util.List;
import java.util.Map;

import kalpas.VKModule;
import kalpas.simple.Friends;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Hello world!
 * 
 */
public class App {
    static private Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        // logger.info("Started");
        // Controller.getInstance().start();

        Injector injector;
        Friends friends;
        HttpClientContainer container = null;
        try {
            BasicConfigurator.configure();
            injector = Guice.createInjector(new VKModule());
            container = injector.getInstance(HttpClientContainer.class);
            Logger.getLogger("org.apache").setLevel(Level.FATAL);
            friends = injector.getInstance(Friends.class);
            List<VKFriend> myFriends = friends.get("1080446");
            Map<String, List<VKFriend>> allFriends = friends.get(myFriends);
            allFriends.put("1080446", myFriends);
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
