package main.java.HomeGateway.EchonteLite.SensorsProcess;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.EchoUtils;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.sensor.TemperatureSensor;
import main.java.HomeGateway.EchonteLite.CheckReliable;
import main.java.HomeGateway.EchonteLite.TimeRequest;
import main.java.HomeGateway.MqttBroker.DataTransfer;
import main.java.HomeGateway.MqttBroker.MqttConnection;
import main.java.HomeGateway.MqttBroker.TopicDevices;
import main.java.HomeGateway.Payload.TemperaturePayload;
import org.json.JSONException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureProcess {
    private int countPayload = 0;
    private DataTransfer data = new DataTransfer();
    private CheckReliable checkReliable = new CheckReliable();

    public synchronized void temperatureSensor(TemperatureSensor device, TopicDevices topicDevices) {
        TemperaturePayload tempSensor = new TemperaturePayload();
        try {
            tempSensor.createMessage();
            tempSensor.setIPAddress(device.getNode().getAddress().getHostAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        device.setReceiver(new TemperatureSensor.Receiver() {
            @Override
            protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv,
                                                EchoProperty property, boolean success) {
                super.onGetOperationStatus(eoj, tid, esv, property, success);
                if (!success) {
                    tempSensor.alertSucessWrong();
                    return;
                }
                try {
//                                    System.out.println(property.edt[0]);
                    if (property.edt[0] == 48) {
                        tempSensor.setOperationStatus(true);
                    } else if (property.edt[0] == 49) {
                        tempSensor.setOperationStatus(false);
                    } else {
                        tempSensor.setOperationStatus(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onGetMeasuredTemperatureValue(EchoObject eoj, short tid, byte esv,
                                                         EchoProperty property, boolean success) {
                super.onGetMeasuredTemperatureValue(eoj, tid, esv, property, success);
                if (!success) {
                    tempSensor.alertSucessWrong();
                    return;
                }
                try {
                    ByteBuffer wrapped = ByteBuffer.wrap(property.edt); // big-endian by default
                    short num = wrapped.getShort();
                    int value = (int)num;
                    //tempSensor.setIlluminanceValue(value);
                    tempSensor.setIlluminanceValue(new Random().nextInt(100));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void onGetMacAddress(EchoObject eoj, short tid, byte esv,
                                           EchoProperty property, boolean success) {
                super.onGetMacAddress(eoj, tid, esv, property, success);
                if (!success) {
                    tempSensor.alertSucessWrong();
                    return;
                }
                String macAddress = EchoUtils.toHexString(property.edt[0])+":"+ EchoUtils.toHexString(property.edt[1])
                        +":"+EchoUtils.toHexString(property.edt[2])+":"+EchoUtils.toHexString(property.edt[3])
                        +":"+EchoUtils.toHexString(property.edt[4])+":"+EchoUtils.toHexString(property.edt[5]);
                device.setMacAddress(macAddress);
                try {
                    tempSensor.setMAC(macAddress);
                    topicDevices.myDevices.add(device);
                    if (topicDevices.checkTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                            device.getClassCode(), device.getInstanceCode())) {
                        data.sendMessageToBroker(MqttConnection.getMqttClientPub(),
                                topicDevices.getTopicForDevice(device.getMacAddress(), device.getClassGroupCode(),
                                        device.getClassCode(), device.getInstanceCode()) + "/data", tempSensor.getMessage());
                        countPayload ++;
                        String dev = macAddress + "/" + device.getClassGroupCode() + "/" + device.getClassCode() + "/" + device.getInstanceCode();
                        System.out.println("Payload Temperature: " + countPayload + " ---> " + "Device: " + dev);
                    } else {
                        //send request to register new device to MQTT Broker
                        if (!topicDevices.checkDeviceRegister(device.getMacAddress(), device.getClassGroupCode(),
                                device.getClassCode(), device.getInstanceCode())) {
                            data.sendMessageToBroker(MqttConnection.getMqttClientPub(), topicDevices.registerDeviceTopic(),
                                    topicDevices.registerDevicePayload(device.getMacAddress(), device.getClassGroupCode(),
                                            device.getClassCode(), device.getInstanceCode()));
                            topicDevices.isSendRegister(device.getMacAddress(), device.getClassGroupCode(),
                                    device.getClassCode(), device.getInstanceCode());
                            System.out.println("Register Temperature");
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
                            device.get().reqGetMeasuredTemperatureValue().reqGetInstallationLocation().
                                    reqGetOperationStatus().reqGetMacAddress().send();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },0, TimeRequest.temperatureTime);
    }
}
