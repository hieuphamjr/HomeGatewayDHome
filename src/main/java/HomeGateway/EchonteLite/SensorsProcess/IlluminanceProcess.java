package main.java.HomeGateway.EchonteLite.SensorsProcess;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.EchoUtils;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.sensor.IlluminanceSensor;
import main.java.Extensions.Extentions;
import main.java.HomeGateway.EchonteLite.CheckReliable;
import main.java.HomeGateway.EchonteLite.TimeRequest;
import main.java.HomeGateway.MqttBroker.DataTransfer;
import main.java.HomeGateway.MqttBroker.MqttConnection;
import main.java.HomeGateway.MqttBroker.TopicDevices;
import main.java.HomeGateway.Payload.IlluminancePayload;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class IlluminanceProcess {
    private int countPayload = 0;
    private DataTransfer data = new DataTransfer();
    private CheckReliable checkReliable = new CheckReliable();

    public synchronized void illuminanceSensor(IlluminanceSensor device, TopicDevices topicDevices) {
        IlluminancePayload illuSensor = new IlluminancePayload();
        try {
            illuSensor.createMessage();
            illuSensor.setIPAddress(device.getNode().getAddress().getHostAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        device.setReceiver(new IlluminanceSensor.Receiver() {
            @Override
            protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv,
                                                EchoProperty property, boolean success) {
                super.onGetOperationStatus(eoj, tid, esv, property, success);
                if (!success) {
                    illuSensor.alertSucessWrong();
                    return;
                }
                try {
//                                    System.out.println(property.edt[0]);
                    if (property.edt[0] == 48) {
                        illuSensor.setOperationStatus(true);
                    } else if (property.edt[0] == 49) {
                        illuSensor.setOperationStatus(false);
                    } else {
                        illuSensor.setOperationStatus(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGetMeasuredIlluminanceValue1(EchoObject eoj, short tid, byte esv,
                                                       EchoProperty property, boolean success) {
                super.onGetMeasuredIlluminanceValue1(eoj, tid, esv, property, success);
                if (!success) {
                    illuSensor.alertSucessWrong();
                    return;
                }
                try {
                    illuSensor.setIlluminanceValue(Extentions.ConvertByteToInt(property.edt));

                    System.out.println(Extentions.ConvertByteToInt(property.edt));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onGetMacAddress(EchoObject eoj, short tid, byte esv,
                                           EchoProperty property, boolean success) {
                super.onGetMacAddress(eoj, tid, esv, property, success);
                if (!success) {
                    illuSensor.alertSucessWrong();
                    return;
                }
                String macAddress = EchoUtils.toHexString(property.edt[0])+":"+ EchoUtils.toHexString(property.edt[1])
                        +":"+EchoUtils.toHexString(property.edt[2])+":"+EchoUtils.toHexString(property.edt[3])
                        +":"+EchoUtils.toHexString(property.edt[4])+":"+EchoUtils.toHexString(property.edt[5]);
                System.out.println(macAddress);
                System.out.println();
                device.setMacAddress(macAddress);
                try {
                    illuSensor.setMAC(macAddress);
                    //topicDevices.myDevices.add(device);
                    checkReliable.addMAC(device.getNode().getAddress().getHostAddress(), macAddress);
                    String dev = macAddress + "/" + device.getClassGroupCode() + "/" + device.getClassCode() + "/" + device.getInstanceCode() + "~" + tid;
                    checkReliable.addDeviceReceive(dev, "received");
                    if (topicDevices.checkTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                            device.getClassCode(), device.getInstanceCode())) {
                        data.sendMessageToBroker(MqttConnection.getMqttClientPub(),
                                topicDevices.getTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                                        device.getClassCode(), device.getInstanceCode()) + "/data", illuSensor.getMessage());
                        countPayload ++;

                        //System.out.println("Payload Illuminance: " + countPayload + " ---> " + "Device: " + dev);
                    } else {
                        //send request to register new device to MQTT Broker
                        if (!topicDevices.checkDeviceRegister(device.getMacAddress(), device.getClassGroupCode(),
                                device.getClassCode(), device.getInstanceCode())) {
                            data.sendMessageToBroker(MqttConnection.getMqttClientPub(), topicDevices.registerDeviceTopic(),
                                    topicDevices.registerDevicePayload(device.getMacAddress(), device.getClassGroupCode(),
                                            device.getClassCode(), device.getInstanceCode()));
                            topicDevices.isSendRegister(device.getMacAddress(), device.getClassGroupCode(),
                                    device.getClassCode(), device.getInstanceCode());
                            System.out.println("Register Illuminance");
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
                        short tidRequest = device.get().reqGetMeasuredIlluminanceValue1().reqGetInstallationLocation().
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
                                                checkReliable.removeDevicePacket(dev);
                                            } else {
                                                String[] tidReq = dev.split("~");
                                                device.get().reqGetMeasuredIlluminanceValue1().reqGetInstallationLocation().
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
            },0, TimeRequest.illuminanceTime);
    }
}
