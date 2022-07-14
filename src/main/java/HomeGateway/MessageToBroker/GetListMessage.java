package main.java.HomeGateway.MessageToBroker;

import org.json.simple.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetListMessage {
    private String cmd;
    private List list;
    private long time;

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
