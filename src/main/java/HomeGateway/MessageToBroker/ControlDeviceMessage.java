package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

public class ControlDeviceMessage {
    private String cmd;
    private Integer id;
    private Integer status;
    private long time;

    public ControlDeviceMessage(Integer id, Integer status) {
        this.cmd = "ctrlDev";
        this.id = id;
        this.status = status;
        this.time = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("cmd", cmd);
        jsonMessage.put("id", id);
        jsonMessage.put("status", status);
        jsonMessage.put("time", time);
        return jsonMessage.toString();
    }
}
