package main.java.HomeGateway.DHomeDevice.DHomeController;


import main.java.HomeGateway.DHomeDevice.MethodParam.AddDevice;
import main.java.HomeGateway.DHomeDevice.MethodParam.AddGroup;
import main.java.HomeGateway.DHomeDevice.MethodParam.Com;
import main.java.HomeGateway.DHomeDevice.MethodParam.RemoveDSwitch;

public class DHomeController {
    private final DHomeConnection mDHomeConnection;

    public DHomeController(DHomeConnection mDHomeConnection) {
        this.mDHomeConnection = mDHomeConnection;
    }


    public void turnOn(Integer id) {
        Object[] param = new Object[1];
        param[0] = new Com(id, "on");
        mDHomeConnection.getDdpClient().call("com", param);
    }

    public void turnOff(Integer id) {
        Object[] param = new Object[1];
        param[0] = new Com(id, "off");
        mDHomeConnection.getDdpClient().call("com", param);
    }

    public void toggle(Integer id) {
        Object[] param = new Object[1];
        param[0] = new Com(id, "toggle");
        mDHomeConnection.getDdpClient().call("com", param);
    }

    public void curtainUp(Integer id) {
        mDHomeConnection.getDdpClient().call("curtainUp", new Object[]{id});
    }

    public void curtainDown(Integer id) {
        mDHomeConnection.getDdpClient().call("curtainDown", new Object[]{id});
    }

    public void curtainStop(Integer id) {
        mDHomeConnection.getDdpClient().call("curtainStop", new Object[]{id});
    }

    public void addSwitch() throws InterruptedException {
        mDHomeConnection.getDdpClient().call("turnOnPairingMode", new Object[]{});
        Thread.sleep(1000);
    }

    public void removeDevice(Integer id, Integer netAdd) throws InterruptedException {
        Object[] param = new Object[1];
        param[0] = new RemoveDSwitch(id, netAdd);
        mDHomeConnection.getDdpClient().call("removeSigleDevice", param);
        Thread.sleep(1000);
    }

    public void addRoom(String name) throws InterruptedException {
        Object[] param = new Object[1];
        param[0] = new AddGroup(name, 0);
        mDHomeConnection.getDdpClient().call("addGroup", param);
        Thread.sleep(1000);
    }

    public void removeRoom(Integer id) throws InterruptedException {
        mDHomeConnection.getDdpClient().call("removeGroup", new Object[]{id});
        Thread.sleep(1000);
    }

    public void addDevice(String name, Integer type, Integer idx, Integer netadd, Integer endpoint, Integer groupId, DHomeClientObserver obs) throws InterruptedException {
        Object[] param = new Object[1];
        param[0] = new AddDevice(name, type, idx, netadd, endpoint, groupId);
        mDHomeConnection.getDdpClient().call("addDevice", param, obs);
        Thread.sleep(1000);
    }
}
