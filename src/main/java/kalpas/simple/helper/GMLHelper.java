package kalpas.simple.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import kalpas.simple.DO.User;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class GMLHelper {

    private Logger logger = Logger.getLogger(GMLHelper.class);

    public void writeToFile(String fileName, Map<User, Collection<User>> edges) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(fileName + new DateTime().getMillis() + ".gml"), true));
            bw.write("graph [");
            bw.newLine();
            bw.write("\tdirected 1");
            bw.newLine();
            bw.write("\tid 1");
            bw.newLine();
            for (User node : edges.keySet()) {
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
            for (Map.Entry<User, Collection<User>> entry : edges.entrySet()) {
                for (User user : entry.getValue()) {

                    bw.write("\tedge [");
                    bw.newLine();
                    bw.write("\t\tsource " + entry.getKey().uid);
                    bw.newLine();
                    bw.write("\t\ttarget " + user.uid);
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
