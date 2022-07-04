package main.java.HomeGateway.DHomeDevice.DHomeController;

import com.keysolutions.ddpclient.DDPClient;
import main.java.HomeGateway.DHomeDevice.MethodParam.com;
import main.java.HomeGateway.DHomeDevice.MethodParam.removeDSwitch;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Logger;

public class DHomeController {
    private DHomeConnection mDHomeConnection;

    public DHomeController(DHomeConnection mDHomeConnection) {
        this.mDHomeConnection = mDHomeConnection;
    }


    public void turnOn (Integer id){
        Object[] param = new Object[1];
        param[0] = new com(id, "on");
        mDHomeConnection.getDdpClient().call("com", param);
    }

    public void turnOff (Integer id){
        Object[] param = new Object[1];
        param[0] = new com(id, "off");
        mDHomeConnection.getDdpClient().call("com", param);
    }

    public void toggle (Integer id){
        Object[] param = new Object[1];
        param[0] = new com(id, "toggle");
        mDHomeConnection.getDdpClient().call("com", param);
    }

    public void curtainUp (Integer id) {
        mDHomeConnection.getDdpClient().call("curtainUp", new Object[]{id});
    }

    public void curtainDown (Integer id) {
        mDHomeConnection.getDdpClient().call("curtainDown", new Object[]{id});
    }

    public void curtainStop (Integer id) {
        mDHomeConnection.getDdpClient().call("curtainStop", new Object[]{id});
    }

    public void addSwitch () {
        mDHomeConnection.getDdpClient().call("turnOnPairingMode", new Object[]{}, new DHomeClientObserver(){
            @Override
            public void update(Observable client, Object msg) {
                if (msg instanceof Map<?, ?>) {
                    Map<String, Object> jsonFields = (Map<String, Object>) msg;
                    String msgtype = (String) jsonFields.get(DDPClient.DdpMessageField.MSG);
                    if ((msgtype.equals(DDPClient.DdpMessageType.ADDED))) {
                        String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                        if (!mCollections.containsKey("device")) {
                            // add new collection
                            System.out.println("New DSwitch added:");
                            mCollections.put(collName, new HashMap<String, Object>());
                            Map<String, Object> collection = mCollections.get("device");
                            String id = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
                            System.out.println("Added DSwitch " + id + " to collection " + collName);
                            collection.put(id, jsonFields.get(DDPClient.DdpMessageField.FIELDS));
                            dumpMap((Map<String, Object>) jsonFields.get(DDPClient.DdpMessageField.FIELDS));
                            System.out.println("---------------");
                        }
                    }
                }
            }
        });
    }

    public void removeSwitch(Integer id, Integer netAdd) {
        Object[] param = new Object[1];
        param[0] = new removeDSwitch(id, netAdd);
        mDHomeConnection.getDdpClient().call("removeDSwitch", param, new DHomeClientObserver() {
            private final Logger LOGGER = Logger.getLogger(DDPClient.class .getName());
            @Override
            public void update(Observable client, Object msg) {
                if (msg instanceof Map<?, ?>) {
                    Map<String, Object> jsonFields = (Map<String, Object>) msg;
                    String msgtype = (String) jsonFields.get(DDPClient.DdpMessageField.MSG);
                    if (msgtype.equals(DDPClient.DdpMessageType.REMOVED)) {
                        String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                        if (mCollections.containsKey(collName)) {
                            // remove IDs from collection
                            Map<String, Object> collection = mCollections.get(collName);
                            String docId = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
                            System.out.println(collName + docId + "removed");
                            collection.remove(docId);
                        } else {
                            LOGGER.warning("Received invalid removed msg for collection " + collName);
                        }
                    }
                }
            }
        });
    }
}
