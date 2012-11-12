package kalpas;

import kalpas.simple.DO.NewFriendsGraph;
import kalpas.simple.VKApi.Friends;
import kalpas.simple.helper.GMLHelper;
import kalpas.simple.helper.HttpClientContainer;

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
            Logger.getLogger("org.apache").setLevel(Level.FATAL);

            injector = Guice.createInjector(new VKModule());
            container = injector.getInstance(HttpClientContainer.class);
            // Wall wall = injector.getInstance(Wall.class).addCount(200);
            // WallComments wallComments =
            // injector.getInstance(WallComments.class);
            // Likes likes = injector.getInstance(Likes.class).addType("post");
            // List<WallPost> posts = wall.get("-26599838");// -26599838
            // List<Comment> comments = wallComments.get(posts.get(0).to_id,
            // posts.get(0).id);
            // logger.debug("start");
            // wallComments.get(posts.subList(0, 200));
            // logger.debug("start");
            // likes.get(posts);
            // logger.debug("end");

            NewFriendsGraph graph = injector.getInstance(NewFriendsGraph.class);
            graph.getMyFriends();
            GMLHelper helper = new GMLHelper();
            helper.writeToFile("out/gml/mileStone", graph.edges.asMap());
            


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
