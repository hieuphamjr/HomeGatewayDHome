package main.java.HomeGateway.EchonteLite;

import com.sonycsl.echo.EchoSocket;
import main.java.Extensions.Extentions;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

public class UDPReceiveMessage {
    private final int port = 3610;
    public int UDP_MAX_PACKET_SIZE = 10;
    private CheckReliable checkReliable;
    private MulticastSocket multicastSocket;
    private InetAddress inetAddress;
    private DatagramPacket packet;
    private String mesageFromDevice = "";
    private String device;

    public void setDevice(String device) {
        this.device = device + "/receive";
    }

    public void openConnect() {
        try {
            inetAddress = InetAddress.getByName(EchoSocket.MULTICAST_ADDRESS);
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(inetAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveMessageFromDevice(int size) throws Exception {
        byte[] temp = new byte[size];
        packet = new DatagramPacket(temp, temp.length);
        packet.setLength(packet.getData().length);
        multicastSocket.receive(packet);
        String str = new String(packet.getData());
        System.out.println("Chuoi nhan tu client:  " + str + " --- " + "Time: " + Extentions.ConvertDateToString(Calendar.getInstance()));
        return str;
    }

    public void sendMessage() throws Exception {
        String str = device.replace("/receive", "");
        System.out.println("Send: " + str);
        DatagramPacket packet1=new DatagramPacket(str.getBytes(),str.length(), inetAddress, port);
        multicastSocket.send(packet1);
        checkReliableCommand();
    }

    public void closeConnect() {
        try {
            multicastSocket.leaveGroup(inetAddress);
            multicastSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkReliableCommand() {
        try {
            System.out.println("checkReliableCommand");
            Calendar calendarReq = Calendar.getInstance();
            calendarReq.add(Calendar.SECOND, 5);
            Date timeCheck = calendarReq.getTime();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mesageFromDevice.equals(device)) {
                        System.out.println("Done command");
                        //timer.cancel();
                    } else {
                        try {
                            System.out.println("Resend --> Loss Packet");
                            sendMessage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, timeCheck);

            System.out.println("Recieving message from: " + device);
            mesageFromDevice = receiveMessageFromDevice(device.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//
//        UDPReceiveMessage udp = new UDPReceiveMessage();
//        String dev1 = "5c:cf:7f:34:35:ce/0/17/1";
//        udp.openConnect();
//        udp.setDevice(dev1);
//        udp.checkReliableCommand();
//
//
////        Timer timeRequest = new Timer();
////
////        timeRequest.scheduleAtFixedRate(
////                new TimerTask() {
////                    public void run() {
////                        try {
////                            System.out.println("Loading.......");
////                            udp.mes = udp.receiveMessageFromDevice();
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////                    }
////                },0, 1000);
//    }
}
