package main.java.HomeGateway;

import main.java.HomeGateway.MqttBroker.MqttConnection;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws MqttException, URISyntaxException, InterruptedException, IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        MqttConnection HomeGateway = new MqttConnection();
        HomeGateway.ConnectMqttBroker();
    }
}
