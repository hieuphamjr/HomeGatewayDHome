package main.java.HomeGateway.MqttBroker;

import main.java.HomeGateway.ConfigHgw;

import main.java.HomeGateway.EchonteLite.EchonetLiteController;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class MqttConnection {
    private String host;
    private static MqttClient mqttClientSub;
    private static MqttClient mqttClientPub;
    private EchonetLiteController echo;

    public void setEcho(EchonetLiteController echo) {
        this.echo = echo;
    }

    public void ConnectMqttBroker(String username, String password, TopicDevices topicDevices) {
        ConfigHgw config = new ConfigHgw();

        DataTransfer data = new DataTransfer(topicDevices);
        host = config.getInfoBroker();
        try {
            mqttClientSub = new MqttClient(host, MqttClient.generateClientId());
            MqttConnectOptions conOptsSub = setUpConnectionOptions(username, password);
            mqttClientSub.connect(conOptsSub);
            mqttClientSub.subscribe(TopicDevices.getTopicForNewDevice());
            for (String topicSub:TopicDevices.getTopicSubcribe()) {
                mqttClientSub.subscribe(topicSub);
            }
            data.getMessageFromBroker(mqttClientSub);

            mqttClientPub = new MqttClient(host, MqttClient.generateClientId());
            MqttConnectOptions conOptsPub = setUpConnectionOptions(username, password);
            mqttClientPub.connect(conOptsPub);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public synchronized static MqttClient getMqttClientPub() {
        return mqttClientPub;
    }

    public static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }
}
