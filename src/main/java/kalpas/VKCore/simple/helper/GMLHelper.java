package kalpas.VKCore.simple.helper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.UserRelation;
import kalpas.VKCore.stats.DO.EdgeProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;

public class GMLHelper {

    private static Logger logger = LogManager.getLogger(GMLHelper.class);

    public static void writeToFile(String fileName, Map<UserRelation, Collection<User>> edges) {
        BufferedWriter bw = null;
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(fileName + ".gml", false),
                    "UTF-8");
            bw = new BufferedWriter(fileWriter);
            bw.write("graph [");
            bw.newLine();
            bw.write("\tdirected 1");
            bw.newLine();
            bw.write("\tid 1");
            bw.newLine();
            for (UserRelation node : edges.keySet()) {
                bw.write("\tnode [");
                bw.newLine();
                bw.write("\t\tid " + node.user.uid);
                bw.newLine();
                bw.write("\t\tlabel \"" + node.user.first_name + " " + node.user.last_name + "\"");
                bw.newLine();
                bw.write("\t\tsex " + node.user.sex);
                bw.newLine();
                bw.write("\t]");
                bw.newLine();
            }
            for (Map.Entry<UserRelation, Collection<User>> entry : edges.entrySet()) {
                for (User user : entry.getValue()) {
                    UserRelation key = entry.getKey();

                    bw.write("\tedge [");
                    bw.newLine();
                    bw.write("\t\tsource " + key.user.uid);
                    bw.newLine();
                    bw.write("\t\ttarget " + user.uid);
                    bw.newLine();
                    bw.write("\t\tweight " + key.relations.get(user.uid).getWeight());
                    bw.newLine();
                    bw.write("\t\tlikes " + key.relations.get(user.uid).likes);
                    bw.newLine();
                    bw.write("\t\tcomments " + key.relations.get(user.uid).comments);
                    bw.newLine();
                    bw.write("\t\tposts " + key.relations.get(user.uid).wallPosts);
                    bw.newLine();
                    bw.write("\t]");
                    bw.newLine();
                }
            }
            bw.write("]");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.error("IO exception", e);
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
            }
        }
        return;
    }

    public static void writeToFile(String fileName, Multimap<User, User> multimap) {
        BufferedWriter bw = null;
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(fileName + ".gml", false),
                    "UTF-8");
            bw = new BufferedWriter(fileWriter);
            bw.write("graph [");
            bw.newLine();
            bw.write("\tdirected 1");
            bw.newLine();
            bw.write("\tid 1");
            bw.newLine();
            for (User node : multimap.keySet()) {
                bw.write("\tnode [");
                bw.newLine();
                bw.write("\t\tid " + node.uid);
                bw.newLine();
                bw.write("\t\tlabel \"" + node.first_name + " " + node.last_name + "\"");
                bw.newLine();
                bw.write("\t\tsex " + node.sex);
                bw.newLine();
                bw.write("\t]");
                bw.newLine();
            }
            for (User user : multimap.keySet()) {
                for (User friend : multimap.get(user)) {
                    if (friend == null) {
                        continue;
                    }

                    bw.write("\tedge [");
                    bw.newLine();
                    bw.write("\t\tsource " + user.uid);
                    bw.newLine();
                    bw.write("\t\ttarget " + friend.uid);
                    bw.newLine();
                    bw.write("\t]");
                    bw.newLine();
                }
            }
            bw.write("]");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.error("IO exception", e);
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
            }
        }
        return;
    }

    public static void writeToFileM(String fileName, Multimap<User, Map.Entry<EdgeProperties, User>> multimap) {
        BufferedWriter bw = null;
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(fileName + ".gml", false),
                    "UTF-8");
            bw = new BufferedWriter(fileWriter);
            bw.write("graph [");
            bw.newLine();
            bw.write("\tdirected 1");
            bw.newLine();
            bw.write("\tid 1");
            bw.newLine();
            for (User node : multimap.keySet()) {
                bw.write("\tnode [");
                bw.newLine();
                bw.write("\t\tid " + node.uid);
                bw.newLine();
                bw.write("\t\tlabel \"" + node.first_name + " " + node.last_name + "\"");
                bw.newLine();
                bw.write("\t\tsex " + node.sex);
                bw.newLine();
                bw.write("\t]");
                bw.newLine();
            }
            for (User user : multimap.keySet()) {
                for (Map.Entry<EdgeProperties, User> friend : multimap.get(user)) {
                    if (friend == null) {
                        continue;
                    }

                    bw.write("\tedge [");
                    bw.newLine();
                    bw.write("\t\tsource " + user.uid);
                    bw.newLine();
                    bw.write("\t\ttarget " + friend.getValue().uid);
                    bw.newLine();
                    bw.write("\t\tweight " + friend.getKey().reposts);
                    bw.newLine();
                    bw.write("\t]");
                    bw.newLine();
                }
            }
            bw.write("]");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.error("IO exception", e);
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
            }
        }
        return;
    }

    // FIXME awful
    public static void writeToFileM2(String fileName, Multimap<User, Map.Entry<EdgeProperties, User>> multimap) {
        BufferedWriter bw = null;
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(fileName + ".gml", false),
                    "UTF-8");
            bw = new BufferedWriter(fileWriter);
            bw.write("graph [");
            bw.newLine();
            bw.write("\tdirected 1");
            bw.newLine();
            bw.write("\tid 1");
            bw.newLine();
            for (User node : multimap.keySet()) {
                bw.write("\tnode [");
                bw.newLine();
                bw.write("\t\tid " + node.uid);
                bw.newLine();
                bw.write("\t\tlabel \"" + node.first_name + " " + node.last_name + "\"");
                bw.newLine();
                bw.write("\t\tsex " + node.sex);
                bw.newLine();
                bw.write("\t]");
                bw.newLine();
            }
            for (User user : multimap.keySet()) {
                for (Map.Entry<EdgeProperties, User> friend : multimap.get(user)) {
                    if (friend == null) {
                        continue;
                    }

                    bw.write("\tedge [");
                    bw.newLine();
                    bw.write("\t\tsource " + friend.getValue().uid);
                    bw.newLine();
                    bw.write("\t\ttarget " + user.uid);
                    bw.newLine();
                    bw.write("\t\tweight " + friend.getKey().likes);
                    bw.newLine();
                    bw.write("\t]");
                    bw.newLine();
                }
            }
            bw.write("]");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.error("IO exception", e);
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
            }
        }
        return;
    }

}
