package kalpas.simple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import kalpas.VK.VKUser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class GMLHelper {

    private Logger logger = Logger.getLogger(GMLHelper.class);

    public void writeToFile(String fileName, FriendsGraph graph) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(fileName + new DateTime().getMillis() + ".gml"), true));
            bw.write("graph [");
            bw.newLine();
            bw.write("\tdirected 0");
            bw.newLine();
            bw.write("\tid 1");
            bw.newLine();
            for (VKUser node : graph.getNodes()) {
                bw.write("\tnode [");
                bw.newLine();
                bw.write("\t\tid " + node.getUid());
                bw.newLine();
                bw.write("\t\tlabel \"" + node.getFirstName() + " " + node.getLastName() + "\"");
                bw.newLine();
                bw.write("\t\tsex " + node.getSex());
                bw.newLine();
                bw.write("\t]");
                bw.newLine();
            }
            for (Map.Entry<String, String> entry : graph.getEdges()) {
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
