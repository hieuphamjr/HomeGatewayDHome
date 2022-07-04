package main.java.HomeGateway.EchonteLite.SensorsProcess;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.EchoUtils;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.sensor.HumiditySensor;
import main.java.HomeGateway.EchonteLite.CheckReliable;
import main.java.HomeGateway.EchonteLite.TimeRequest;
import main.java.HomeGateway.MqttBroker.DataTransfer;
import main.java.HomeGateway.MqttBroker.MqttConnection;
import main.java.HomeGateway.MqttBroker.TopicDevices;
import main.java.HomeGateway.Payload.HumidityPayload;
import org.json.JSONException;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HumidityProcess {
    private int countPayload = 0;
    private DataTransfer data = new DataTransfer();
    private CheckReliable checkReliable = new CheckReliable();

    public synchronized void humiditySensor(HumiditySensor device, TopicDevices topicDevices) {
        HumidityPayload humiSensor = new HumidityPayload();
        try {
            humiSensor.createMessage();
            humiSensor.setIPAddress(device.getNode().getAddress().getHostAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        device.setReceiver(new HumiditySensor.Receiver() {
            @Override
            protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv,
                                                EchoProperty property, boolean success) {
                super.onGetOperationStatus(eoj, tid, esv, property, success);
                if (!success) {
                    humiSensor.alertSucessWrong();
                    return;
                }
                try {
//                                    System.out.println(property.edt[0]);
                    if (property.edt[0] == 48) {
                        humiSensor.setOperationStatus(true);
                    } else if (property.edt[0] == 49) {
                        humiSensor.setOperationStatus(false);
                    } else {
                        humiSensor.setOperationStatus(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void onGetMeasuredValueOfRelativeHumidity(EchoObject eoj, short tid, byte esv,
                                                                EchoProperty property, boolean success) {
                super.onGetMeasuredValueOfRelativeHumidity(eoj, tid, esv, property, success);
                if (!success) {
                    humiSensor.alertSucessWrong();
                    return;
                }
                try {
                    humiSensor.setIlluminanceValue(new Random().nextInt(100));
                    //.setIlluminanceValue(Integer.valueOf(property.edt[0]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void onGetMacAddress(EchoObject eoj, short tid, byte esv,
                                           EchoProperty property, boolean success) {
                super.onGetMacAddress(eoj, tid, esv, property, success);
                if (!success) {
                    humiSensor.alertSucessWrong();
                    return;
                }
                String macAddress = EchoUtils.toHexString(property.edt[0])+":"+ EchoUtils.toHexString(property.edt[1])
                        +":"+EchoUtils.toHexString(property.edt[2])+":"+EchoUtils.toHexString(property.edt[3])
                        +":"+EchoUtils.toHexString(property.edt[4])+":"+EchoUtils.toHexString(property.edt[5]);
                device.setMacAddress(macAddress);
//                System.out.println("Group: " + device.getClassGroupCode() + "-- Class: " + device.getClassCode() + "-- Instance: " + device.getInstanceCode());
                try {
                    humiSensor.setMAC(macAddress);
                    topicDevices.myDevices.add(device);
                    if (topicDevices.checkTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                            device.getClassCode(), device.getInstanceCode())) {
                        data.sendMessageToBroker(MqttConnection.getMqttClientPub(),
                                topicDevices.getTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                                        device.getClassCode(), device.getInstanceCode()) + "/data", humiSensor.getMessage());
                        countPayload ++;
                        String dev = macAddress + "/" + device.getClassGroupCode() + "/" + device.getClassCode() + "/" + device.getInstanceCode();
                        System.out.println("Payload Humidity: " + countPayload + " ---> " + "Device: " + dev);
                    } else {
                        //send request to register new device to MQTT Broker
                        if (!topicDevices.checkDeviceRegister(device.getMacAddress(), device.getClassGroupCode(),
                                device.getClassCode(), device.getInstanceCode())) {
                            data.sendMessageToBroker(MqttConnection.getMqttClientPub(), topicDevices.registerDeviceTopic(),
                                    topicDevices.registerDevicePayload(device.getMacAddress(), device.getClassGroupCode(),
                                            device.getClassCode(), device.getInstanceCode()));
                            topicDevices.isSendRegister(device.getMacAddress(), device.getClassGroupCode(),
                                    device.getClassCode(), device.getInstanceCode());
                            System.out.println("Register Humidity");
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
                            device.get().reqGetMeasuredValueOfRelativeHumidity().reqGetInstallationLocation().
                                    reqGetOperationStatus().reqGetMacAddress().send();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },0, TimeRequest.illuminanceTime);
    }
}
