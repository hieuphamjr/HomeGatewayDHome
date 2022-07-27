package main.java.HomeGateway.DHomeDevice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.java.HomeGateway.DHomeDevice.DHomeObject.Device;
import main.java.HomeGateway.DHomeDevice.DHomeObject.Group;

import java.io.IOException;
import java.util.HashMap;

import static main.java.Extensions.Extensions.idToString;

public class ClassConvert {
    public static Group jsonToGroup(JsonObject json) throws IOException {
        ObjectMapper m = new ObjectMapper();
        return m.readValue(json.toString(), Group.class);
    }

    public static Device jsonToDevice(JsonObject json) throws IOException {
        ObjectMapper m = new ObjectMapper();
        return m.readValue(json.toString(), Device.class);
    }

    public static Device getDeviceById(HashMap deviceList, Integer id) throws IOException {
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(deviceList.get(idToString(id))).getAsJsonObject();
        Device device = jsonToDevice(json);
        return device;
    }

    public static Group getGroupId(HashMap groupList, Integer id) throws IOException {
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(groupList.get(idToString(id))).getAsJsonObject();
        Group group = jsonToGroup(json);
        return group;
    }

}
