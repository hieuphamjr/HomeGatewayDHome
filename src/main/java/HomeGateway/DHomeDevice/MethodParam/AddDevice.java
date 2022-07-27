package main.java.HomeGateway.DHomeDevice.MethodParam;

public class AddDevice {
    private final String name;
    private final String description;
    private final Integer type;
    private final Integer idx;
    private final Integer netadd;
    private final Integer endpoint;
    private final Integer groupId;
    private final Integer icon;

    public AddDevice(String name, Integer type, Integer idx, Integer netadd, Integer endpoint, Integer groupId) {
        this.name = name;
        this.description = "Description";
        this.type = type;
        this.idx = idx;
        this.netadd = netadd;
        this.endpoint = endpoint;
        this.groupId = groupId;
        this.icon = 0;
    }
}
