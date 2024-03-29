package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

import java.util.List;

public class GetListMessage {
    private final String cmd;
    private final List list;
    private final long time;

    public GetListMessage(String cmd, List list) {
        this.cmd = cmd;
        this.list = list;
        this.time = System.currentTimeMillis();
    }

    public String createMessage() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("cmd", cmd);
        jsonMessage.put("data", list);
        jsonMessage.put("time", time);
        return jsonMessage.toString();
    }
}
