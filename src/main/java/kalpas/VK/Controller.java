package kalpas.VK;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Controller {

    private VK VK = new VK();

    private static Controller instance = new Controller();

    private Controller() {
    }

    public static Controller getInstance() {
        return instance;
    }

    public void start() {
        VK.auth();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
            String s = br.readLine();
            while (!s.equals("quit")) {
                VK.request(s);
                s = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // VK.getFriendsList();
        return;
    }

}
