package kalpas.VK;

import java.util.List;
import java.util.Map;

import kalpas.VKModule;
import kalpas.simple.FriendsGraph;
import kalpas.simple.GMLHelper;
import kalpas.simple.api.Friends;

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
            // friends = friends.addFields("uid", "first_name", "last_name",
            // "sex", "bdate");
            List<VKUser> myFriends = friends.get("1080446");
            Map<VKUser, List<VKUser>> allFriends = friends.get(myFriends);
            // allFriends.put(new VKFriend().setUid("1080446"), myFriends);
            GMLHelper gmlWriter = new GMLHelper();
            FriendsGraph graph = new FriendsGraph();
            // graph.add(new VKFriend().setUid("1080446"), myFriends);
            graph.addInterconnections(allFriends);
            gmlWriter.writeToFile("test", graph);
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
