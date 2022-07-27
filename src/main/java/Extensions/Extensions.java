package main.java.Extensions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static main.java.HomeGateway.ConfigHomeGateway.controlTopic;
import static main.java.HomeGateway.Main.clientPub;

public class Extensions {
    public static float ConvertByteToFloat(byte[] b) {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        return buffer.getFloat();
    }

    public static int ConvertByteToInt(byte[] b) {
        int value = 0;
        for(int i = 0; i<b.length; i++){
            int n = (b[i] < 0 ? (int)b[i] + 256 : (int)b[i]) << (8*i);
            value += n;
        }
        return value;
    }

    public static String ConvertByteToString(byte[] b) {
        return new String(b);
    }

    public static int ConvertStringToInt(String s) {
        int i = Integer.parseInt(s);
        if (i < 0) {
            i += 256;
        }
        return i;
    }
    public static int ConvertByteToInt(byte b) {
        return (b & 0xFF);
    }

    public static String ConvertDateToString(Calendar calendar) {
        String dateConv;
        DateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calendar = Calendar.getInstance();
        dateConv = time.format(calendar.getTime());
        return dateConv;
    }

    public static Date GetDate(Calendar calendar) {
        DateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calendar = Calendar.getInstance();
        String dateString = time.format(calendar.getTime());
        try {
            return time.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doubleToDate(Double dateDouble) {
        long dateLong = (long) ((double) dateDouble);
        DateFormat formatter =  new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date itemDate = new Date(dateLong);
        return formatter.format(itemDate);
    }

    public static void dumpMap(Map<String, Object> result) {
        for (String key : result.keySet()) {
            System.out.println(key + ": " + result.get(key).toString());
        }
        System.out.println("++++++++++");
    }
    public static boolean isJSON(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public static String getTopicReceiver (String topic){
        return topic.substring(0, topic.length() - 1) + "r";
    }

    public static String idToString(Integer id) {
        String stringId = "~" + String.valueOf(id);
        return stringId;
    }

    public static List getList(HashMap<String, Object> map) {
        List list = new ArrayList();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Gson gson = new Gson();
            String key = entry.getKey();
            JsonObject jsonGroup = gson.toJsonTree(map.get(key)).getAsJsonObject();
            list.add(jsonGroup);
        }
        return list;
    }

    public static void clientPublish(String topic, MqttMessage message) throws MqttException {
        clientPub.publish(topic.substring(0, controlTopic.length() - 1) + "r", message);
        System.out.println("Publish message to Broker, topic: " + topic);
    }
}
