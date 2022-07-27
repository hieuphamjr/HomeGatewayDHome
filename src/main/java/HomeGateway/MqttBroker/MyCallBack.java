package main.java.HomeGateway.MqttBroker;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyCallBack implements MqttCallbackExtended {
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Connected to MQTT Broker server " + serverURI);
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
