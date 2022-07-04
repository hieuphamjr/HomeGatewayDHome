package main.java.HomeGateway.Payload;

import org.json.JSONException;
import org.json.simple.JSONObject;

public class HumidityPayload {
    private String MAC = "00:00:00:00:00:00";
    private String IPAddress = "null";
    private boolean operationStatus = true;
    private int illuminanceValue = 0;
    private String installationLocation = "null";
    private JSONObject jsonMessage = new JSONObject();

    public void createMessage() throws JSONException {
        jsonMessage.put("MAC", MAC);
        jsonMessage.put("IPAddress", IPAddress);
        jsonMessage.put("OperationStatus", operationStatus);
        jsonMessage.put("IlluminanceValue", illuminanceValue);
    }

    public String getMessage() {
        return jsonMessage.toString();
    }

    public void setMAC(String MAC) throws JSONException {
        this.MAC = MAC;
        jsonMessage.remove("MAC");
        jsonMessage.put("MAC", this.MAC);
    }

    public void setIPAddress(String IPAddress) throws JSONException {
        this.IPAddress = IPAddress;
        jsonMessage.remove("IPAddress");
        jsonMessage.put("IPAddress", this.IPAddress);
    }

    public void setIlluminanceValue(int illuminanceValue) throws JSONException {
        this.illuminanceValue = illuminanceValue;
        jsonMessage.remove("IlluminanceValue");
        jsonMessage.put("IlluminanceValue", this.illuminanceValue);
    }

    public void setOperationStatus(boolean operationStatus) throws JSONException {
        this.operationStatus = operationStatus;
        jsonMessage.remove("OperationStatus");
        jsonMessage.put("OperationStatus", this.operationStatus);
    }

    public void alertSucessWrong() {
        System.out.println("Can't get any value");
    }
}
