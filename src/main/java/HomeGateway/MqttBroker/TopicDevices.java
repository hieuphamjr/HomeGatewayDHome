package main.java.HomeGateway.MqttBroker;

import com.sonycsl.echo.eoj.device.DeviceObject;
import main.java.HomeGateway.Account.Account;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class TopicDevices {
    //    File f = new File(path);
//    f.getParentFile().mkdirs();
//    FileOutputStream fos = new FileOutputStream(f);
    private final String path = "src/data/TopicDevices.txt";
    public ArrayList<DeviceObject> myDevices = new ArrayList<>();
    private static final ConcurrentHashMap<String, String> TopicForDevice = new ConcurrentHashMap<>();
    private static final ArrayList<String> topicSubscribe = new ArrayList<>();
    private static final ArrayList<String> checkDevice = new ArrayList<>();
    private final File topicFile = new File(path);
    private String[] devices;
    private String[] topicForDevice;

    //Add topic for new device in database
    public void createHashMap() {
        try {
            if (topicFile.length() != 0) {
                FileInputStream fis = new FileInputStream(topicFile);
                Scanner scanFile = new Scanner(fis);
                while (scanFile.hasNextLine()) {
                    String[] splitTopic = scanFile.nextLine().split("~");
                    topicSubscribe.add(splitTopic[1] + "/command");
                    TopicForDevice.put(splitTopic[0], splitTopic[1]);
                    TopicForDevice.put(splitTopic[1], splitTopic[0]);
                }
                scanFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTopicNewDevice(String MAC, String groupCode, String classCode, String instanceCode, String topic) {
        String device = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        topicSubscribe.add(topic + "/command");
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
        return TopicForDevice.containsKey(device);
    }

    public static ArrayList<String> getTopicSubscribe() {
        return topicSubscribe;
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
        return checkDevice.contains(device);
    }
}
