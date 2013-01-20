package kalpas.VKCore;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.VKApi.Friends;
import kalpas.VKCore.simple.helper.GMLHelper;
import kalpas.VKCore.simple.helper.HttpClientContainer;
import kalpas.VKCore.stats.GroupStats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Hello world!
 * 
 */

// TODO timeout for socket operations
public class App {
    static private Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        // logger.info("Started");
        // Controller.getInstance().start();

        Injector injector;
        Friends friends;
        HttpClientContainer container = null;
        try {

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

            // NewFriendsGraph graph =
            // injector.getInstance(NewFriendsGraph.class);
            // graph.getMyFriends();
            // GMLHelper helper = new GMLHelper();
            // helper.writeToFile("out/gml/mileStone", graph.edges.asMap());

            GroupStats stats = injector.getInstance(GroupStats.class);

            String gid = "21642795";
            Multimap<User, User> multimap = stats.getMemberNetwork("kubana_bel_tour");

            GMLHelper.writeToFile("out\\gml\\" + App.class.toString(), multimap);
            


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
