package main.java.HomeGateway;

import java.util.Arrays;
import java.util.List;

public class ConfigHomeGateway {
    public static String brokerURL = "ssl://2d471e79b9804f9bb726fb5249fddb0c.s1.eu.hivemq.cloud:8883";
    public static String brokerURL2 = "tcp://dhome.cloud.shiftr.io:1883";
    public static String username = "hieuphamjr";
    public static String username2 = "dhome";
    public static String password = "hieujr123";
    public static String password2 = "EqcArf55durZsTQi";
    public static String controlTopic = "/MZfihHlV6C/control/s";
    public static String addDeviceTopic = "/MZfihHlV6C/data/addNewDevice/s";
    public static String addGroupTopic = "/MZfihHlV6C/data/addNewGroup/s";
    public static String deleteGroupTopic = "/MZfihHlV6C/data/deleteGroup/s";
    public static String getListTopic = "/MZfihHlV6C/data/getList/s";
    public static String configDeviceTopic = "/MZfihHlV6C/data/configDevice/s";
    public static String deleteDeviceTopic = "/MZfihHlV6C/data/deleteDevice/s";

    public static List<String> subscribeTopics = Arrays.asList(
            controlTopic,
            addDeviceTopic,
            addGroupTopic,
            deleteGroupTopic,
            getListTopic,
            configDeviceTopic,
            deleteDeviceTopic
    );
}
