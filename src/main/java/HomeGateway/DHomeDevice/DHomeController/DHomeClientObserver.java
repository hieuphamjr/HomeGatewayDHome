package main.java.HomeGateway.DHomeDevice.DHomeController;

import com.keysolutions.ddpclient.DDPClient;
import com.keysolutions.ddpclient.DDPListener;

import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Hieu
 *
 * DDP client observer that handles messages for DDP Client
 */
public class DHomeClientObserver extends DDPListener implements Observer {
    private final static Logger LOGGER = Logger.getLogger(DDPClient.class .getName());
    public enum DDPSTATE {
        Disconnected,
        Connected,
        LoggedIn,
        Closed,
    }

    public DDPSTATE mDdpState;
    public String mResumeToken;
    public String mUserId;
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

    public DHomeClientObserver() {
        mDdpState = DDPSTATE.Disconnected;
        mCollections = new HashMap<String, Map<String, Object>>();
    }

    @Override
    public void onResult(Map<String, Object> resultFields) {
    }

    @Override
    public void onNoSub(String id, Map<String, Object> error) {
        if (error != null) {
            mErrorCode = (int) Math.round((Double)error.get("error"));
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
                mCloseCode = Integer.parseInt(jsonFields.get(DDPClient.DdpMessageField.CODE).toString());
                mCloseReason = (String) jsonFields.get(DDPClient.DdpMessageField.REASON);
                mCloseFromRemote = (Boolean) jsonFields.get(DDPClient.DdpMessageField.REMOTE);
            }
            if (msgtype.equals(DDPClient.DdpMessageType.ADDED)) {
                String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                if (!mCollections.containsKey(collName)) {
                    // add new collection
                    System.out.println("Added collection " + collName);
                    mCollections.put(collName, new HashMap<String, Object>());
                }
                Map<String, Object> collection = mCollections.get(collName);
                String id = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
                System.out.println("Added " + collName + id + " to collection " + collName);
                collection.put(id, jsonFields.get(DDPClient.DdpMessageField.FIELDS));
                dumpMap((Map<String, Object>) jsonFields.get(DDPClient.DdpMessageField.FIELDS));
            }
            if (msgtype.equals(DDPClient.DdpMessageType.REMOVED)) {
                String collName = (String) jsonFields.get(DDPClient.DdpMessageField.COLLECTION);
                if (mCollections.containsKey(collName)) {
                    // remove IDs from collection
                    Map<String, Object> collection = mCollections.get(collName);
                    String docId = (String) jsonFields.get(DDPClient.DdpMessageField.ID);
                    LOGGER.fine("Removed doc: " + collName + docId);
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
                        }
                        // take care of clearing fields
                        List<String> clearfields = ((List<String>) jsonFields.get(DDPClient.DdpMessageField.CLEARED));
                        if (clearfields != null) {
                            for (String fieldname : clearfields) {
                                if (doc.containsKey(fieldname)) {
                                    doc.remove(fieldname);
                                }
                            }
                        }
                    }
                    System.out.println(collName + docId + " changed");
                    dumpMap((Map<String, Object>) jsonFields.get(DDPClient.DdpMessageField.FIELDS));
                } else {
                    LOGGER.warning("Received invalid changed msg for collection " + collName);
                }
            }
        }
    }
    public static void dumpMap(Map<String, Object> result) {
        for (String key : result.keySet()) {
            System.out.println(key + ": " + result.get(key).toString());
        }
        System.out.println("++++++++++");
    }
}

