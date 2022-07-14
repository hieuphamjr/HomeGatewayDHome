package main.java.HomeGateway;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.java.HomeGateway.DHomeDevice.DHome;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeClientObserver;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeConnection;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeController;
import main.java.HomeGateway.MessageToBroker.*;
import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static main.java.Extensions.Extensions.*;
import static main.java.HomeGateway.ConfigHomeGateway.*;

public class Main {
    public static MqttClient mqttClient;
    public static MqttClient clientPub;
    public static void main(String[] args) throws MqttException, URISyntaxException, InterruptedException, IOException {
        System.setProperty("java.net.preferIPv4Stack" , "true");
        Gson gson = new Gson();
        DHome dhome = new DHome("192.168.0.103");
        DHomeClientObserver obs = new DHomeClientObserver();
        DHomeConnection conn = new DHomeConnection();
        conn.connectDDP(dhome, obs);
        conn.subscribe(obs);
        Thread.sleep(1000);

        DHomeController controller = new DHomeController(conn);
        mqttClient = new MqttClient(brokerURL, "subClient");
        clientPub = new MqttClient(brokerURL, "pubClient");
        MqttConnectOptions opt = new MqttConnectOptions();
        opt.setUserName(username);
        opt.setPassword(password.toCharArray());
        opt.setAutomaticReconnect(true);
        opt.setCleanSession(true);
        clientPub.connect(opt);
        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean status, String host) {
                System.out.println("Connected to MQTT Broker server " + host);
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
        });
        mqttClient.connect(opt);
        for (String topic : subscribeTopics) {
            mqttClient.subscribeWithResponse(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Received message from Broker, topic: " + topic);
                    if (isJSON(message.toString())){
                        JSONParser parser = new JSONParser();
                        JSONObject jsonMessage = (JSONObject) parser.parse(message.toString());

                        if (topic.equals(controlTopic)) {
                            if (!(jsonMessage.containsKey("id") && jsonMessage.containsKey("status"))) {
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                int id = ((Long) jsonMessage.get("id")).intValue();
                                int status = ((Long) jsonMessage.get("status")).intValue();
                                HashMap<String, Object> deviceCollection = (HashMap<String, Object>) obs.mCollections.get("device");
                                if (deviceCollection.containsKey(idToString(id))) {
                                    if (status == 0 ) {
                                        controller.turnOff(id);
                                        ControlDeviceMessage msg = new ControlDeviceMessage(id, 0);
                                        message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                    } else if (status == 1) {
                                        controller.turnOn(id);
                                        ControlDeviceMessage msg = new ControlDeviceMessage(id, 1);
                                        message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                    } else if (status == 3) {
                                        controller.curtainUp(id);
                                        ControlDeviceMessage msg = new ControlDeviceMessage(id, 3);
                                        message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                    } else if (status == 4) {
                                        controller.curtainStop(id);
                                        ControlDeviceMessage msg = new ControlDeviceMessage(id, 4);
                                        message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                    } else if (status == 5) {
                                        controller.curtainDown(id);
                                        ControlDeviceMessage msg = new ControlDeviceMessage(id, 5);
                                        message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                    } else {
                                        ErrorMessage error = new ErrorMessage(topic, 2);
                                        message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                    }
                                } else {
                                    ErrorMessage error = new ErrorMessage(topic, 1);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                        }
                        if (topic.equals(getListTopic)) {
                            if (!jsonMessage.containsKey("cmd")){
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                String cmd = jsonMessage.get("cmd").toString();
                                if(cmd.equals("getAllRoom")){
                                    HashMap<String, Object> groupCollection = (HashMap<String, Object>) obs.mCollections.get("group");
                                    List<JsonObject> listGroup = getList(groupCollection);
                                    GetListMessage msg = new GetListMessage(cmd, listGroup);
                                    message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                } else if(cmd.equals("getAllDevice")){
                                    HashMap<String, Object> deviceCollection = (HashMap<String, Object>) obs.mCollections.get("device");
                                    List<JsonObject> listDevice = getList(deviceCollection);
                                    GetListMessage msg = new GetListMessage(cmd, listDevice);
                                    message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                } else {
                                    ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                        }
                        if (topic.equals(addGroupTopic)) {
                            if (!jsonMessage.containsKey("name")){
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                String name = jsonMessage.get("name").toString();
                                controller.addRoom(name);
                                HashMap<String, Object> groupCollection = (HashMap<String, Object>) obs.mCollections.get("group");
                                Integer newGroupId = 0;
                                for (Map.Entry<String, Object> entry : groupCollection.entrySet()) {
                                    String key = entry.getKey();
                                    JsonObject json = gson.toJsonTree(groupCollection.get(key)).getAsJsonObject();
                                    if (newGroupId < json.get("id").getAsInt()) newGroupId = json.get("id").getAsInt();
                                }
                                AddRoomMessage msg = new AddRoomMessage(newGroupId, name);
                                message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        if (topic.equals(deleteGroupTopic)) {
                            if (!jsonMessage.containsKey("id")){
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                int id = ((Long) jsonMessage.get("id")).intValue();
                                HashMap<String, Object> groupCollection = (HashMap<String, Object>) obs.mCollections.get("group");
                                if (groupCollection.containsKey(idToString(id))) {
                                    controller.removeRoom(id);
                                    DeleteObjectMessage msg = new DeleteObjectMessage("delGroup", id);
                                    message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                } else {
                                    ErrorMessage error = new ErrorMessage(topic, 2);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                        }
                        if (topic.equals(deleteDeviceTopic)) {
                            if (!(jsonMessage.containsKey("id") && jsonMessage.containsKey("netadd"))){
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                int id = ((Long) jsonMessage.get("id")).intValue();
                                int netadd = ((Long) jsonMessage.get("netadd")).intValue();
                                HashMap<String, Object> deviceCollection = (HashMap<String, Object>) obs.mCollections.get("device");
                                if (deviceCollection.containsKey(idToString(id))) {
                                    JsonObject device = gson.toJsonTree(deviceCollection.get(idToString(id))).getAsJsonObject();
                                    if (netadd == device.get("netadd").getAsInt()) {
                                        controller.removeDevice(id, netadd);
                                        DeleteObjectMessage msg = new DeleteObjectMessage("delDev", id);
                                        message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                    } else {
                                        ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                        message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                    }
                                } else {
                                    ErrorMessage error = new ErrorMessage(topic, 2);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                        }
                        if (topic.equals(configDeviceTopic)) {
                            if (!(jsonMessage.containsKey("name")
                                    && jsonMessage.containsKey("type")
                                    && jsonMessage.containsKey("idx")
                                    && jsonMessage.containsKey("netadd")
                                    && jsonMessage.containsKey("endpoint")
                                    && jsonMessage.containsKey("groupId"))) {
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                String name = jsonMessage.get("name").toString();
                                int type = ((Long) jsonMessage.get("type")).intValue();
                                int idx = ((Long) jsonMessage.get("idx")).intValue();
                                int netadd = ((Long) jsonMessage.get("netadd")).intValue();
                                int endpoint = ((Long) jsonMessage.get("endpoint")).intValue();
                                int groupId = ((Long) jsonMessage.get("groupId")).intValue();
                                if ((type == 0 || type == 2) && (idx == 48 || idx == 49 || idx == 50 || idx == 51)) {
                                        HashMap<String, Object> deviceCollection = (HashMap<String, Object>) obs.mCollections.get("device");
                                        boolean isValidDevice = false;
                                        for (Map.Entry<String, Object> entry : deviceCollection.entrySet()) {
                                            String key = entry.getKey();
                                            JsonObject json = gson.toJsonTree(deviceCollection.get(key)).getAsJsonObject();
                                            if (json.get("netadd").getAsInt() == netadd
                                                    && json.get("endpoint").getAsInt() == endpoint) {
                                                HashMap<String, Object> groupCollection = (HashMap<String, Object>) obs.mCollections.get("group");
                                                isValidDevice = groupCollection.containsKey(idToString(groupId));
                                            }
                                        }
                                        if (isValidDevice) {
                                            controller.addDevice(name, type, idx, netadd, endpoint, groupId, obs);
                                            ConfigDeviceMessage msg = new ConfigDeviceMessage(obs.devId.intValue(), name, type, groupId);
                                            message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                                        } else {
                                            ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                            message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                        }
                                } else {
                                    ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                        }
                        if (topic.equals(addDeviceTopic)) {
                            if (!jsonMessage.containsKey("cmd")){
                                ErrorMessage error = new ErrorMessage(controlTopic, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else {
                                String cmd = jsonMessage.get("cmd").toString();
                                if(cmd.equals("addNewDevice")){
                                    controller.addSwitch();
                                } else {
                                    ErrorMessage error = new ErrorMessage(topic, 2);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                        }
                    } else {
                        ErrorMessage error = new ErrorMessage(topic, 3);
                        message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                    }
                    message.setQos(0);
                    clientPub.publish(topic.substring(0, topic.length() - 1) + "r", message);
                    System.out.println("Publish message to Broker, topic: " + topic.substring(0, topic.length() - 1) + "r");
                }
            });
        }
    }
}
