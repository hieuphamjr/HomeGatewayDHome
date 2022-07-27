package main.java.HomeGateway.MqttBroker;

import org.eclipse.paho.client.mqttv3.*;

public class DataTransfer {
    public DataTransfer() {

    }

    public void getMessageFromBroker(MqttClient myClient) {
        myClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean status, String host) {
                System.out.println("Connection " + status + "; server " + host);
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("MQTT subscriber error: Lost MQTT Connection" + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                System.out.println("topic" + mqttMessage);
                if (topic.equals("testtopic/s")) {

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    public void sendMessageToBroker(MqttClient myClient, String topic, String payLoad) {
        myClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("MQTT subscriber error: Lost MQTT Connection" + cause.getMessage());
                System.out.println("msg " + cause.getMessage());
                System.out.println("loc " + cause.getLocalizedMessage());
                System.out.println("cause " + cause.getCause());
                System.out.println("except " + cause);
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        try {
            MqttMessage message = new MqttMessage(payLoad.getBytes());
            myClient.publish(topic, message);
            //System.out.println(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
