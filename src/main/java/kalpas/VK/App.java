package kalpas.VK;

import java.util.List;

import kalpas.VKModule;
import kalpas.simple.api.Friends;
import kalpas.simple.api.Wall;
import kalpas.simple.api.WallPost;

import org.apache.log4j.BasicConfigurator;
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
            Wall wall = injector.getInstance(Wall.class).addCount(0);
            List<WallPost> posts = wall.get("1080446").getValue();

            // Logger.getLogger("org.apache").setLevel(Level.FATAL);
            // friends =
            // injector.getInstance(Friends.class).addFields("first_name",
            // "last_name", "sex");
            // List<VKUser> myFriends = friends.get("1080446");
            // Users users =
            // injector.getInstance(Users.class).addFields("first_name",
            // "last_name", "sex");
            // Map<VKUser, List<VKUser>> allFriends = friends.get(myFriends);
            // GMLHelper gmlWriter = new GMLHelper();
            // FriendsGraph graph = new FriendsGraph();
            // graph.addInterconnections(allFriends);
            // gmlWriter.writeToFile("test", graph);

            // Users users = injector.getInstance(Users.class);
            //
            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(System.in));
            // String line = in.readLine();
            // ;
            // while (!"q".equals(line)) {
            // try {
            // users.addFields(line.split(",")).get("1080446");
            // line = in.readLine();
            // } catch (Exception e) {
            // logger.error("errm", e);
            // }
            // }
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
