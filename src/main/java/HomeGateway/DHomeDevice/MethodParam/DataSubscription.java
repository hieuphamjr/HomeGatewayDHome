package main.java.HomeGateway.DHomeDevice.MethodParam;

public class DataSubscription {
    private final String token;
    private final String userId;
    private final String userPass;

    public DataSubscription(String token, String userId, String userPass) {
        this.token = token;
        this.userId = userId;
        this.userPass = userPass;
    }
}
