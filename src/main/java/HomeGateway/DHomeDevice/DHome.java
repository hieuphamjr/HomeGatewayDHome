package main.java.HomeGateway.DHomeDevice;

public class DHome {
    private String host;
    private Integer ddpPort;

    public DHome(String host) {
        this.host = host;
        this.ddpPort = 7777;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getDdpPort() {
        return ddpPort;
    }

    public void setDdpPort(Integer ddpPort) {
        this.ddpPort = ddpPort;
    }

}
