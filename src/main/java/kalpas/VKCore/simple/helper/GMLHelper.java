package kalpas.VKCore.simple.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.DO.UserRelation;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class GMLHelper {

    private Logger logger = Logger.getLogger(GMLHelper.class);

    public void writeToFile(String fileName, Map<UserRelation, Collection<User>> edges) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(fileName + new DateTime().getMillis() + ".gml"), true));
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

}
