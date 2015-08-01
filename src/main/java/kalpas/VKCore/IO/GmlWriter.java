package kalpas.VKCore.IO;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import kalpas.VKCore.simple.DO.User;
import kalpas.VKCore.simple.graph.GraphNode;
import kalpas.VKCore.simple.graph.UserGraph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GmlWriter {

    private static Logger logger = LogManager.getLogger(GmlWriter.class);
    
    public void saveGraphToFile(String fileName, UserGraph graph) {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("first_name");
        hashSet.add("last_name");
        saveGraphToFile(fileName, graph, hashSet);
    }

    public void saveGraphToFile(String fileName, UserGraph graph, Set<String> nodeProps) {
        BufferedWriter bw = null;
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(fileName + ".gml", false),
                    "UTF-8");
            bw = new BufferedWriter(fileWriter);
            save(bw, graph, checkProps(nodeProps));
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
    }
    
    private Set<String> checkProps(Set<String> nodeProps) {
        if(nodeProps == null){
            nodeProps = new HashSet<>();
        }
        nodeProps.add("id");
        return nodeProps;
    }

    private void save(BufferedWriter bw, UserGraph graph, Set<String> nodeProps)
            throws IOException {
        addHeading(bw);
        for (GraphNode node : graph.nodes.keySet()) {
            startNode(bw);
            for (String prop : nodeProps) {
                addProp(bw, node.user, prop);
            }
            endNode(bw);
        }
        for (Entry<GraphNode, GraphNode> entry : graph.nodes.entries()) {
            startEdge(bw);
            addSourceTarget(bw, entry.getKey().user.id, entry.getValue().user.id);
            endEdge(bw);
        }
        endFile(bw);

    }

    private void endFile(BufferedWriter bw) throws IOException {
        bw.write("]");
        
    }

    private void endEdge(BufferedWriter bw) throws IOException {
        bw.write("\t]");
        bw.newLine();

    }

    private void addSourceTarget(BufferedWriter bw, String source, String target) throws IOException {
        bw.write("\t\tsource " + source);
        bw.newLine();
        bw.write("\t\ttarget " + target);
        bw.newLine();
    }

    private void startEdge(BufferedWriter bw) throws IOException {
        bw.write("\tedge [");
        bw.newLine();
    }

    private void endNode(BufferedWriter bw) throws IOException {
        bw.write("\t]");
        bw.newLine();

    }

    private void startNode(BufferedWriter bw) throws IOException {
        bw.write("\tnode [");
        bw.newLine();

    }

    private void addProp(BufferedWriter bw, User user, String prop) throws IOException {
        try {
            String value = (String) User.class.getDeclaredField(prop).get(user);
            bw.write("\t\t" + prop + " " + value);
            bw.newLine();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void addHeading(BufferedWriter bw) throws IOException {
        bw.write("graph [");
        bw.newLine();
        bw.write("\tdirected 1");
        bw.newLine();
        bw.write("\tid 1");
        bw.newLine();
    }

}
