package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class ConfigDeviceMessage {
    private final Integer id;
    private final String name;
    private final Integer type;
    private final Integer groupId;
    private final long updatedAt;

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
