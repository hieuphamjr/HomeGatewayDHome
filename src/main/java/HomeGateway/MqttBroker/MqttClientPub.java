package main.java.HomeGateway.MqttBroker;

import main.java.HomeGateway.ConfigHomeGateway;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClientPub {
    private final MqttClient mqttClientPub;

    public MqttClientPub() throws MqttException {
        this.mqttClientPub = new MqttClient(ConfigHomeGateway.brokerURL, "pubclient");
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setUserName(ConfigHomeGateway.username);
        connOpts.setPassword(ConfigHomeGateway.password.toCharArray());
        connOpts.setConnectionTimeout(100000000);
        mqttClientPub.connect(connOpts);
    }

    public MqttClient getMqttClientPub() {
        return mqttClientPub;
    }
}
