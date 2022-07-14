package main.java.HomeGateway;

import main.java.HomeGateway.DHomeDevice.DHome;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeClientObserver;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeConnection;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeController;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Test {
    public static void main(String[] args) throws JSONException, URISyntaxException, InterruptedException {
        String groupId = "~" + String.valueOf(100);
        System.out.println(groupId);
    }
}
