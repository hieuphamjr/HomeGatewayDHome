package main.java.HomeGateway.MqttBroker;

import main.java.HomeGateway.EchonteLite.CommandMessage;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AnalyzeMessage extends Thread {
    private final String topicDevice;
    private final MqttMessage mqttMessage;
    private final TopicDevices topicDevices;

    AnalyzeMessage(String topic, MqttMessage message, TopicDevices topicDevices) {
        topicDevice = topic;
        mqttMessage = message;
        this.topicDevices = topicDevices;
    }

    public void run() {

        try {
            if (topicDevice.equals(TopicDevices.getTopicForNewDevice())) {
                System.out.println("MESSAGE: " + mqttMessage);
                JSONParser parserMessage = new JSONParser();
                JSONObject topicForNewDevice = (JSONObject) parserMessage.parse(mqttMessage.toString());
                topicDevices.addTopicNewDevice(topicForNewDevice.get("MAC").toString(),
                        topicForNewDevice.get("GroupCode").toString(), topicForNewDevice.get("ClassCode").toString(),
                        topicForNewDevice.get("InstanceCode").toString(), topicForNewDevice.get("Topic").toString());
            } else {
                String[] splitTopic = topicDevice.split("/");
                if (splitTopic[6].equals("command")) {
                    CommandMessage command = new CommandMessage(topicDevices);
                    command.sendCommandMessage(topicDevice, mqttMessage.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
