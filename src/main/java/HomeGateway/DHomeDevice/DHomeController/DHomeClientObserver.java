package main.java.HomeGateway.DHomeDevice.DHomeController;

import com.keysolutions.ddpclient.DDPClient;
import com.keysolutions.ddpclient.DDPListener;
import main.java.Extensions.Extensions;
import main.java.HomeGateway.MessageToBroker.ControlDeviceMessage;
import main.java.HomeGateway.MessageToBroker.NewDeviceMessage;
import main.java.HomeGateway.MqttBroker.MqttClientPub;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static main.java.Extensions.Extensions.getTopicReceiver;
import static main.java.HomeGateway.ConfigHomeGateway.controlTopic;
import static main.java.HomeGateway.ConfigHomeGateway.newDeviceTopic;

/**
 * @author Hieu
 * <p>
 * DDP client observer that handles messages for DDP Client
 */
public class DHomeClientObserver extends DDPListener implements Observer {
    private final static Logger LOGGER = Logger.getLogger(DDPClient.class.getName());

    public enum DDPSTATE {
        Disconnected,
        Connected,
        LoggedIn,
        Closed,
    }

    public DDPSTATE mDdpState;
    public Double devId;
    public int mErrorCode;
    public String mErrorType;
    public String mErrorReason;
    public String mErrorMsg;
    public String mErrorSource;
    public String mSessionId;
    public int mCloseCode;
    public String mCloseReason;
    public boolean mCloseFromRemote;
    public Map<String, Map<String, Object>> mCollections;
    public String mReadySubscription;
    public String mPingId;

    private MqttClientPub mqttClientPub;

