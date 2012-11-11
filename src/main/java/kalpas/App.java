package kalpas;

import java.util.ArrayList;
import java.util.List;

import kalpas.simple.DO.WallPost;
import kalpas.simple.VKApi.Friends;
import kalpas.simple.VKApi.Likes;
import kalpas.simple.VKApi.Wall;
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
            Wall wall = injector.getInstance(Wall.class);
            Likes likes = injector.getInstance(Likes.class).addType("post");
            List<WallPost> posts = wall.get("-26599838").getValue();// -26599838
            List<Integer> lenghts = new ArrayList<>();
            logger.debug("start");
            // for (WallPost post : posts) {
            // post.likes = likes.get(post.to_id, post.id);
            // lenghts.add(Integer.valueOf(post.likes.users.length));
            // }
            // logger.debug("end");
            // for (WallPost post : posts) {
            // post.likes = null;
            // }
            logger.debug("start");
            likes.get(posts);
            logger.debug("end");
            
            // for(int i = 0; i < posts.size();i++ ){
            // assertEquals(posts.get(i).likes.users.length,
            // likes.get(posts.get(i).to_id, posts.get(i).id).users.length);
            // }

            // friends = injector.getInstance(Friends.class);
            // List<User> myFriends = friends.get("1080446");
            // Users users = injector.getInstance(Users.class).addFields("uid",
            // "first_name", "last_name", "nickname",
            // "screen_name", "sex", "bdate", "city", "country", "timezone",
            // "photo", "photo_medium", "photo_big",
            // "has_mobile", "contacts", "education", "online", "counters",
            // "lists", "can_post",
            // "can_see_all_posts", "activity", "last_seen", "relation",
            // "exports", "wall_comments",
            // "connections", "interests", "movies", "tv", "books", "games",
            // "about", "domain");
            // myFriends = users.get(myFriends);
            // User me = users.get("1080446");
            // me = users.get(me);
            // List<User> test = users.batchGet("1080446");
            // List<User> test2 = users.batchGet(myFriends);
            //
            // Map<User, List<User>> allFriends = friends.get(myFriends);

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
