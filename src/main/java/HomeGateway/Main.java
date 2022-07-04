package main.java.HomeGateway;

import main.java.HomeGateway.Account.Account;
import main.java.HomeGateway.DHomeDevice.DHome;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeClientObserver;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeConnection;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeController;
import main.java.HomeGateway.EchonteLite.EchonetLiteController;
import main.java.HomeGateway.MqttBroker.MqttConnection;
import main.java.HomeGateway.MqttBroker.TopicDevices;

import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
//        System.setProperty("java.net.preferIPv4Stack" , "true");
//        Account account = new Account();
//        TopicDevices topicDevices = new TopicDevices();
//        topicDevices.createHashMap();
//        MqttConnection mqttConnection = new MqttConnection();
//        mqttConnection.ConnectMqttBroker(account.getUsername(), account.getPassword(), topicDevices);
//        EchonetLiteController echonetLiteController = new EchonetLiteController(topicDevices);
        DHome dhome = new DHome("192.168.0.100");
        DHomeClientObserver obs = new DHomeClientObserver();
        DHomeConnection conn = new DHomeConnection();
        conn.connectDDP(dhome, obs);
        conn.subscribe(obs);
        DHomeController controller = new DHomeController(conn);
        controller.toggle(240);
    }
}
