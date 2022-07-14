package main.java.HomeGateway.DHomeDevice.MethodParam;

public class AddDevice {
    private String name;
    private String description;
    private Integer type;
    private Integer idx;
    private Integer netadd;
    private Integer endpoint;
    private Integer groupId;
    private Integer icon;

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
