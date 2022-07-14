package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class ErrorMessage {
    private String topic;
    private Integer errorCode;
    private long time;

    public ErrorMessage(String topic, Integer errorCode) {
        this.topic = topic;
        this.errorCode = errorCode;
        this.time = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("topic", topic);
        jsonMessage.put("errorCode", errorCode);
        jsonMessage.put("time", time);
        return jsonMessage.toString();
    }
}
