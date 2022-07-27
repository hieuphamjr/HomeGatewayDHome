package main.java.HomeGateway.EchonteLite;

import com.sonycsl.echo.Echo;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;
import com.sonycsl.echo.eoj.device.sensor.IlluminanceSensor;
import com.sonycsl.echo.eoj.profile.NodeProfile;
import com.sonycsl.echo.processing.defaults.DefaultController;
import com.sonycsl.echo.processing.defaults.DefaultNodeProfile;
import main.java.HomeGateway.EchonteLite.DeviceProcess.LightingProcess;
import main.java.HomeGateway.EchonteLite.SensorsProcess.IlluminanceProcess;
import main.java.HomeGateway.MqttBroker.TopicDevices;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class EchonetLiteController {
    private final int countPayload = 0;
    private final CheckReliable checkReliable = new CheckReliable();

    public EchonetLiteController(TopicDevices topicDevices) {
        //Sensor thread
        Thread sensorsThead = new Thread(new Runnable() {
            @Override
            public void run() {
                Echo.addEventListener(new Echo.EventListener() {
                    //Illuminance Sensor
                    @Override
                    public void onNewIlluminanceSensor(IlluminanceSensor device) {
                        super.onNewIlluminanceSensor(device);
                        System.out.println("New Illuminance device");
                        topicDevices.myDevices.add(device);
                        IlluminanceProcess illuminanceProcess = new IlluminanceProcess();
                        illuminanceProcess.illuminanceSensor(device, topicDevices);
                    }
//                    //Temperature Sensor
//                    @Override
//                    public void onNewTemperatureSensor(TemperatureSensor device) {
//                        super.onNewTemperatureSensor(device);
//                        System.out.println("New Temperature device");
//                        TemperatureProcess temperatureProcess = new TemperatureProcess();
//                        temperatureProcess.temperatureSensor(device, topicDevices);
//                    }
//
//                    //Humidity Sensor
//                    @Override
//                    public void onNewHumiditySensor(HumiditySensor device) {
//                        super.onNewHumiditySensor(device);
//                        System.out.println("New Humidity device");
//                        HumidityProcess humidityProcess = new HumidityProcess();
//                        humidityProcess.humiditySensor(device, topicDevices);
//                    }
                });
            }
        });
        sensorsThead.start();

        Thread deviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Echo.addEventListener(new Echo.EventListener() {
                    //Lighting Device
                    @Override
                    public void onNewGeneralLighting(GeneralLighting device) {
                        super.onNewGeneralLighting(device);
                        System.out.println("New Lighting device");
                        topicDevices.myDevices.add(device);
                        LightingProcess lightingProcess = new LightingProcess();
                        lightingProcess.lightingDevice(device, topicDevices, checkReliable);
                    }
                });
            }
        });
        deviceThread.start();

        Timer timeRequest = new Timer();
        timeRequest.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        try {
                            Echo.start(new DefaultNodeProfile(), new DeviceObject[]{new DefaultController()});
                            NodeProfile.informG().reqInformInstanceListNotification().send();
                            System.out.println("Searching Device in Home ........................................");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, TimeRequest.homeGatewayTime);
    }
}