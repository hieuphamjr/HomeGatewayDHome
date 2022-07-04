package main.java.HomeGateway.DHomeDevice.DHomeController;

import com.keysolutions.ddpclient.DDPClient;
import main.java.HomeGateway.DHomeDevice.DHome;
import main.java.HomeGateway.DHomeDevice.MethodParam.DataSubscription;
import main.java.HomeGateway.DHomeDevice.MethodParam.com;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Logger;

public class DHomeConnection {
    private DDPClient ddpClient;
    public DHomeConnection() {
    }

//    public DHomeConnection(DDPClient ddpClient) {
//        this.ddpClient = ddpClient;
//    }

    public DDPClient getDdpClient() {
        return this.ddpClient;
    }

    public void connectDDP(DHome dhome, DHomeClientObserver obs) throws InterruptedException, NullPointerException, URISyntaxException {
        ddpClient = new DDPClient(dhome.getHost(), dhome.getDdpPort());
        ddpClient.addObserver(obs);
        ddpClient.connect();
        Thread.sleep(500);
        System.out.println(ddpClient.getState() + " to DHome server " + dhome.getHost() + ":" + dhome.getDdpPort());
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

    public int subscribe(DHomeClientObserver obs){
        System.out.println("Subscribed to DHome data");
        Object[] dataSubscription = new Object[1];
        dataSubscription[0] = new DataSubscription("MZfihHlV6C", "1", "123456");
        return ddpClient.subscribe("data", dataSubscription, obs);
    }

    public void unsubscribed(int subId, DHomeClientObserver obs) {
        System.out.println("Unsubscribed to DHome data");
        ddpClient.unsubscribe(this.subscribe(obs));
    }

}
