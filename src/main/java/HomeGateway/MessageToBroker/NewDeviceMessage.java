package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class NewDeviceMessage {
    private final String cmd;
    private final Object device;
    private final long time;

    public NewDeviceMessage(Object device) {
        this.cmd = "newDevice";
        this.device = device;
        this.time = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("cmd", cmd);
        jsonMessage.put("device", device);
        jsonMessage.put("time", time);
        return jsonMessage.toString();
    }
}
