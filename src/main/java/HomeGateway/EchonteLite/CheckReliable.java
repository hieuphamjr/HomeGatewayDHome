package main.java.HomeGateway.EchonteLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class CheckReliable {
    private ConcurrentHashMap<String, String> IPAddress = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> CheckReceivePacket = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> CheckMessageCommand = new ConcurrentHashMap<>();

    public synchronized void addDeviceReceive(String device, String status) {
        if (getDeviceReceive(device) == null) {
            CheckReceivePacket.put(device, status);
        } else {
            CheckReceivePacket.replace(device, status);
        }
    }

    public synchronized String getDeviceReceive(String device) {
        String timeString = CheckReceivePacket.get(device);
        return timeString;
    }

    public synchronized void removeDevicePacket(String device) {
        CheckReceivePacket.remove(device);
    }

    public void addDeviceCommand(String device, String status) {
        if (getDeviceStatus(device) == null) {
            CheckMessageCommand.put(device, status);
        } else {
            CheckMessageCommand.replace(device, status);
        }
    }

    public synchronized String getDeviceStatus(String device) {
        String timeString = CheckMessageCommand.get(device);
        return timeString;
    }

    public synchronized void removeDeviceCommand(String device) {
        CheckMessageCommand.remove(device);
    }

    public synchronized void addMAC(String IP, String MAC) {
        if (getDeviceStatus(IP) == null) {
            IPAddress.put(IP, MAC);
        } else {
            IPAddress.replace(IP, MAC);
        }
    }

    public synchronized String getMAC(String IP) {
        String mac = CheckMessageCommand.get(IP);
        return mac;
    }

}
