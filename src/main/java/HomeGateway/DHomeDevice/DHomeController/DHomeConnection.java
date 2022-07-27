package main.java.HomeGateway.DHomeDevice.DHomeController;

import com.keysolutions.ddpclient.DDPClient;
import main.java.HomeGateway.DHomeDevice.DHome;
import main.java.HomeGateway.DHomeDevice.MethodParam.Com;
import main.java.HomeGateway.DHomeDevice.MethodParam.DataSubscription;

import java.net.URISyntaxException;

public class DHomeConnection {
    private DDPClient ddpClient;
    public DHomeConnection() {
    }

    public DDPClient getDdpClient() {
        return this.ddpClient;
    }

    public void connectDDP(DHome dhome, DHomeClientObserver obs) throws InterruptedException, NullPointerException, URISyntaxException {
        ddpClient = new DDPClient(dhome.getHost(), dhome.getDdpPort());
        ddpClient.addObserver(obs);
        ddpClient.connect();
        Thread.sleep(500);
        System.out.println(ddpClient.getState() + " to controller server " + dhome.getHost() + ":" + dhome.getDdpPort());
    }

    public void disconnectDDP() {
        ddpClient.disconnect();
    }

    public void reconnectDDP(DHome dhome, DHomeClientObserver obs) throws URISyntaxException, InterruptedException {
        if (this.ddpClient.getState().equals(DDPClient.CONNSTATE.Connected)) {
            disconnectDDP();
            connectDDP(dhome,obs);
        }
    }

    public void subscribe(DHomeClientObserver obs) throws InterruptedException {
        Object[] dataSubscription = new Object[1];
        dataSubscription[0] = new DataSubscription("MZfihHlV6C", "1", "123456");
        ddpClient.subscribe("data", dataSubscription, obs);
        Thread.sleep(200);
        System.out.println("Subscribed to DHome data");
    }

}
