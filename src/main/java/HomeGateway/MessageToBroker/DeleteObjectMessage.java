package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class DeleteObjectMessage {
    private final String cmd;
    private final Integer id;
    private final long time;

    public DeleteObjectMessage(String cmd, Integer id) {
        this.cmd = cmd;
        this.id = id;
        this.time = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("cmd", cmd);
        jsonMessage.put("id", id);
        jsonMessage.put("time", time);
        return jsonMessage.toString();
    }
}
