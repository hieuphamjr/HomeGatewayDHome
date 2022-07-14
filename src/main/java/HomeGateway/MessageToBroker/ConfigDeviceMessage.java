package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class ConfigDeviceMessage {
    private Integer id;
    private String name;
    private Integer type;
    private Integer groupId;
    private long updatedAt;

    public ConfigDeviceMessage(Integer id, String name, Integer type, Integer groupId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.groupId = groupId;
        this.updatedAt = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("id", id);
        jsonMessage.put("name", name);
        jsonMessage.put("type", type);
        jsonMessage.put("groupId", groupId);
        jsonMessage.put("updatedAt", updatedAt);
        return jsonMessage.toString();
    }
}
