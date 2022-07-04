package main.java.HomeGateway.EchonteLite;

import com.sonycsl.echo.EchoFrame;
import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.DeviceObject;
import main.java.Extensions.Extentions;
import main.java.HomeGateway.MqttBroker.TopicDevices;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CommandMessage {
    private TopicDevices topicDevices;
    private CheckReliable checkReliable = new CheckReliable();

    public CommandMessage(TopicDevices topicDevices) {
        this.topicDevices = topicDevices;
    }

    public synchronized void sendCommandMessage(String topic, String message) {
        String[] splitTopic = topic.split("/");
        String getDev = splitTopic[0] + "/" + splitTopic[1] + "/" + splitTopic[2] + "/" + splitTopic[3] + "/" + splitTopic[4] + "/" + splitTopic[5];
        String[] splitDevice = topicDevices.getDevice(getDev).split("/");
        String MAC = splitDevice[0];
        String groupCode = splitDevice[1];
        String classCode = splitDevice[2];
        String instanceCode = splitDevice[3];
        String dev = MAC + "/" + groupCode + "/" + classCode + "/" + instanceCode;
        System.out.println(dev);
        try {
            System.out.println(topicDevices.myDevices.size());
            for (DeviceObject devices: topicDevices.myDevices) {

                if (devices.getMacAddress().equals(MAC)
                        && Extentions.ConvertByteToI(devices.getClassGroupCode()) == Extentions.ConvertStringToInt(groupCode)
                        && Extentions.ConvertByteToI(devices.getClassCode()) == Extentions.ConvertStringToInt(classCode)
                        && Extentions.ConvertByteToI(devices.getInstanceCode()) == Extentions.ConvertStringToInt(instanceCode)) {
                    String messageParse = message.replaceAll("\\\\", "" );
                    JSONParser cmdMessage = new JSONParser();
                    JSONObject objCmdMessage = (JSONObject) cmdMessage.parse(messageParse.substring(1, messageParse.length()-1));
                    for (Object obj: objCmdMessage.keySet()) {
                        // Get Epc~Key and Edt~Value from JsonMessage
                        String epcStr = obj.toString();
                        String edtStr = objCmdMessage.get(obj).toString();
//                        System.out.println("EPC+EDT: "+ epcStr +" "+edtStr);
                        //Convert epc and edt from string type to byte and byte respectively
                        long edtLong = Long.parseLong(edtStr, 16);
                        int epcInt = Integer.parseInt(epcStr,16);
                        byte[] edtByteArr = BigInteger.valueOf(edtLong).toByteArray();
                        byte epcByte = Integer.valueOf(epcInt).byteValue();
                        System.out.println("EDT" + edtByteArr);
                        System.out.println("EPC" + epcByte);
                        short tidCommand = devices.set().reqSetProperty(epcByte, edtByteArr).send().getTID();
                        System.out.println("TID RES: " + tidCommand);
                        devices.checkReliable.addDeviceCommand(dev + "~" + tidCommand, "sent");
//                    System.out.println("REQ: " + dev + "~" + tidCommand);

                        Calendar calendarReq = Calendar.getInstance();
                        calendarReq.add(Calendar.SECOND, 3);
                        Date timeCheck = calendarReq.getTime();
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Timer timeRequest = new Timer();
                                timeRequest.scheduleAtFixedRate(
                                        new TimerTask() {
                                            public void run() {
                                                try {
//                                            System.out.println("CHECK: " + dev + "~" + tidCommand);
//                                            System.out.println("STATUS: " + devices.checkReliable.getDeviceStatus(dev + "~" + tidCommand));
                                                    if (devices.checkReliable.getDeviceStatus(dev + "~" + tidCommand).equals("received")) {
                                                        devices.checkReliable.removeDeviceCommand(dev + "~" + tidCommand);
                                                        System.out.println("Receive Command Message: " + tidCommand);
                                                        timeRequest.cancel();
                                                    } else {
                                                        devices.set().reqSetProperty(epcByte, edtByteArr).reSend(tidCommand);
                                                        System.out.println("Loss Command Packet: " + tidCommand);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },0, 1000);
                            }
                        }, timeCheck);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
