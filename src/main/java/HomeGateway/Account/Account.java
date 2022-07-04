package main.java.HomeGateway.Account;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

public class Account {
    private String path = "src/data/InforAccount.txt";

    public ArrayList<String> getInforAccount() {
        try {
            File f = new File(path);
            ArrayList<String> infor = (ArrayList<String>) Files.readAllLines(f.toPath());
            return infor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setInforAccount(String username, String password, String homeId) {
        try {
            File f = new File(path);
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(username.getBytes());
            fos.write(password.getBytes());
            fos.write(homeId.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return getInforAccount().get(0);
    }

    public String getPassword() {
        return getInforAccount().get(1);
    }

    public String getHomeId() {
        return getInforAccount().get(2);
    }
}