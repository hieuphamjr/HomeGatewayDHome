package main.java.HomeGateway.MqttBroker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.java.HomeGateway.DHomeDevice.DHome;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeClientObserver;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeConnection;
import main.java.HomeGateway.DHomeDevice.DHomeController.DHomeController;
import main.java.HomeGateway.MessageToBroker.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.java.Extensions.Extensions.*;
import static main.java.HomeGateway.ConfigHomeGateway.*;


public class MqttConnection {
    private static MqttClient mqttClientPub;

    public void ConnectMqttBroker() throws URISyntaxException, InterruptedException, MqttException {
        MqttClient mqttClientSub = new MqttClient(brokerURL, "subClient");
        mqttClientPub = new MqttClient(brokerURL, "pubClient");
        MqttConnectOptions opt = setUpConnectionOptions();
        mqttClientPub.connect(opt);
        mqttClientSub.setCallback(new MyCallBack());
        mqttClientSub.connect(opt);
        DHome dhome = new DHome("192.168.4.1");
        DHomeClientObserver obs = new DHomeClientObserver();
        DHomeConnection conn = new DHomeConnection(obs);
        conn.connectDDP(dhome);
        DHomeController controller = new DHomeController(conn);
        for (String topic : subscribeTopics) {
            mqttClientSub.subscribeWithResponse(topic, (topic1, message) -> {
                System.out.println("Received message from Broker, topic: " + topic1 + " at " + System.currentTimeMillis());
                if (isJSON(message.toString())) {
                    Gson gson = new Gson();
                    JSONParser parser = new JSONParser();
                    JSONObject jsonMessage = (JSONObject) parser.parse(message.toString());

                    if (topic1.equals(controlTopic)) {
                        if (!(jsonMessage.containsKey("id") && jsonMessage.containsKey("status"))) {
                            ErrorMessage error = new ErrorMessage(controlTopic, 2);
                            message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                        } else {
                            int id = ((Long) jsonMessage.get("id")).intValue();
                            int status = ((Long) jsonMessage.get("status")).intValue();
                            HashMap<String, Object> deviceCollection = (HashMap<String, Object>) obs.mCollections.get("device");
                            if (deviceCollection.containsKey(idToString(id))) {
                                if (status == 0) {
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
                                    ErrorMessage error = new ErrorMessage(topic1, 2);
                                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                                }
                            } else {
                                ErrorMessage error = new ErrorMessage(topic1, 1);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                    if (topic1.equals(getListTopic)) {
                        if (!jsonMessage.containsKey("cmd")) {
                            ErrorMessage error = new ErrorMessage(controlTopic, 2);
                            message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                        } else {
                            String cmd = jsonMessage.get("cmd").toString();
                            if (cmd.equals("getAllRoom")) {
                                HashMap<String, Object> groupCollection = (HashMap<String, Object>) obs.mCollections.get("group");
                                List<JsonObject> listGroup = getList(groupCollection);
                                GetListMessage msg = new GetListMessage(cmd, listGroup);
                                message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                            } else if (cmd.equals("getAllDevice")) {
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
                    if (topic1.equals(addGroupTopic)) {
                        if (!jsonMessage.containsKey("name")) {
                            ErrorMessage error = new ErrorMessage(controlTopic, 2);
                            message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                        } else {
                            String name = jsonMessage.get("name").toString();
                            controller.addRoom(name);
                            HashMap<String, Object> groupCollection = (HashMap<String, Object>) obs.mCollections.get("group");
                            int newGroupId = 0;
                            for (Map.Entry<String, Object> entry : groupCollection.entrySet()) {
                                String key = entry.getKey();
                                JsonObject json = gson.toJsonTree(groupCollection.get(key)).getAsJsonObject();
                                if (newGroupId < json.get("id").getAsInt()) newGroupId = json.get("id").getAsInt();
                            }
                            AddRoomMessage msg = new AddRoomMessage(newGroupId, name);
                            message.setPayload(msg.createMessage().getBytes(StandardCharsets.UTF_8));
                        }
                    }
                    if (topic1.equals(deleteGroupTopic)) {
                        if (!jsonMessage.containsKey("id")) {
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
                                ErrorMessage error = new ErrorMessage(topic1, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                    if (topic1.equals(deleteDeviceTopic)) {
                        if (!(jsonMessage.containsKey("id") && jsonMessage.containsKey("netadd"))) {
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
                                ErrorMessage error = new ErrorMessage(topic1, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                    if (topic1.equals(configDeviceTopic)) {
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
                    if (topic1.equals(addDeviceTopic)) {
                        if (!jsonMessage.containsKey("cmd")) {
                            ErrorMessage error = new ErrorMessage(controlTopic, 2);
                            message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                        } else {
                            String cmd = jsonMessage.get("cmd").toString();
                            if (cmd.equals("addNewDevice")) {
                                controller.addSwitch();
                            } else {
                                ErrorMessage error = new ErrorMessage(topic1, 2);
                                message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    }
                } else {
                    ErrorMessage error = new ErrorMessage(topic1, 3);
                    message.setPayload(error.createMessage().getBytes(StandardCharsets.UTF_8));
                }
                message.setQos(0);
                mqttClientPub.publish(topic1.substring(0, topic1.length() - 1) + "r", message);
                System.out.println("Published message to Broker, topic: " + getTopicReceiver(topic) + " at " + System.currentTimeMillis());
            });
        }
    }

    public static MqttConnectOptions setUpConnectionOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        connOpts.setConnectionTimeout(100000000);
        return connOpts;
    }

    public synchronized static MqttClient getMqttClientPub() {
        return mqttClientPub;
    }

}
