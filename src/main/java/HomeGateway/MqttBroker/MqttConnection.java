package main.java.HomeGateway.MqttBroker;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import static main.java.HomeGateway.ConfigHomeGateway.*;


public class MqttConnection {
    private static MqttClient mqttClientSub;
    private static MqttClient mqttClientPub;

    public void ConnectMqttBroker() {

        DataTransfer data = new DataTransfer();
        try {
            mqttClientSub = new MqttClient(brokerURL, MqttClient.generateClientId());
            MqttConnectOptions conOptsSub = setUpConnectionOptions();
            mqttClientSub.connect(conOptsSub);
            mqttClientSub.subscribe("home1/a");
            data.getMessageFromBroker(mqttClientSub);

            mqttClientPub = new MqttClient(brokerURL, MqttClient.generateClientId());
            MqttConnectOptions conOptsPub = setUpConnectionOptions();
            mqttClientPub.connect(conOptsPub);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public synchronized static MqttClient getMqttClientPub() {
        return mqttClientPub;
    }

    public static MqttConnectOptions setUpConnectionOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }
}
