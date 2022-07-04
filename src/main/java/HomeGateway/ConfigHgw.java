package main.java.HomeGateway;

import java.io.*;

public class ConfigHgw {
    private String hostBroker;

    public String getInfoBroker() {
        try {
            FileReader path = new FileReader("src/data/configHGW.txt");
            BufferedReader reader = new BufferedReader(path);
            String line;
            while ((line = reader.readLine()) != null) {
                hostBroker = "tcp://" + line + ":1883";
            }
            return hostBroker;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
