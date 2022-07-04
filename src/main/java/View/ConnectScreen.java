//package main.java.View;
//
//import main.java.HomeGateway.Account.Account;
//import main.java.HomeGateway.MqttBroker.MqttConnection;
//
//import javax.swing.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class ConnectScreen extends JFrame{
//    private JButton btnConnect;
//    private JPasswordField password;
//    private JTextField username;
//    private JPanel MainPanel;
//    private JLabel passwordLabel;
//
//    public ConnectScreen(String title) {
//        super(title);
//        this.setContentPane(this.MainPanel);
//        this.setDefaultCloseOperation(3);
//        this.pack();
//        btnConnect.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
//                super.mouseClicked(e);
//                MqttConnection mqttConnect = new MqttConnection();
//                //mqttConnect.ConnectMqttBroker(username.getText(), password.getText());
//                Account account = new Account();
//            }
//        });
//    }
//
//    private void createUIComponents() {
//        // TODO: place custom component creation code here
//    }
//}
