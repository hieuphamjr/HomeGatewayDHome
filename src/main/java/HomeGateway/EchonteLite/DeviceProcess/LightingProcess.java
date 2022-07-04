package main.java.HomeGateway.EchonteLite.DeviceProcess;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.EchoUtils;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;
import main.java.Extensions.Extentions;
import main.java.HomeGateway.EchonteLite.CheckReliable;
import main.java.HomeGateway.EchonteLite.CommandMessage;
import main.java.HomeGateway.EchonteLite.TimeRequest;
import main.java.HomeGateway.MqttBroker.DataTransfer;
import main.java.HomeGateway.MqttBroker.MqttConnection;
import main.java.HomeGateway.MqttBroker.TopicDevices;
import main.java.HomeGateway.Payload.LightingDevicePayload;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LightingProcess {
    private int countPayload = 0;
    private DataTransfer data = new DataTransfer();

    public synchronized void lightingDevice(GeneralLighting device, TopicDevices topicDevices, CheckReliable checkReliable) {
        LightingDevicePayload lightingPayload = new LightingDevicePayload();
        try {
            lightingPayload.createMessage();
            lightingPayload.setIPAddress(device.getNode().getAddress().getHostAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        device.setReceiver(new GeneralLighting.Receiver() {
            @Override
            protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv,
                                                EchoProperty property, boolean success) {
                super.onGetOperationStatus(eoj, tid, esv, property, success);
                if (!success) {
                    lightingPayload.alertSucessWrong();
                    return;
                }
                try {
//                                    System.out.println(property.edt[0]);
                    if (property.edt[0] == 48) {
                        lightingPayload.setOperationStatus(true);
                    } else if (property.edt[0] == 49) {
                        lightingPayload.setOperationStatus(false);
                    } else {
                        lightingPayload.setOperationStatus(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onGetIlluminanceLevel(EchoObject eoj, short tid, byte esv,
                                                 EchoProperty property, boolean success) {
                super.onGetIlluminanceLevel(eoj, tid, esv, property, success);
                if (!success) {
                    lightingPayload.alertSucessWrong();
                    return;
                }
                try {
                    lightingPayload.setLightingValue(Extentions.ConvertByteToInt(property.edt));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onGetMacAddress(EchoObject eoj, short tid, byte esv,
                                           EchoProperty property, boolean success) {
                //System.out.println("MAC");
                super.onGetMacAddress(eoj, tid, esv, property, success);
                if (!success) {
                    lightingPayload.alertSucessWrong();
                    return;
                }
                String macAddress = EchoUtils.toHexString(property.edt[0])+":"+ EchoUtils.toHexString(property.edt[1])
                        +":"+EchoUtils.toHexString(property.edt[2])+":"+EchoUtils.toHexString(property.edt[3])
                        +":"+EchoUtils.toHexString(property.edt[4])+":"+EchoUtils.toHexString(property.edt[5]);
                device.setMacAddress(macAddress);

                try {
                    lightingPayload.setMAC(macAddress);
                    String dev = macAddress + "/" + device.getClassGroupCode() + "/" + device.getClassCode() + "/" + device.getInstanceCode();
                    device.setDev(dev);
                    checkReliable.addDeviceReceive(dev + "~" + tid, "received");
                    if (topicDevices.checkTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                            device.getClassCode(), device.getInstanceCode())) {
                        data.sendMessageToBroker(MqttConnection.getMqttClientPub(),
                                topicDevices.getTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                                        device.getClassCode(), device.getInstanceCode()) + "/data", lightingPayload.getMessage());
                        countPayload ++;
                        //System.out.println("Payload Lighting Device: " + tid + " ---> " + "Device: " + dev +
                              //      " --- " + "Time: " + Extentions.ConvertDateToString(Calendar.getInstance()));
                    } else {
                        //System.out.println("Class Code: " + device.getClassCode());
                        //send request to register new device to MQTT Broker
                        if (!topicDevices.checkDeviceRegister(device.getMacAddress(), device.getClassGroupCode(),
                                device.getClassCode(), device.getInstanceCode())) {
                            data.sendMessageToBroker(MqttConnection.getMqttClientPub(), topicDevices.registerDeviceTopic(),
                                    topicDevices.registerDevicePayload(device.getMacAddress(), device.getClassGroupCode(),
                                            device.getClassCode(), device.getInstanceCode()));
                            topicDevices.isSendRegister(device.getMacAddress(), device.getClassGroupCode(),
                                    device.getClassCode(), device.getInstanceCode());
                            System.out.println("Register Lighting Device");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        Timer timeRequest = new Timer();
        timeRequest.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        try {
                            short tidRequest = device.get().reqGetIlluminanceLevel().reqGetInstallationLocation().
                                    reqGetOperationStatus().reqGetMacAddress().send().getTID();

                            String dev = device.getMacAddress() + "/" + device.getClassGroupCode() + "/" + device.getClassCode() + "/" +device.getInstanceCode() + "~" + tidRequest;
                            checkReliable.addDeviceReceive(dev, "sent");

                            //Reliable transfer
                            if (TimeRequest.illuminanceTime > TimeRequest.reliableTransferTime && topicDevices.checkTopicForDevice(device.getMacAddress(),
                                    device.getClassGroupCode(), device.getClassCode(), device.getInstanceCode())) {
                                if (checkReliable.getDeviceReceive(dev) != null) {
                                    Calendar calendarReq = Calendar.getInstance();
                                    calendarReq.add(Calendar.SECOND, TimeRequest.timeCheckReliable);
                                    Date timeCheck = calendarReq.getTime();
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (checkReliable.getDeviceReceive(dev).equals("received")) {
                                                    System.out.println("HGW is received packet: " + dev);
                                                } else {
                                                    String[] tidReq = dev.split("~");
                                                    device.get().reqGetIlluminanceLevel().reqGetInstallationLocation().
                                                            reqGetOperationStatus().reqGetMacAddress().reSend(Short.parseShort(tidReq[1]));
                                                    System.out.println("Loss Packet");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, timeCheck);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },0, TimeRequest.lightingDevice);
    }
}
