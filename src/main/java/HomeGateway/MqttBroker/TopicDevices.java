package main.java.HomeGateway.MqttBroker;

import com.sonycsl.echo.eoj.device.DeviceObject;
import main.java.HomeGateway.Account.Account;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Integer;

public class TopicDevices {
//    File f = new File(path);
//    f.getParentFile().mkdirs();
//    FileOutputStream fos = new FileOutputStream(f);
    private String path = "src/data/TopicDevices.txt";
    public ArrayList<DeviceObject> myDevices = new ArrayList<>();
    private static ConcurrentHashMap<String, String> TopicForDevice = new ConcurrentHashMap<>();
    private static ArrayList<String> topicSubcribe = new ArrayList<String>();
    private static ArrayList<String> checkDevice = new ArrayList<String>();
    private File topicFile = new File(path);
    private String[] devices;
    private String[] topicForDevice;

    //Add topic for new device in database
    public void createHashMap() {
        try {
            if (topicFile.length() != 0) {
                FileInputStream fis = new FileInputStream(topicFile);
                Scanner scanFile = new Scanner(fis);
                while(scanFile.hasNextLine()) {
                    String[] splitTopic = scanFile.nextLine().split("~");
                    topicSubcribe.add(splitTopic[1] + "/command");
                    TopicForDevice.put(splitTopic[0], splitTopic[1]);
                    TopicForDevice.put(splitTopic[1], splitTopic[0]);
                }
                scanFile.close();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addTopicNewDevice(String MAC, String groupCode, String classCode, String instanceCode, String topic) {
        String device = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        topicSubcribe.add(topic + "/command");
        TopicForDevice.put(device, topic);
        TopicForDevice.put(topic, device);
        topicFile.getParentFile().mkdirs();
        try {
            String data = device + "~" + topic;
            FileWriter fr = new FileWriter(topicFile, true);
            fr.write(data + "\n");
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Add topic: " + topic);
    }
    //Get device's topic
    public String getTopicForDevice(String MAC, byte groupCode, byte classCode, byte instanceCode) {
        String device = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        return TopicForDevice.get(device);
    }

    public String getDevice(String topic) {
        return TopicForDevice.get(topic);
    }

    //Check device's topic in database
    public boolean checkTopicForDevice(String MAC, byte groupCode, byte classCode, byte instanceCode) {
        String device = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        boolean isExistMAC = TopicForDevice.containsKey(device);
        if (isExistMAC) {
            return true;
        } else return false;
    }

    public static ArrayList<String> getTopicSubcribe() {
        return topicSubcribe;
    }

    public String registerDeviceTopic() {
        Account account = new Account();
        return "/" + account.getUsername() + "/" + account.getHomeId() + "/" + "registerTopicForDevice";
    }

    public String registerDevicePayload(String MAC, byte groupCode, byte classCode, byte instanceCode) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("MAC", MAC);
            payload.put("GroupCode", groupCode);
            payload.put("ClassCode", classCode);
            payload.put("InstanceCode", instanceCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload.toString();
    }

    public static String getTopicForNewDevice() {
        Account account = new Account();
        return "/" + account.getUsername() + "/" + account.getHomeId() + "/" + "topicForDevice";
    }

    public void isSendRegister(String MAC, byte groupCode, byte classCode, byte instanceCode) {
        String device = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        checkDevice.add(device);
    }

    public boolean checkDeviceRegister(String MAC, byte groupCode, byte classCode, byte instanceCode) {
        String device = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        boolean isSend = checkDevice.contains(device);
        if (isSend) {
            return true;
        } else return false;
    }
}