    public DHomeClientObserver() {
        mDdpState = DDPSTATE.Disconnected;
        mCollections = new HashMap<>();
        try {
            mqttClientPub = new MqttClientPub();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResult(Map<String, Object> resultFields) {
        if (resultFields.containsKey("result")) {
            Map<String, Object> result = (Map<String, Object>) resultFields.get(DDPClient.DdpMessageField.RESULT);
            devId = Double.parseDouble(result.get("devId").toString());
        }
        if (resultFields.containsKey("error")) {
            Map<String, Object> error = (Map<String, Object>) resultFields.get(DDPClient.DdpMessageField.ERROR);
            mErrorCode = (int) Math.round((Double) error.get("error"));
            mErrorMsg = (String) error.get("message");
            mErrorType = (String) error.get("errorType");
            mErrorReason = (String) error.get("reason");
        }
    }

    @Override
    public void onNoSub(String id, Map<String, Object> error) {
        if (error != null) {
            mErrorCode = (int) Math.round((Double) error.get("error"));
            mErrorMsg = (String) error.get("message");
            mErrorType = (String) error.get("errorType");
            mErrorReason = (String) error.get("reason");
            LOGGER.warning(mErrorMsg);
        } else {
            // if there's no error, it just means a subscription was unsubscribed
            mReadySubscription = null;
        }
    }

    @Override
    public void onReady(String id) {
        mReadySubscription = id;
    }

    @Override
    public void onPong(String id) {
        mPingId = id;
    }

    @Override
    public void update(Observable client, Object msg) {
        if (msg instanceof Map<?, ?>) {
            Map<String, Object> jsonFields = (Map<String, Object>) msg;
            String msgtype = (String) jsonFields.get(DDPClient.DdpMessageField.MSG);
            if (msgtype == null) {
                return;
            }
            if (msgtype.equals(DDPClient.DdpMessageType.ERROR)) {
                mErrorSource = (String) jsonFields.get(DDPClient.DdpMessageField.SOURCE);
                mErrorMsg = (String) jsonFields.get(DDPClient.DdpMessageField.ERRORMSG);
            }
            if (msgtype.equals(DDPClient.DdpMessageType.CONNECTED)) {
                mDdpState = DHomeClientObserver.DDPSTATE.Connected;
                mSessionId = (String) jsonFields.get(DDPClient.DdpMessageField.SESSION);
            }
            if (msgtype.equals(DDPClient.DdpMessageType.CLOSED)) {
                mDdpState = DHomeClientObserver.DDPSTATE.Closed;
                mCloseCode = parseInt(jsonFields.get(DDPClient.DdpMessageField.CODE).toString());
                mCloseReason = (String) jsonFields.get(DDPClient.DdpMessageField.REASON);
                mCloseFromRemote = (Boolean) jsonFields.get(DDPClient.DdpMessageField.REMOTE);
            }
            if (msgtype.equals(DDPClient.DdpMessageType.ADDED)) {
                String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                if (!mCollections.containsKey(collName)) {
                    mCollections.put(collName, new HashMap<>());
                }
                Map<String, Object> collection = mCollections.get(collName);
                String id = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
                collection.put(id, jsonFields.get(DDPClient.DdpMessageField.FIELDS));
                if (jsonFields.get(DDPClient.DdpMessageField.COLLECTION).toString().equals("device")) {
                    Object device = jsonFields.get(DDPClient.DdpMessageField.FIELDS);
                    NewDeviceMessage message = new NewDeviceMessage(device);
                    MqttMessage mqttMessage = new MqttMessage(message.createMessage().getBytes(StandardCharsets.UTF_8));
                    mqttMessage.setQos(0);
                    try {
                        mqttClientPub.getMqttClientPub().publish(newDeviceTopic, mqttMessage);
                        System.out.println("Published message to Broker, " + newDeviceTopic + " at " + System.currentTimeMillis());
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (msgtype.equals(DDPClient.DdpMessageType.REMOVED)) {
                String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                if (mCollections.containsKey(collName)) {
                    // remove IDs from collection
                    Map<String, Object> collection = mCollections.get(collName);
                    String docId = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
//                    LOGGER.fine("Removed doc: " + collName + docId);
                    collection.remove(docId);
                } else {
                    LOGGER.warning("Received invalid removed msg for collection " + collName);
                }
            }
            if (msgtype.equals(DDPClient.DdpMessageType.CHANGED)) {
                // handle document updates
                String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                if (mCollections.containsKey(collName)) {
                    Map<String, Object> collection = mCollections.get(collName);
                    String docId = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
                    Map<String, Object> doc = (Map<String, Object>) collection.get(docId);
                    if (doc != null) {
                        // take care of field updates
                        Map<String, Object> fields = (Map<String, Object>) jsonFields.get(DDPClient.DdpMessageField.FIELDS);
                        if (fields != null) {
                            for (Map.Entry<String, Object> field : fields.entrySet()) {
                                String fieldname = field.getKey();
                                doc.put(fieldname, field.getValue());
                            }
                            if (collName.equals("device")) {
                                int deviceId = Extensions.stringToId(docId);
                                double status = Double.parseDouble(fields.get("status").toString());
                                int devStatus = 0;
                                if (status == 49.0) {
                                    devStatus = 1;
                                }
                                ControlDeviceMessage message = new ControlDeviceMessage(deviceId, devStatus);
                                MqttMessage mqttMessage = new MqttMessage(message.createMessage().getBytes(StandardCharsets.UTF_8));
                                mqttMessage.setQos(0);
//                                try {
//                                    String topic = getTopicReceiver(controlTopic);
//                                    mqttClientPub.getMqttClientPub().publish(topic, mqttMessage);
//                                    System.out.println("Published message to Broker, topic: " + topic + " at " + System.currentTimeMillis());
//                                } catch (MqttException e) {
//                                    throw new RuntimeException(e);
//                                }
                            }
                        }
                        // take care of clearing fields
                        List<String> clearfields = ((List<String>) jsonFields.get(DDPClient.DdpMessageField.CLEARED));
                        if (clearfields != null) {
                            for (String fieldname : clearfields) {
                                doc.remove(fieldname);
                            }
                        }
                    }
                } else {
                    LOGGER.warning("Received invalid changed msg for collection " + collName);
                }
            }
        }
    }
}

