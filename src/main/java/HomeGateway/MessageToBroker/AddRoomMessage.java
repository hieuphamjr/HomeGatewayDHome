package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class AddRoomMessage {
    private final Integer id;
    private final String name;
    private final long time;

    public AddRoomMessage(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.time = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("id", id);
        jsonMessage.put("name", name);
        jsonMessage.put("time", time);
        return jsonMessage.toString();
    }
}
