package kalpas.VK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class Controller {

    private Logger            logger   = Logger.getLogger(Controller.class);

    private VK                VK       = new VK();
    private static Controller instance = new Controller();

    private Controller() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void start() throws IOException {
        VK.auth(false);
        // try {
        // BufferedReader br = new BufferedReader(new InputStreamReader(
        // System.in));
        // String s = br.readLine();
        // while (!s.equals("quit")) {
        // VK.request(s);
        // s = br.readLine();
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        Long startGetFriends = System.nanoTime();
        List<VKUser> friends = VK.getFriendsList("17702327");
        // List<VKFriend> friends = VK.getFriendsList("53822984");
        // friends.addAll(VK.getFriendsList("14218071"));
        // friends.addAll(VK.getFriendsList("1080446"));
        // friends.addAll(VK.getFriendsList("19306295"));
        // friends.addAll(VK.getFriendsList("17702327"));
        Long endGetFriends = System.nanoTime();
        logger.debug("total time for my friends " + (endGetFriends - startGetFriends) * 1.0E-9);
        startGetFriends = System.nanoTime();
        Map<String, List<VKUser>> friendMap = VK.getFrinedsOfFriends(friends);
        endGetFriends = System.nanoTime();
        logger.debug("total time for friends of friends " + (endGetFriends - startGetFriends) * 1.0E-9);
        List<Map.Entry<String, String>> edges = new ArrayList<>();
        List<String> uids = new ArrayList<String>();
        for (VKUser f : friends) {
            uids.add(f.getUid());
        }
        for (String listKey : friendMap.keySet()) {
            for (VKUser friend : friendMap.get(listKey)) {
                for (String uid : uids) {
                    if (uid.equals(friend.getUid()))
                        edges.add(new AbstractMap.SimpleEntry(listKey, friend.getUid()));
                }
            }
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(new DateTime().getMillis() + ".gml"), true));
        bw.write("graph [");
        bw.newLine();
        bw.write("\tdirected 0");
        bw.newLine();
        bw.write("\tid 1");
        bw.newLine();
        for (VKUser f : friends) {
            bw.write("\tnode [");
            bw.newLine();
            bw.write("\t\tid " + f.getUid());
            bw.newLine();
            bw.write("\t\tlabel \"" + f.getFirstName() + " " + f.getLastName() + "\"");
            bw.newLine();
            bw.write("\t\tsex " + f.getSex());
            bw.newLine();
            bw.write("\t]");
            bw.newLine();
        }
        for (Map.Entry<String, String> entry : edges) {
            bw.write("\tedge [");
            bw.newLine();
            bw.write("\t\tsource " + entry.getKey());
            bw.newLine();
            bw.write("\t\ttarget " + entry.getValue());
            bw.newLine();
            bw.write("\t]");
            bw.newLine();
        }
        bw.write("]");
        bw.flush();
        bw.close();
        return;
    }

}
